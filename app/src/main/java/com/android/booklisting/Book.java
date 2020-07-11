package com.android.booklisting;

class Book {

    private String title;
    private String author;
    private double rating;
    private double price;
    private String imageResourceUrl;
    private int index;
    private String buyLink;

    public Book(String title, double rating, String author, String imageResourceUrl,
                double price, int index, String buyLink) {
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.price = price;
        this.imageResourceUrl = imageResourceUrl;
        this.index = index;
        this.buyLink = buyLink;
    }

    public int getIndex() {
        return index;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getRating() {
        return rating;
    }

    public double getPrice() {
        return price;
    }

    public String getImageResourceUrl() {
        return imageResourceUrl;
    }

}
