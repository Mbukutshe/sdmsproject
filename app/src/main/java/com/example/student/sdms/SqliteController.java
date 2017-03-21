package com.example.student.sdms;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Student on 10/7/2016.
 */
public class SqliteController extends SQLiteOpenHelper
{
    private static final String LOGCAT = null;
    public SqliteController(Context applicationcontext)
    {
        super(applicationcontext, "androidsqlite.db", null, 1);
        Log.d(LOGCAT,"Created");
    }
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        String query,query1;
        query = "CREATE TABLE IF NOT EXISTS Message ( messageId INTEGER PRIMARY KEY, message TEXT,author TEXT,date TEXT,subject TEXT,urgent TEXT)";
        query1= "CREATE TABLE IF NOT EXISTS Messages (messageId INTEGER PRIMARY KEY, message TEXT,subject Text,link Text,author TEXT,date TEXT,filename TEXT)";
        database.execSQL(query1);
        database.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {
        String query;
        query = "DROP TABLE IF EXISTS Students";
        database.execSQL(query);
        onCreate(database);
    }
    public void insertMessage(int messageId,String message,String subject,String author,String link,String date,String filename)
    {
        Log.d(LOGCAT,"insert");
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "INSERT INTO Messages (messageId,message,subject,link,author,date,filename) VALUES("+messageId+",'"+message+"','"+subject+"', '"+link+"','"+author+"','"+date+"','"+filename+"')";
        Log.d("query",query);
        database.execSQL(query);
        database.close();
    }
    public void insertMessag(int messageId,String message,String author,String date,String urgent,String subject)
    {
        Log.d(LOGCAT,"insert");
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "INSERT INTO Message (messageId,message,author,date,subject,urgent) VALUES("+messageId+",'"+message+"', '"+author+"', '"+date+"','"+subject+"','"+urgent+"')";
        Log.d("query",query);
        database.execSQL(query);
        database.close();
    }

    public void deleteMessage(int id)
    {
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM Message where messageId='"+ id +"'";
        Log.d("query",deleteQuery);
        database.execSQL(deleteQuery);
    }
    public void deleteDocuments(int id)
    {
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM Messages where messageId='"+ id +"'";
        Log.d("query",deleteQuery);
        database.execSQL(deleteQuery);
    }
    public Cursor getAllMessages()
    {
        String selectQuery = "SELECT * FROM Messages";
        String []columns = {"messageId","message","subject","link","author","date","filename"};
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query("Messages",columns, null,null,null,null,"date DESC");
        return cursor;
    }
    public Cursor getAllMessage()
    {
        String selectQuery = "SELECT * FROM Message";
        String []columns = {"messageId","message","author","date","subject","urgent"};
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query("Message",columns, null,null,null,null,"date DESC");

        return cursor;
    }

}
