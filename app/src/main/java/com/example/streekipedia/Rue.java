package com.example.streekipedia;

import java.io.Serializable;

class Rue implements Serializable {

    private String nomRue;
    private String titre;
    private Integer pageId;
    private String snippet;
    private String description;
    private String thumbnail;
    private String image;

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

    String getThumbnail() {
        return thumbnail;
    }

    void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    String getImage() {
        return image;
    }

    void setImage(String image) {
        this.image = image;
    }
}
