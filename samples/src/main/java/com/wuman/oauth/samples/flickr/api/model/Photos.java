
package com.wuman.oauth.samples.flickr.api.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.List;

public class Photos extends GenericJson {

    @Key("total")
    private Integer total;

    @Key("page")
    private Integer page;

    @Key("per_page")
    private Integer perPage;

    @Key("pages")
    private Integer pages;

    @Key("photo")
    private List<Photo> photoList;

    public final Integer getTotal() {
        return total;
    }

    public final Integer getPage() {
        return page;
    }

    public final Integer getPerPage() {
        return perPage;
    }

    public final Integer getPages() {
        return pages;
    }

    public final List<Photo> getPhotoList() {
        return photoList;
    }

    @Override
    public Photos clone() {
        return (Photos) super.clone();
    }

    @Override
    public Photos set(String fieldName, Object value) {
        return (Photos) super.set(fieldName, value);
    }

}
