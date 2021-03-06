package com.hustler.quote.ui.pojo;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by anvaya5 on 27/12/2017.
 */

public class Quote implements Serializable {
    long id;
    String quote_body;
    String quote_author;
    String quote_category;
    String quote_language;
    GradientDrawable color;

    public Quote(long id, String quote_body, String quote_author, String quote_category, String quote_language, GradientDrawable color) {
        this.id = id;
        this.quote_body = quote_body;
        this.quote_author = quote_author;
        this.quote_category = quote_category;
        this.quote_language = quote_language;
        this.color = color;
    }

    public GradientDrawable getColor() {
        return color;
    }

    public void setColor(GradientDrawable color) {
        this.color = color;
    }


    public Quote(long id, String quote_body, String quote_author, String quote_category, String quote_language, GradientDrawable color, int isLiked) {
        this.id = id;
        this.quote_body = quote_body;
        this.quote_author = quote_author;
        this.quote_category = quote_category;
        this.quote_language = quote_language;
        this.color = color;
        this.isLiked = isLiked;
    }

    public int getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    int isLiked;

    public Quote(String quote_body, String quote_author, String quote_category, String quote_language) {
        this.quote_body = quote_body;
        this.quote_author = quote_author;
        this.quote_category = quote_category;
        this.quote_language = quote_language;
    }

    public Quote() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuote_body() {
        return quote_body;
    }

    public void setQuote_body(String quote_body) {
        this.quote_body = quote_body;
    }

    public String getQuote_author() {
        return quote_author;
    }

    public void setQuote_author(String quote_author) {
        this.quote_author = quote_author;
    }

    public String getQuote_category() {
        return quote_category;
    }

    public void setQuote_category(String quote_category) {
        this.quote_category = quote_category;
    }

    public String getQuote_language() {
        return quote_language;
    }

    public void setQuote_language(String quote_language) {
        this.quote_language = quote_language;
    }


//    @Override
//    public int compareTo(@NonNull Object o) {
//        Quote quote = (Quote) o;
//        return quote_body.equals(quote.quote_body) ? 0 : 1  ;
//    }
}
