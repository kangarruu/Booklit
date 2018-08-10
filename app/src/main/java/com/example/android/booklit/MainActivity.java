package com.example.android.booklit;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private android.support.v7.widget.Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ArticleAdapter articleAdapter;
    private TextView emptyView;

    //String url to query the Guardian site for recent articles about books
    private static final String GUARDIAN_RQUEST_URL =
            "https://content.guardianapis.com/search?section=childrens-books-site|books&format=json&show-fields=all&api-key=c893b188-9d88-42b1-8273-c1429007a39d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.app_name));

        //Create a new instance of an ArticleAdapter
        articleAdapter = new ArticleAdapter(this, 0, new ArrayList<Article>());

        //Locate the ListView with id listView in activity_main and set articleAdaper on it
        ListView articlesListView = findViewById(R.id.listView);
        articlesListView.setAdapter(articleAdapter);

        //Find the TextView with the id emptyView and set it as the EmptyView for the ListView
        emptyView = findViewById(R.id.empty_view);
        articlesListView.setEmptyView(emptyView);




    }
}
