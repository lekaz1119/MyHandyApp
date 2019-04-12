package com.example.myhandyapp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myhandyapp.listitems.News;

import java.util.ArrayList;
import java.util.List;

/**
 * class to manipulate database
 */
public class NewsDataSource {

    private int content=0;

    private SQLiteDatabase database;
    private NewsSQLiteHelper dbHelper;
    private String[] allColumns = {
            NewsSQLiteHelper.COLUMN_ID,
            NewsSQLiteHelper.COLUMN_NEWS_TITLE,
            NewsSQLiteHelper.COLUMN_NEWS_AUTHOR,
            NewsSQLiteHelper.COLUMN_NEWS_URL,
            NewsSQLiteHelper.COLUMN_NEWS_ARTICLE

    };

    /**
     * constructor
     * @param context
     */
    public NewsDataSource(Context context) {
        dbHelper = new NewsSQLiteHelper(context);
    }

    /**
     * method to open database as writable
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * method to close the database
     */
    public void close() {
        dbHelper.close();
    }


    /**
     * method to  store news article information in the database for later viewing
     * @param title
     * @param author
     * @param article
     * @param url
     * @return
     */
    public News createNewsArticle( String title, String author,
                               String article, String url) {
        ContentValues values = new ContentValues();
        //sets the value for the database
        values.put(NewsSQLiteHelper.COLUMN_NEWS_TITLE, title);
        values.put(NewsSQLiteHelper.COLUMN_NEWS_AUTHOR, author);
        values.put(NewsSQLiteHelper.COLUMN_NEWS_ARTICLE, article);
        values.put(NewsSQLiteHelper.COLUMN_NEWS_URL, url);

        Log.i("Create news","adding to database");
        long insertId = database.insert(NewsSQLiteHelper.TABLE_NEWS, null, values);
        //inserts the valye int the database
        Cursor cursor = database.query(NewsSQLiteHelper.TABLE_NEWS,
                allColumns, NewsSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        printCursor(cursor);
        //prints the result in LogCat
        News newNews = cursorToNews(cursor);
        cursor.close();
        content++;
        return newNews;
    }

    /**
     * method to delete news item
     * @param id
     */
    public void deleteNews(long id ) {
        Log.d(NewsSQLiteHelper.TABLE_NEWS, " deleted with id: " + id);
        database.delete(NewsSQLiteHelper.TABLE_NEWS, NewsSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }


    /**
     * method to get all the news items stored in the database
     * @return
     */
    public List<News> getAllNews() {
        List<News> newsList = new ArrayList<News>();

        Cursor cursor = database.query(NewsSQLiteHelper.TABLE_NEWS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            News news = cursorToNews(cursor);
            newsList.add(news);
            cursor.moveToNext();
        }
        printCursor(cursor);

        // Make sure to close the cursor
        cursor.close();
        return newsList;
    }

    /**
     * method that uses the values from the cursor to populate the News class
     * @param cursor
     * @return
     */
    private News cursorToNews(Cursor cursor) {
        News news = new News();
        //pass the values from cursor to New class
        news.setId(cursor.getLong(0));
        news.setTitle(cursor.getString(1));
        news.setAuthor(cursor.getString(2));
        news.setUrl(cursor.getString(3));
        news.setNewsArticle(cursor.getString(4));

        return news;
    }

    /**
     * method to  to return content
     * @return
     */
    public int getContent(){
        return content;
    }

    /**
     *  method that prints out the values of the cursor
     * @param c
     */
    public void printCursor(Cursor c){
        NewsSQLiteHelper.printCursor(c);
    }
}
