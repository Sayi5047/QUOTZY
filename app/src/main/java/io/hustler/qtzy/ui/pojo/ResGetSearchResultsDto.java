package io.hustler.qtzy.ui.pojo;

import io.hustler.qtzy.ui.pojo.unspalsh.Unsplash_Image;

/**
 * Created by Sayi on 01-02-2018.
 */

public class ResGetSearchResultsDto {
    Unsplash_Image[] results;

    public Unsplash_Image[] getResults() {
        return results;
    }

    public void setResults(Unsplash_Image[] results) {
        this.results = results;
    }
}
