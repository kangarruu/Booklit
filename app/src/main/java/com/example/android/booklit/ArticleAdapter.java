package com.example.android.booklit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//Custom ArrayAdapter for displaying Article objects in a list

public class ArticleAdapter extends ArrayAdapter<Article> {
    public static final String LOG_TAG = ArticleAdapter.class.getSimpleName();

    public ArticleAdapter(@NonNull Context context, int resource, @NonNull List<Article> articles) {
        super(context, resource, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        Article currentArticle = getItem(position);

        // If there is no current View, create a LayoutInflator and use it to inflate a new item_list Layout.
        if (convertView == null){
            LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item, null);

        // Populate the new convertView with Views stored in the Viewholder
        viewHolder.author_view = (TextView) convertView.findViewById(R.id.author_view);
        viewHolder.title_view = (TextView) convertView.findViewById(R.id.title_view);
        viewHolder.snippet_view = (WebView) convertView.findViewById(R.id.snippet_view);
        viewHolder.date_view = (TextView) convertView.findViewById(R.id.date_view);
        viewHolder.section_view = (TextView) convertView.findViewById(R.id.section_view);
        viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);

        // Set the tag on the convertView with the viewHolder for better performance
        convertView.setTag(viewHolder);

        // Otherwise if there is an existing convertView get the previous contents
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //If there is an Article object in the current position of the ArrayList, get its details
         if(currentArticle != null) {

             //Get the title of the article in the current position. Display it in title_view
             String articleTitle = currentArticle.getArticleTitle();
             viewHolder.title_view.setText(articleTitle);

             //Get a short description about the article in the current position. Display it in snippet_view
             //Learned how to hide html tags by using a webView here: https://stackoverflow.com/questions/1292575/android-textview-justify-text/42991773#42991773
             String articleSnippet = currentArticle.getArticleSnippet();
             viewHolder.snippet_view.loadData(articleSnippet,"text/html", null);

             //Get the author for the article in the current position. Display it in author_view
             String articleAuthor = currentArticle.getArticleAuthor();
             viewHolder.author_view.setText(articleAuthor);

             //Get the date the article was published. Split the date off the webPublicationDate String. Reformat that String to SimpleDateFormat with the pattern LLL, dd, yyy.
             // Display the reformatted String in date_view
             String apiArticleDate = currentArticle.getArticleDate();
             if(apiArticleDate.contains("T")){
                 String[] splitDate = apiArticleDate.split("(?=T)");
                 //Keep the first index
                 String justDate = splitDate[0];
                 SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                 Date convertedDate = null;
                 try {
                     convertedDate= dateFormatter.parse(justDate);
                 } catch (ParseException e) {
                     Log.e(LOG_TAG, "Error parsing date String into Date", e);
                 }

                 SimpleDateFormat finalFormatter = new SimpleDateFormat("LLL dd, yyyy");
                 String dateToDisplay = finalFormatter.format(convertedDate);

                 viewHolder.date_view.setText(dateToDisplay);
             }

             //Get the section for the article in the current position. Display it in section_view
             String articleSection = currentArticle.getArticleSection();
             viewHolder.section_view.setText(articleSection);

             //Get the thumbnail url for the article in the current position
             Bitmap articleThumbnailUrl = currentArticle.getArticleThumbnail();
             //If no image exists, hide he thumbnail View
             if (articleThumbnailUrl != null){
                 viewHolder.thumbnail.setImageBitmap(articleThumbnailUrl);
             } else {
                 viewHolder.thumbnail.setVisibility(View.GONE);
             }

         }

        return convertView;
    }

    //Declare the ViewHolder class to store the Views from list_item.xml
    private static class ViewHolder{
        public TextView title_view;
        public WebView snippet_view;
        public TextView author_view;
        public TextView date_view;
        public TextView section_view;
        public ImageView thumbnail;
    }
}
