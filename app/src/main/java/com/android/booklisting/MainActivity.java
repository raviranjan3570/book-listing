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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final int BOOK_LOADER_ID = 1;
    private static final String GOOGLE_BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    private BookAdapter mAdapter;
    private LoaderManager loaderManager = getLoaderManager();
    private String query;
    private TextView mEmptyTextView;
    private View mGettingBook;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book currentBook = mAdapter.getItem(i);
                assert currentBook != null;
                if (currentBook.getBuyLink() != null) {
                    Uri bookUri = Uri.parse(currentBook.getBuyLink());
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(MainActivity.this, bookUri);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Book is not available.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
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
            mEmptyTextView.setVisibility(View.GONE);
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
        assert searchManager != null;
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        listView.setVisibility(View.GONE);
        mGettingBook.setVisibility(View.VISIBLE);
        Uri baseUri = Uri.parse(GOOGLE_BOOK_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", query);
        uriBuilder.appendQueryParameter("maxResults", "9");

        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        mAdapter.clear();
        if (books != null && !books.isEmpty()) mAdapter.addAll(books);
        mGettingBook.setVisibility(View.GONE);
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (isConnected) {
            mEmptyTextView.setText(R.string.no_books_found);
        } else {
            mEmptyTextView.setText(R.string.no_internet);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }
}