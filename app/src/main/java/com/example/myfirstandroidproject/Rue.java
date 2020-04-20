package com.example.myfirstandroidproject;

import android.widget.ImageView;

public class Rue {

    private String nomRue;
    private String titre;
    private Integer pageId;
    private String snippet;
    private String description;
    private ImageView thumbnail;
    private ImageView image;

    /*public Rue(String nomRue, String titre, Integer pageId, String snippet, String description, ImageView thumbnail, ImageView image) {
        this.nomRue = nomRue;
        this.titre = titre;
        this.pageId = pageId;
        this.snippet = snippet;
        this.description = description;
        this.thumbnail = thumbnail;
        this.image = image;
    }*/

    Rue() {

    }

    String getNomRue() {
        return nomRue;
    }

    void setNomRue(String nomRue) {
        this.nomRue = nomRue;
    }

    String getTitre() {
        return titre;
    }

    void setTitre(String titre) {
        this.titre = titre;
    }

    Integer getPageId() {
        return pageId;
    }

    void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    String getSnippet() {
        return snippet;
    }

    void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    ImageView getThumbnail() {
        return thumbnail;
    }

    void setThumbnail(ImageView thumbnail) {
        this.thumbnail = thumbnail;
    }

    ImageView getImage() {
        return image;
    }

    void setImage(ImageView image) {
        this.image = image;
    }
}
