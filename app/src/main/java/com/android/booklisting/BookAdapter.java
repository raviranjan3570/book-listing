package com.android.booklisting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;

class BookAdapter extends ArrayAdapter<Book> {

    private Context mContext;

    public BookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
        mContext = context;
    }

    @SuppressLint("SetTextI18n")
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

        TextView indexTextView = listItemView.findViewById(R.id.book_index);
        int index = currentBook.getIndex();
        indexTextView.setText(Integer.toString(index));

        TextView authorTextView = listItemView.findViewById(R.id.author_name);
        String author_name = currentBook.getAuthor();
        authorTextView.setText(author_name);

        TextView bookTitleTextView = listItemView.findViewById(R.id.book_name);
        String book_title = currentBook.getTitle();
        bookTitleTextView.setText(book_title);

        TextView ratingTextView = listItemView.findViewById(R.id.rating);
        String rating = formatRating(currentBook.getRating());
        ratingTextView.setText(rating);

        TextView priceTextView = listItemView.findViewById(R.id.book_price);
        double price = currentBook.getPrice();
        if (price == 0) {
            priceTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            priceTextView.setText(R.string.not_for_sale);
        } else {
            String priceWithCurrencyCode = formatPrice(price) + " ";
            priceTextView.setText(priceWithCurrencyCode);
        }

        String imageUrl = currentBook.getImageResourceUrl();
        ImageView bookCover = listItemView.findViewById(R.id.book_cover);
        Glide.with(mContext).load(imageUrl).into(bookCover);
        return listItemView;
    }

    private String formatPrice(double bookPrice) {
        DecimalFormat priceFormat = new DecimalFormat("0.00");
        return priceFormat.format(bookPrice);
    }

    private String formatRating(double bookRating) {
        DecimalFormat ratingFormat = new DecimalFormat("0.0");
        return ratingFormat.format(bookRating);
    }
}
