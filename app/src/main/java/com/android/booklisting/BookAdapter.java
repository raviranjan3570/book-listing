package com.android.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;

class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // check if the existing view is being reused
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);
        assert currentBook != null;

        TextView authorTextView = listItemView.findViewById(R.id.author_name);
        String author_name = currentBook.getAuthor();
        authorTextView.setText(author_name);

        TextView bookTitleTextView = listItemView.findViewById(R.id.book_name);
        String book_title = currentBook.getTitle();
        bookTitleTextView.setText(book_title);

        TextView languageCodeTextView = listItemView.findViewById(R.id.language_code);
        String languageCode = currentBook.getLanguage();
        languageCodeTextView.setText(languageCode);

        TextView priceTextView = listItemView.findViewById(R.id.book_price);
        double price = currentBook.getPrice();
        priceTextView.setText(formatPrice(price));
        if (price == 0) priceTextView.setVisibility(View.INVISIBLE);

        TextView currencyCodeTextView = listItemView.findViewById(R.id.currency_code);
        String currencyCode = currentBook.getCurrency();
        currencyCodeTextView.setText(currencyCode);

        ImageView bookCover = listItemView.findViewById(R.id.book_cover);
        bookCover.setImageResource(R.drawable.ic_baseline_search_24);

        return listItemView;
    }

    private String formatPrice(double bookPrice) {
        DecimalFormat priceFormat = new DecimalFormat("0.00");
        return priceFormat.format(bookPrice);
    }
}
