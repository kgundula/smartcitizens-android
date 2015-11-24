package app.defensivethinking.co.za.smartcitizen.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract.UserEntry;
import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract.PropertyEntry;
import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract.MeterReading;


public class SmartCitizenDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "smartcitizen.db";

    public SmartCitizenDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " ( " +
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                UserEntry.COLUMN_USER_ID + " TEXT UNIQUE NOT NULL, " +
                UserEntry.COLUMN_USERNAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                UserEntry.COLUMN_USER_HASH + " TEXT NOT NULL, " +
                UserEntry.COLUMN_USER_SALT + " TEXT NOT NULL, " +
                UserEntry.COLUMN_UPDATED + " TEXT NOT NULL, " +
                " UNIQUE (  "+ UserEntry.COLUMN_USER_EMAIL + " , " + UserEntry.COLUMN_USER_ID +" ) ON CONFLICT IGNORE"+
                " ); ";

        final String SQL_CREATE_PROPERTY_TABLE = "CREATE TABLE " + PropertyEntry.TABLE_NAME + " ( " +
                PropertyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PropertyEntry.COLUMN_PROPERTY_ID +" TEXT UNIQUE NOT NULL, " +
                PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER +" TEXT UNIQUE NOT NULL, " +
                PropertyEntry.COLUMN_PROPERTY_EMAIL +" TEXT NOT NULL, " +
                PropertyEntry.COLUMN_PROPERTY_BP +" TEXT NOT NULL, " +
                PropertyEntry.COLUMN_PROPERTY_PORTION +" TEXT NOT NULL, "+
                PropertyEntry.COLUMN_PROPERTY_CONTACT_TEL +" TEXT NOT NULL, " +
                PropertyEntry.COLUMN_PROPERTY_INITIALS +" TEXT NOT NULL, "+
                PropertyEntry.COLUMN_PROPERTY_OWNER + " TEXT NOT NULL, "+
                PropertyEntry.COLUMN_PROPERTY_SURNAME + " TEXT NOT NULL, " +
                PropertyEntry.COLUMN_PROPERTY_PHYSICAL_ADDRESS +" TEXT NOT NULL, "+
                PropertyEntry.COLUMN_PROPERTY_UPDATED + "  TEXT NOT NULL, " +
                " FOREIGN KEY ( " + PropertyEntry.COLUMN_PROPERTY_OWNER + " ) REFERENCES " +
                UserEntry.TABLE_NAME + " ( " + UserEntry.COLUMN_USER_ID + " )" +
                " UNIQUE (  "+ PropertyEntry.COLUMN_PROPERTY_ID + " , " + PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER +" ) ON CONFLICT IGNORE"+
                " ); ";

        final String SQL_CREATE_METER_TABLE = "CREATE TABLE " + MeterReading.TABLE_NAME +" ( " +
                MeterReading._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MeterReading.COLUMN_METER_ID + " TEXT NOT NULL, " +
                MeterReading.COLUMN_METER_ACCOUNT_NUMBER + " TEXT NOT NULL, "+
                MeterReading.COLUMN_METER_ELECTRICITY + " TEXT NOT NULL, "+
                MeterReading.COLUMN_METER_WATER + " TEXT NOT NULL, "+
                MeterReading.COLUMN_METER_READING_DATE + " TEXT NOT NULL, "+
                " FOREIGN KEY ( " + MeterReading.COLUMN_METER_ACCOUNT_NUMBER+ " ) REFERENCES " +
                PropertyEntry.TABLE_NAME + " ( "+ PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER + " )" +
                " UNIQUE (  "+ MeterReading.COLUMN_METER_ID + " ) ON CONFLICT IGNORE " +
                " ); ";

        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_PROPERTY_TABLE);
        db.execSQL(SQL_CREATE_METER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+UserEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+PropertyEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+MeterReading.TABLE_NAME);
        onCreate(db);
    }
}
