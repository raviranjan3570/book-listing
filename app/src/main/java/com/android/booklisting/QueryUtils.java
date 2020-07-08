package com.android.booklisting;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class QueryUtils {

    //tag for log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // private constructor because no one should ever create instance of this class
    private QueryUtils() {
    }

    // url builder from string
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the url", e);
        }
        return url;
    }

    // convert the inputStream into json string
    public static String readFromInputStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    // make http request to the api
    private static String makeHttpConnection(URL url) throws IOException {

        String jsonResponse = "";
        if (url == null) return jsonResponse;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Response Code : " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem in retrieving the json result");
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (inputStream != null) inputStream.close();
        }
        return jsonResponse;
    }

    private static List<Book> extractFeatureFromJson(String bookJson) {

        if (TextUtils.isEmpty(bookJson)) return null;
        List<Book> books = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(bookJson);
            JSONArray booksArray = baseJsonResponse.getJSONArray("items");
            Log.println(Log.INFO, LOG_TAG, String.valueOf(booksArray.length()));

            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject currentBook = booksArray.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                String language = volumeInfo.getString("language");

                String author;
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    if (!volumeInfo.isNull("authors")) author = (String) authors.get(0);
                    else author = "unknown author";
                } else author = "missing info about author";

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String coverImageUrl = imageLinks.getString("smallThumbnail");

                double price = 0;
                String currency;
                JSONObject saleInfo = currentBook.getJSONObject("saleInfo");
                if (saleInfo.has("retailPrice")) {
                    JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
                    price = retailPrice.getDouble("amount");
                    currency = retailPrice.getString("currencyCode");
                } else {
                    currency = "Not for sale";
                }

                String buyLink = null;
                if (saleInfo.has("buyLink")) {
                    buyLink = saleInfo.getString("buyLink");
                }

                Book book = new Book(title, language, author, coverImageUrl, price, currency, buyLink);
                books.add(book);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the book json result", e);
        }
        return books;
    }

    static List<Book> fetchBookData(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpConnection(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the http request", e);
        }
        return extractFeatureFromJson(jsonResponse);
    }
}
