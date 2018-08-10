package com.example.android.booklit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

//Custom ArrayAdapter for displaying Article objects in list

public class ArticleAdapter extends ArrayAdapter<Article> {
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
        viewHolder.snippet_view = (TextView) convertView.findViewById(R.id.snippet_view);
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
         if(currentArticle != null){

            //Get the title of the article in the current position. Display it in title_view
            String articleTitle = currentArticle.getArticleTitle();
            viewHolder.title_view.setText(articleTitle);

            //Get a short description about the article in the current position. Display it in snippet_view
            String articleSnippet = currentArticle.getArticleSnippet();
            viewHolder.snippet_view.setText(articleSnippet);

            //Get the author for the article in the current position. Display it in author_view
            String articleAuthor = currentArticle.getArticleAuthor();
            viewHolder.author_view.setText(articleAuthor);

            //Get the date the article was published. Reformat that String to SimpleDateFormat with the pattern LLL, dd, yyy.
            // Display the reformatted String in date_view
            String articleDate = currentArticle.getArticleDate();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("LLL dd, yyyy");
            String dateToDisplay = dateFormatter.format(articleDate);
            viewHolder.date_view.setText(dateToDisplay);

            //Get the section for the article in the current position. Display it in section_view
            String articleSection = currentArticle.getArticleSection();
            viewHolder.section_view.setText(articleSection);

            //Get the thumbnail url for the article in the current position.
            String articleThumbnailUrl = currentArticle.getArticleThumbnail();
        }


        return convertView;
    }

    //Declare the ViewHolder class to store the Views from list_item.xml
    private static class ViewHolder{
        public TextView title_view;
        public TextView snippet_view;
        public TextView author_view;
        public TextView date_view;
        public TextView section_view;
        public ImageView thumbnail;
    }
}
