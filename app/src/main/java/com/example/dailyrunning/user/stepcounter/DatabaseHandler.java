package com.example.dailyrunning.user.stepcounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "stepDatabase";
    private static final String TABLE = "step";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(StepModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        db.insert(TABLE, null, cv);
    }

    public StepModel getTasks(String id){
        Cursor cur;
        cur = db.query(TABLE, null, null, null, null, null, null, null);
        StepModel task = new StepModel(0,0);
        cur.moveToFirst();
        while(!cur.isLast()) {
            task.setId(cur.getInt(cur.getColumnIndex(ID)));
            task.setTask(cur.getInt(cur.getColumnIndex(TASK)));
            cur.moveToNext();
        }
        cur.close();
        return task;
    }
    public String getLastTask() {
        String selectQuery = "SELECT * FROM " + TABLE + " ORDER BY ID DESC LIMIT 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String str = "";
        if (cursor.moveToFirst())
            str = cursor.getString(cursor.getColumnIndex(ID));
        cursor.close();
        return str;
    }
    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TABLE, cv, id + "= ?", new String[] {String.valueOf(task)});
    }

    public void deleteTask(int id){
        db.delete(TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}
