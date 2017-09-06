package com.kyle.lefebvre.cardholder;

import android.provider.BaseColumns;

/**
 * Created by kyle on 9/6/2017.
 */

public class DbCard {

    private DbCard() {
    }

    public static class Card implements BaseColumns {

        public static final String TABLE_NAME = "card";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CVV = "cvv";
        public static final String COLUMN_EXPIRY = "date";
        public static final String COLUMN_CARD_NUMBER = "card_number";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_CVV + " TEXT NOT NULL, " +
                COLUMN_EXPIRY + " TEXT NOT NULL, " +
                COLUMN_CARD_NUMBER + " TEXT NOT NULL );";
    }
}
