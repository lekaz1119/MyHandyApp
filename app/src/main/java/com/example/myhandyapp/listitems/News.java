package com.example.myhandyapp.listitems;

public class News extends ListItem {

    private long id;
    private String title,author,article,url;



    public void setId(long id){this.id=id;}

    public long getId() {
        return id;
    }

    public void setTitle(String title){this.title=title;}

    public String getTitle(){return title;}

    public void setAuthor(String author){this.author=author;}

    public String getAuthor(){return author;}

    public void setNewsArticle(String article){this.article=article;}

    public String getNewsArticle(){return article;}

    public void setUrl(String url){this.url=url;}

    public String getUrl(){return url;}



}
