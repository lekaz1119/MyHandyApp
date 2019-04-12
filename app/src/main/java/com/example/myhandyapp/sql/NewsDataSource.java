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

public class NewsDataSource {


    private SQLiteDatabase database;
    private NewsSQLiteHelper dbHelper;
    private String[] allColumns = {
            NewsSQLiteHelper.COLUMN_ID,
            NewsSQLiteHelper.COLUMN_NEWS_TITLE,
            NewsSQLiteHelper.COLUMN_NEWS_AUTHOR,
            NewsSQLiteHelper.COLUMN_NEWS_URL,
            NewsSQLiteHelper.COLUMN_NEWS_ARTICLE

    };

    public NewsDataSource(Context context) {
        dbHelper = new NewsSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public News createNewsArticle( String title, String author,
                               String article, String url) {
        ContentValues values = new ContentValues();
        values.put(NewsSQLiteHelper.COLUMN_NEWS_TITLE, title);
        values.put(NewsSQLiteHelper.COLUMN_NEWS_AUTHOR, author);
        values.put(NewsSQLiteHelper.COLUMN_NEWS_ARTICLE, article);
        values.put(NewsSQLiteHelper.COLUMN_NEWS_URL, url);

        long insertId = database.insert(NewsSQLiteHelper.TABLE_NEWS, null, values);
        Cursor cursor = database.query(NewsSQLiteHelper.TABLE_NEWS,
                allColumns, NewsSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        News newNews = cursorToNews(cursor);
        cursor.close();
        return newNews;
    }

    public void deleteNews(long id ) {
        Log.d(NewsSQLiteHelper.TABLE_NEWS, " deleted with id: " + id);
        database.delete(NewsSQLiteHelper.TABLE_NEWS, NewsSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }


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


    private News cursorToNews(Cursor cursor) {
        News news = new News();
        news.setId(cursor.getLong(0));
        news.setTitle(cursor.getString(1));
        news.setAuthor(cursor.getString(2));
        news.setUrl(cursor.getString(3));
        news.setNewsArticle(cursor.getString(4));

        return news;
    }

    public void printCursor(Cursor c){
        NewsSQLiteHelper.printCursor(c);
    }
}
