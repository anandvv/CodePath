package com.codepath.anvijay.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anvijay on 11/19/15.
 */

public class TodoItemDatabase extends SQLiteOpenHelper {

    //Database error TAG
    private static final String UNKNOWN_DB_ERROR = "DB Error: ";
    // Database Info
    private static final String DATABASE_NAME = "todosDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TODO = "todos";

    // Todo Table Columns
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TEXT = "text";
    private static final String KEY_TODO_DUE_DATE = "dueDate";
    private static final String KEY_TODO_PRIORITY = "priority";

    //database Singleton
    private static TodoItemDatabase sInstance;

    private SQLiteDatabase mDb;

    public static synchronized TodoItemDatabase getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TodoItemDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodoItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TODO_TEXT + " TEXT" +
                KEY_TODO_DUE_DATE + " TEXT" +
                KEY_TODO_PRIORITY + " INTEGER" +
                ")";


        db.execSQL(CREATE_TODO_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
            onCreate(db);
        }
    }

    // Insert a post into the database
    public void addTodo(Todo todo) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            //values.put(KEY_TODO_ID, todo.id);
            values.put(KEY_TODO_TEXT, todo.text);
            //TODO: add a user defined due date
            values.put(KEY_TODO_DUE_DATE, "2015-11-19 15:24:10.123");
            //TODO: add a real user entered priority
            values.put(KEY_TODO_PRIORITY, (Integer.parseInt(TodoPriority.LOW.toString())));

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_TODO, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d(UNKNOWN_DB_ERROR, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    // Insert or update a user in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long updateTodo(Todo todo) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_ID, todo.id);
            values.put(KEY_TODO_TEXT, todo.text);
            //TODO: add a user defined due date
            values.put(KEY_TODO_DUE_DATE, "2015-11-19 15:24:10.123");
            //TODO: add a real user entered priority
            values.put(KEY_TODO_PRIORITY, (Integer.parseInt(TodoPriority.LOW.toString())));

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_TODO, values, null, null);

            // Check if update succeeded
            if (rows == 1) {
                db.setTransactionSuccessful();
            } else {
                throw new Exception("Unable to update the record!");
            }
        } catch (Exception e) {
            Log.d(UNKNOWN_DB_ERROR, e.getMessage().toString());
        } finally {
            db.endTransaction();
        }
        return todo.id ;
    }

    // Get all posts in the database
    public List<Todo> getAllTodos() {
        List<Todo> todos = new ArrayList<>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        String TODOS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_TODO);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODOS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo newTodo = new Todo();
                    newTodo.text = cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT));

                    todos.add(newTodo);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(UNKNOWN_DB_ERROR, "Error while trying to get todos from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return todos;
    }

    // Delete all todos
    public void deleteAllTodos() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_TODO, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(UNKNOWN_DB_ERROR, "Error while trying to delete all todos");
        } finally {
            db.endTransaction();
        }
    }
}