package com.example.travelplan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String place, String day, String time, String address, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO TRAVEL VALUES (NULL, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, place);
        statement.bindString(2, day);
        statement.bindString(3, time);
        statement.bindString(4, address);
        statement.bindBlob(5, image);

        statement.executeInsert();
    }

    public void updateData(String place, String day, String time, String address, byte[] image, int id) {
        SQLiteDatabase database = getWritableDatabase();
        //SQLiteDatabase database = getReadableDatabase();
        String sql = "UPDATE TRAVEL SET place = ?, day = ?, time = ?, address = ?,image = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, place);
        statement.bindString(2, day);
        statement.bindString(3, time);
        statement.bindString(4, address);
        statement.bindBlob(5, image);
        statement.bindDouble(6, (double)id);

        statement.execute();
        database.close();

    }


    public  void deleteData(int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM TRAVEL WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}