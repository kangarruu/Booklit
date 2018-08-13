package com.example.android.booklit;

import android.graphics.Bitmap;

public class Article {

    private String mAuthor;         //byline (Name of the article's Author)
    private String mTitle;          //webTitle (Article title as appears on web)
    private String mSnippet;        //trailText (Descriptive text)
    private String mPublishDate;     //webPublicationDate (Published date)
    private String mSection;        //sectionName (Website section name)
    private String mUrl;            //shortUrl (shortened article URL)
    private Bitmap mThumbnail;      //thumbnail (Image associated with the article)

    // Create an Article constructor

    public Article(String author, String title, String snippet,
                   String publishDate, String section, String url, Bitmap thumbnail){
        mAuthor = author;
        mTitle = title;
        mSnippet = snippet;
        mPublishDate = publishDate;
        mSection = section;
        mUrl = url;
        mThumbnail = thumbnail;
    }

    //Create public getter methods for accessing Article variables

    public String getArticleAuthor() {
        return mAuthor;
    }
    public String getArticleTitle() {
        return mTitle;
    }
    public String getArticleSnippet() {
        return mSnippet;
    }
    public String getArticleDate() {
        return mPublishDate;
    }
    public String getArticleSection() {
        return mSection;
    }
    public String getArticleUrl() {
        return mUrl;
    }
    public Bitmap getArticleThumbnail() {
        return mThumbnail;
    }
}

