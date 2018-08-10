package com.example.android.booklit;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private android.support.v7.widget.Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ProgressBar progressBar;
    private TextView emptyView;

    //Instance of custom ArrayAdaper for Article objects
    private ArticleAdapter articleAdapter;

    //String url to query the Guardian site for recent articles about books
    private static final String GUARDIAN_RQUEST_URL =
            "https://content.guardianapis.com/search?section=childrens-books-site|books&format=json&show-fields=all&api-key=c893b188-9d88-42b1-8273-c1429007a39d";

    //Static ID for the ArticleLoader @param
    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Locate the toolbar in activity_main and set it as the SupportActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Locate the CollapsingToolbar in activity_main and set its title to the APP name
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

        //Create an instance of ArticleLoader and pass in the Guardian API String
        getLoaderManager().initLoader(LOADER_ID, null, this);

    }


    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        return new ArticleLoader(MainActivity.this, GUARDIAN_RQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        //Set the text in empty_view to display "Nothing to see here, folks"
        //This TextView will only display if there are no articles available
        emptyView.setText("Nothing to see here, folks");

        //Locate the ProgressBar in activity_main and set its visibility to "GONE"
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        //Clear the ArticleAdapter of any previous entries
        articleAdapter.clear();

        //If a valid list of articles is being passed in set the adapter's contents with this list
        if (articles != null && !articles.isEmpty()) {
            articleAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        //Clear the ArticleAdapter of any existing lists
        articleAdapter.clear();

    }
}
