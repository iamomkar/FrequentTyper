package com.creativeminds.omkar.frequenttyper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omkar on 8/20/2017.
 */

public class SQLDatabaseHelper extends SQLiteOpenHelper {

    //Version
    private static final int DATABASE_VERSION = 1;

    //Names and Keys
    private static final String  DATABASE_NAME = "ftyper";
    private static final String  TABLE_NAME = "tablefwords";
    private static final String  KEY_ID = "fword_id";
    private static final String  KEY_FWORD = "fword";
    private static final String  KEY_FWORD_SEARCH_KEY = "fword_search_key" ;
    private static final String  KEY_DATE_FWORD_ADDED = "fword_date_added";

    //Querys




    public SQLDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_FWORDS_TABLE =  "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FWORD + " TEXT,"
                + KEY_FWORD_SEARCH_KEY + " TEXT," + KEY_DATE_FWORD_ADDED + " TEXT)";

        sqLiteDatabase.execSQL(CREATE_FWORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    //add new entry
    public void addNewFword(Fword fword){
        SQLiteDatabase database =this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FWORD,fword.getFword());
        values.put(KEY_FWORD_SEARCH_KEY,fword.getSearch_key());
        values.put(KEY_DATE_FWORD_ADDED,fword.getDate());

        database.insert(TABLE_NAME,null,values);
        database.close();
    }

    public void updateFword(Fword fword){
        SQLiteDatabase database =this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FWORD,fword.getFword());
        values.put(KEY_FWORD_SEARCH_KEY,fword.getSearch_key());
        values.put(KEY_DATE_FWORD_ADDED,fword.getDate());

        database.update(TABLE_NAME,values,KEY_DATE_FWORD_ADDED + " = ?",new String[]{fword.getDate()});
        database.close();
    }

    public Fword getFwordBySearchKey(String search_key){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor= database.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_FWORD_SEARCH_KEY+" = '"+search_key+"'",null);
        Fword fword = new Fword();
        if(cursor.moveToFirst()) {

           fword = new Fword(cursor.getString(1), cursor.getString(2), cursor.getString(3));

            database.close();
            cursor.close();
        }
        return fword;
    }

    public Fword getFword(String search_key){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor= database.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_FWORD+" = '"+search_key+"'",null);
        Fword fword = new Fword();
        if(cursor.moveToFirst()) {

            fword = new Fword(cursor.getString(1), cursor.getString(2), cursor.getString(3));

            database.close();
            cursor.close();
        }
        return fword;
    }


    public List<Fword> getAllFwords(){
        SQLiteDatabase database = this.getReadableDatabase();

        List<Fword> fwordList = new ArrayList<Fword>();

        Cursor cursor= database.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY "+ KEY_DATE_FWORD_ADDED,null);

        if(cursor != null) {
            cursor.moveToFirst();

            do {
                Fword fword = new Fword(cursor.getString(1), cursor.getString(2), cursor.getString(3));
                fwordList.add(fword);
            }while (cursor.moveToNext());

            database.close();
            cursor.close();
        }

        return fwordList;
    }

    public void getAllFwordsasString(ArrayList<String> list){
        list.clear();
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor= database.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY "+ KEY_DATE_FWORD_ADDED +" DESC" ,null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    //Fword fword = new Fword(cursor.getString(1), cursor.getString(2), cursor.getString(3));
                    list.add(cursor.getString(1));
                } while (cursor.moveToNext());

                database.close();
                cursor.close();
            }
        }
    }

    public void deleteFword(String date){
        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(TABLE_NAME,KEY_DATE_FWORD_ADDED + " = ?",new String[]{date} );
        database.close();
    }

    public boolean checkifSearchKeyAlreadyPresent(String key){

        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_FWORD_SEARCH_KEY+"='" + key + "'", null);
        if (c != null) {
            while (c.moveToNext()) {
                if (!c.getString(0).isEmpty())
                    return true;
            }
        } else {
            return false;
        }
        c.close();
        return false;

    }

    public boolean checkifFwordAlreadyPresent(String key){

        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_FWORD+"='" + key + "'", null);
        if (c != null) {
            while (c.moveToNext()) {
                if (!c.getString(0).isEmpty())
                    return true;
            }
        } else {
            return false;
        }
        c.close();
        return false;

    }

}
