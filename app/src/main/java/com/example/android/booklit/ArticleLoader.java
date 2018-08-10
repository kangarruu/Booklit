package com.example.android.booklit;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {
    private String mUrl;

    //Create an articleLoader constructor
    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {
        //If the url is null return null
        if(mUrl == null){
            return null;
        }

        //Return a new list of Article objects
        List<Article> resultList = QueryUtils.fetchArticles(mUrl);
        return resultList;
    }
}
