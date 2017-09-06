package com.kyle.lefebvre.cardholder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static android.icu.text.MessagePattern.ArgType.SELECT;
import static com.kyle.lefebvre.cardholder.DbCard.Card.COLUMN_CARD_NUMBER;
import static com.kyle.lefebvre.cardholder.DbCard.Card.COLUMN_CVV;
import static com.kyle.lefebvre.cardholder.DbCard.Card.COLUMN_EXPIRY;
import static com.kyle.lefebvre.cardholder.DbCard.Card.COLUMN_NAME;
import static com.kyle.lefebvre.cardholder.DbCard.Card.TABLE_NAME;

/**
 * Created by kyle on 9/6/2017.
 */

public class DbHelper extends SQLiteOpenHelper {
    private DbHelper DBHelper;
    private SQLiteDatabase db;
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "card_database.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DbCard.Card.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public List<Card> getAllcard() {
        // array of columns to fetch
        String[] columns = {
                DbCard.Card._ID,
                DbCard.Card.COLUMN_NAME,
                DbCard.Card.COLUMN_CVV,
                DbCard.Card.COLUMN_EXPIRY,
                DbCard.Card.COLUMN_CARD_NUMBER
        };

        List<Card> mCards = new ArrayList<Card>();

        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_NAME, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Card card = new Card();
                card.setName(cursor.getString(cursor.getColumnIndex(DbCard.Card.COLUMN_NAME)));
                card.setCvv(cursor.getString(cursor.getColumnIndex(DbCard.Card.COLUMN_CVV)));
                card.setExpiry(cursor.getString(cursor.getColumnIndex(DbCard.Card.COLUMN_EXPIRY)));
                card.setNumber(cursor.getString(cursor.getColumnIndex(DbCard.Card.COLUMN_CARD_NUMBER)));
                // Adding user record to list
                mCards.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();

        // return user list
        return mCards;
    }

    public void addCard(Card card) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        
        values.put(DbCard.Card.COLUMN_NAME, card.getName());
        values.put(DbCard.Card.COLUMN_CVV, card.getCvv());
        values.put(DbCard.Card.COLUMN_EXPIRY, card.getExpiry());
        values.put(DbCard.Card.COLUMN_CARD_NUMBER, card.getNumber());

        db.insert(TABLE_NAME, null, values);
        //db.close();
    }

    public DbHelper open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    public boolean checkAlreadyExist(String number)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_CARD_NUMBER + " FROM " + TABLE_NAME + " WHERE " + COLUMN_CARD_NUMBER + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{number});
        if (cursor.getCount() > 0)
        {
            return true;
        }
        else
            return false;
    }

    public void deleteRowFromTable(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_CARD_NUMBER + "=?";
        String[] whereArgs = new String[]{number};
        db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public void updateCard(String fNum,String lNum, String cvv , String name, String expiry){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME,name); //These Fields should be your String values of actual column names
        cv.put(COLUMN_CVV,cvv);
        cv.put(COLUMN_EXPIRY,expiry);
        cv.put(COLUMN_CARD_NUMBER, lNum);

        db.update(TABLE_NAME, cv, COLUMN_CARD_NUMBER +" = "+fNum, null);
    }
}
