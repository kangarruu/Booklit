package com.example.android.booklit;

//Helper methods related to requesting and receiving article data from the Guardian API

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
    private String makeHttpRequest(URL url) throws IOException{
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
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
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
            if (inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponseString;
    }

    //Method for parsing the InputStream and converting it into a String
    //@param an InputStream requested in makeHttpRequest()
    //@return a String containing the content of the InputStream
    private String readFromStream(InputStream inputStream) throws IOException {
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
    private static ArrayList<Article> extractFromJson(String jsonString) throws JSONException{
        if(TextUtils.isEmpty(jsonString)){
            return null;
        }

        //Create an empty ArrayList to add Article objects to
        ArrayList<Article> articles = new ArrayList<>();

        //try parsing the response by locating the root JSON object and the JSONArray containing the results
        try{
            JSONObject baseJsonObject = new JSONObject(jsonString);
            JSONArray resultsArray = baseJsonObject.getJSONArray("results");

            //iterate through baseJsonArray, and extract the relevant Strings
            for(int i = 0; i < resultsArray.length(); i++ ){
                JSONObject currentArticle = resultsArray.getJSONObject(i);
                String sectionName = currentArticle.getString("sectionName");
                String webPublicationDate = currentArticle.getString("webPublicationDate");
                String webTitle = currentArticle.getString("webTitle");
                JSONObject fieldsObject = currentArticle.getJSONObject("fields");
                String trailText = fieldsObject.getString("trailText");
                String byline = fieldsObject.getString("byline");
                String shortUrl = fieldsObject.getString("shortUrl");
                String thumbnail = fieldsObject.getString("thumbnail");

                //Create a new Article object and pass in the extracted variables
                Article article = new Article(byline, webTitle, trailText,webPublicationDate,
                        sectionName, shortUrl, thumbnail );
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
    public List<Article> fetchArticles(String queryString) throws JSONException {
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