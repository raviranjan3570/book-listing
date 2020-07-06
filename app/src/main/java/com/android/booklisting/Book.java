package com.android.booklisting;

class Book {

    private String title;
    private String author;
    private String language;
    private double price;
    private String imageResourceUrl;
    private String currency;
    private String buyLink;

    public Book(String title, String language, String author, String imageResourceUrl,
                double price, String currency, String buyLink) {
        this.title = title;
        this.author = author;
        this.language = language;
        this.price = price;
        this.imageResourceUrl = imageResourceUrl;
        this.currency = currency;
        this.buyLink = buyLink;
    }

    public String getCurrency() {
        return currency;
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

    public String getLanguage() {
        return language;
    }

    public double getPrice() {
        return price;
    }

    public String getImageResourceUrl() {
        return imageResourceUrl;
    }
}
