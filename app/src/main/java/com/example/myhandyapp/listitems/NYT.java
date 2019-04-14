package com.example.myhandyapp.listitems;

public class NYT extends ListItem{

    private long id;
    private String title, article, link;

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getArticle() {
        return article;
    }
    public String getLink() {
        return link;
    }



}
