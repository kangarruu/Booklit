package com.example.android.booklit;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
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
            "https://content.guardianapis.com/search?";

    private static final String MY_API = BuildConfig.ApiKey;

    //Static ID for the ArticleLoader @param
    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Locate the toolbar in activity_main and set it as the SupportActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Learned how to set flag limits for status bar here: https://stackoverflow.com/questions/33347297/can-i-set-flag-layout-no-limits-only-for-status-bar
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //Locate the CollapsingToolbar in activity_main and set its title to the APP name
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.app_name));

        //Locate the ProgressBar in activity_main
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //Create a new instance of an ArticleAdapter
        articleAdapter = new ArticleAdapter(this, 0, new ArrayList<Article>());

        //Locate the ListView with id listView in activity_main and set articleAdaper on it
        ListView articlesListView = findViewById(R.id.listView);
        articlesListView.setAdapter(articleAdapter);
        //Learned how to make nested scrolling work on ListView from here:
        // https://stackoverflow.com/questions/30696611/design-lib-coordinatorlayout-collapsingtoolbarlayout-with-gridview-listview/30885092#30885092
        ViewCompat.setNestedScrollingEnabled(articlesListView,true);

        //Set Intent to open clicked article in user's browser
        articlesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Article currentArticle = (Article) adapterView.getItemAtPosition(position);
                String uri = currentArticle.getArticleUrl();
                Intent uriIntent = new Intent(Intent.ACTION_VIEW);
                uriIntent.setData(Uri.parse(uri));
                startActivity(uriIntent);
            }
        });

        //Find the TextView with the id emptyView and set it as the EmptyView for the ListView
        emptyView = findViewById(R.id.empty_view);
        articlesListView.setEmptyView(emptyView);

        //Check the user's internet connectivity. If there is a connection, initialize the loader
        //Create an instance of ArticleLoader and pass in the Guardian API String
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyView.setText(R.string.connectivity_msg);
        }

    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Retrieve the section preference String value and its default value:
        String section = sharedPrefs.getString(
                getString(R.string.section_preference_key),
                getString(R.string.section_preference_default));

        //Retrieve the order by preference String value and its default value:
        String order_by = sharedPrefs.getString(
                getString(R.string.order_by_key),
                getString(R.string.order_by_default));

        //Parse the URI string that forms the base for the Guardian query
        Uri baseUri = Uri.parse(GUARDIAN_RQUEST_URL);

        //Build up the baseUri with the preference options using a uriBuilder
        Uri.Builder builder = baseUri.buildUpon();

        //Append the query parameters to make a legitimate query Url using the user's preferences
        //But first check if they are null

        if (section != null) {
        builder.appendQueryParameter("section", section);
        }
        if (order_by!= null) {
        builder.appendQueryParameter("order-by", order_by);
        }

        //Add any additional necessary query parameters
        builder.appendQueryParameter(getString(R.string.format_label), getString(R.string.format_value));
        builder.appendQueryParameter(getString(R.string.show_fields_label), getString(R.string.show_fields_value));
        builder.appendQueryParameter(getString(R.string.page_size_label), getString(R.string.page_size_value));


        //Add Api key or query will not work
        builder.appendQueryParameter("api-key", MY_API);

        Log.d(LOG_TAG, "checking for final url string:" + builder.toString());

        return new ArticleLoader(MainActivity.this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        //Set the text in empty_view to display "Nothing to see here, folks"
        //This TextView will only display if there are no articles available
        emptyView.setText(R.string.empty_msg);

        //Set progressBar visibility to "GONE"
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

    //Inflate the menu created in main.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Get the ID of the options item that was selected and create an Intent to open the SettingsActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
