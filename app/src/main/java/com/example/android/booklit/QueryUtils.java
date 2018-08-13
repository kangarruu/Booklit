package com.example.android.booklit;

//Helper methods related to requesting and receiving article data from the Guardian API

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //Create a private constructor
    private QueryUtils(){
    }

    //Method for converting the API string into a URL
    private static URL createUrl (String queryString){
        URL url = null;
        try {
            url = new URL(queryString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error converting String to URL",e);
        }
        return url;
    }

    //Method for making an HTTP request to the server.
    //@param URL created in createUrl()
    //@return a String containing the JSON response from the queried URL
    private static String makeHttpRequest(URL url) throws IOException{
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String jsonResponseString = " ";

        //if url is null, return the empty jsonResponseString
        if (url == null){
            return jsonResponseString;
        }

        //try opening an HttpURLConnection.
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

             //if the connection was successful request the inputStream and proceed with parsing it in readFromStream()
             //if there was an error connecting, log the error
             if (urlConnection.getResponseCode() == 200){
                 inputStream = urlConnection.getInputStream();
                 jsonResponseString = readFromStream(inputStream);
             } else {
                 Log.e(LOG_TAG, "UrlConnection.getResponseCode(): " + urlConnection.getResponseCode());
             }

        } catch (IOException e) {
           Log.e(LOG_TAG, "Error opening connection in Url.openConnection()", e);
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){               //Closing stream can throw a JSON Exception
                inputStream.close();                //Declare exception in method declaration
            }
        }
        return jsonResponseString;
    }

    //Method for parsing the InputStream and converting it into a String
    //@param an InputStream requested in makeHttpRequest()
    //@return a String containing the content of the InputStream
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String nextLine = bufferedReader.readLine();
            while (nextLine != null){
                output.append(nextLine);
                nextLine = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    //Method for parsing a String JSON response and extracting the necessary elements
    //@param the String containing JSON response returned in makeHttpRequest()
    //@return an ArrayList containing articles received from the url
    private static ArrayList<Article> extractFromJson(String jsonString){
        if(TextUtils.isEmpty(jsonString)){
            return null;
        }

        //Create an empty ArrayList to add Article objects to
        ArrayList<Article> articles = new ArrayList<>();

        //try parsing the response by locating the root JSON object and the JSONArray containing the results
        try{
            JSONObject baseJsonObject = new JSONObject(jsonString);
            JSONObject jsonResponse = baseJsonObject.getJSONObject("response");
            JSONArray resultsArray = jsonResponse.getJSONArray("results");

            //iterate through baseJsonArray, and extract the relevant Strings
            //Learned to differentiate between getString and optString from:
            // https://stackoverflow.com/questions/13790726/the-difference-between-getstring-and-optstring-in-json
            for(int i = 0; i < resultsArray.length(); i++ ){
                JSONObject currentArticle = resultsArray.getJSONObject(i);
                String sectionName = currentArticle.getString("sectionName");
                String webPublicationDate = currentArticle.getString("webPublicationDate");
                String webTitle = currentArticle.getString("webTitle");
                JSONObject fieldsObject = currentArticle.getJSONObject("fields");
                String trailText = fieldsObject.optString("trailText");
                String byline = fieldsObject.optString("byline");
                String shortUrl = fieldsObject.optString("shortUrl");
                String thumbnail = fieldsObject.optString("thumbnail");

                //Convert the thumbnail URL to a bitmap
                //Learned how to decode a bitmap from a url from here: https://stackoverflow.com/questions/11831188/how-to-get-bitmap-from-a-url-in-android
                Bitmap image = null;
             try {
                 if (thumbnail != null) {
                     URL imageUrl = new URL(thumbnail);
                     image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                 }
             } catch (MalformedURLException e) {
                 Log.e(LOG_TAG, "URL error when converting image URL to Bitmap", e);
             } catch (IOException e) {
                 Log.e(LOG_TAG, "Error when opening connection when converting URL to Bitmap", e);
             }

                //Create a new Article object and pass in the extracted variables
                Article article = new Article(byline, webTitle, trailText,webPublicationDate,
                        sectionName, shortUrl, image );
                articles.add(article);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error extracting JSON objects in extractFromJson(): ",e);
        }
        return articles;

    }

    //Method that combines all the helper methods to create and query the URL and return the list of Articles
    //@param the Guardian API String
    //@return a List of Article objects for the ArticleAdapter to display
    public static List<Article> fetchArticles(String queryString) {
        //Create a URL object from the input String
        URL guardianUrl = createUrl(queryString);
        //Perform an HTTP request and receive a JSON response
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(guardianUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making Http request in fetchArticles(): ", e);
        }
        //Extract the details from the jsonResponse and create a List<Article>
        List<Article> articles = extractFromJson(jsonResponse);
        return articles;
    }


}
