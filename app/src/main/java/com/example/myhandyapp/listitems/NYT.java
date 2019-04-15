package com.example.myhandyapp.listitems;


/**
 *   It is the new york times class that gets and sets id, title, article  and link.
 */

public class NYT extends ListItem{

    private long id;
    private String title, article, link;

    public long getId() {

        return id;
    }

    /**
     * It sets the id
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * It gets the title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * It sets  title.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * It is sets article.
     * @param article
     */
    public void setArticle(String article) {
        this.article = article;
    }

    /**
     * It sets link.
     * @param link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * It gets article.
     * @return article
     */
    public String getArticle() {
        return article;
    }

    /**
     * It gets link.
     * @return link
     */
    public String getLink() {
        return link;
    }



}
