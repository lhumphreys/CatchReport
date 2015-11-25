package com.app.fish.catchreport;

import android.content.Context;
import android.database.sqlite.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * @author Trevor Sherwood
 * @version 1.0
 * @see android.database.sqlite.SQLiteOpenHelper
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static String name;
    private SQLiteDatabase database;
    private static String dbpath;
    private Context myContext;

    /**
     * Creates instance of DatabaseHandler to handle interactions with given database
     *
     * @param context The context
     * @param n Database name (justTheName.db)
     */
    public DatabaseHandler(Context context, String n)
    {
        super(context, n, null, VERSION);
        name = n;
        dbpath = "/data/data/" + context.getPackageName()+"/databases/";
        myContext=context;
    }

    /**
     * DATABASE MUST BE STORED IN ASSETS. WILL BE COPIED TO /data/data/.../databases AUTOMATICALLY
     *
     * Creates Database if it is not already at /data/data/.../databases. If it exists, does nothing
     *
     * @throws IOException Thrown if there is an error copying database from assets to /data/data/...
     */
    public void createDatabase() throws IOException
    {
        boolean exists = checkDatabase();
        if(!exists)
        {
            this.getReadableDatabase();
            try
            {
                copyDatabase();
            }
            catch(IOException e)
            {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Used in create
     *
     * @return True if database can be opened, False if not
     */
    private boolean checkDatabase()
    {
        SQLiteDatabase db = null;
        try
        {
            String path = dbpath + name;
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch(SQLiteException e){}
        if(db != null) {
            db.close();
            return true;
        }
        return false;
    }

    /**
     * Used in create
     *
     * @throws IOException Thrown if there is an error copying from assets to /data/data/...
     */
    private void copyDatabase() throws IOException
    {
        InputStream min = myContext.getAssets().open(name);
        String outName = dbpath+name;
        OutputStream mout = new FileOutputStream(outName);
        byte[] buffer = new byte[1024];
        int length;
        while((length = min.read(buffer))>0)
        {
            mout.write(buffer, 0, length);
        }
        mout.flush();
        mout.close();
        min.close();
    }

    /**
     * Open database connection. After call, instance of DatabaseHandler can now run queries.
     * Must be closed when done.
     *
     * @throws SQLiteException Error opening database
     */
    public void openDatabase() throws SQLiteException{
        String path = dbpath+name;
        database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * Runs passed in Query and return SQLiteCursor for reading data
     *
     * @param q Query in format SELECT ... FROM ... WHERE attribute=?
     * @param args String[] of values to replace ? in query (in order)
     * @return Cursor for reading information (MUST ALSO BE CLOSED)
     */
    public SQLiteCursor runQuery(String q, String[] args)
    {
        SQLiteCursor cursor = (SQLiteCursor)database.rawQuery(q, args);
        return cursor;
    }

    /**
     * Closes connection
     */
    public synchronized void close()
    {
        if(database != null)
        {
            database.close();
        }
        super.close();
    }


    public void onCreate(SQLiteDatabase data) {}
    public void onUpgrade(SQLiteDatabase data, int one, int two){}
}
