package com.android.booklisting;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final int BOOK_LOADER_ID = 1;
    private static final String GOOGLE_BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    private BookAdapter mAdapter;
    LoaderManager loaderManager = getLoaderManager();
    private String query;
    private TextView mEmptyTextView;
    private View mGettingBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        ListView listView = findViewById(R.id.list_view);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        listView.setAdapter(mAdapter);
        mEmptyTextView = findViewById(R.id.empty_view);
        mEmptyTextView.setText(R.string.no_books);
        listView.setEmptyView(mEmptyTextView);
        mGettingBook = findViewById(R.id.loading_spinner);
        mGettingBook.setVisibility(View.GONE);
        if (!isConnected) {
            mGettingBook.setVisibility(View.GONE);
            mEmptyTextView.setText(R.string.no_internet);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mGettingBook.setVisibility(View.VISIBLE);
            query = intent.getStringExtra(SearchManager.QUERY);
            if (!getSupportLoaderManager().hasRunningLoaders()) {
                loaderManager.initLoader(BOOK_LOADER_ID, null, this);
            }
            loaderManager.restartLoader(BOOK_LOADER_ID, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        Uri baseUri = Uri.parse(GOOGLE_BOOK_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", query);
        uriBuilder.appendQueryParameter("maxResults", "20");

        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        mEmptyTextView.setText(R.string.no_books_found);
        mAdapter.clear();
        if (books != null && !books.isEmpty()) mAdapter.addAll(books);
        mGettingBook.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }
}