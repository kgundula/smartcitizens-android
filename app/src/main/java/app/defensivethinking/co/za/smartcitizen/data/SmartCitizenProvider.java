package app.defensivethinking.co.za.smartcitizen.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;


public class SmartCitizenProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SmartCitizenDbHelper mOpenHelper;

    private static final int USER = 100;
    private static final int USER_ID = 101;
    private static final int METER = 200;
    private static final int METER_ID = 201;
    private static final int PROPERTY = 300;
    private static final int PROPERTY_ID = 301;

    private static final SQLiteQueryBuilder sPropertyByOwnerQueryBuilder;

    static{
        sPropertyByOwnerQueryBuilder = new SQLiteQueryBuilder();
        sPropertyByOwnerQueryBuilder.setTables(
                SmartCitizenContract.UserEntry.TABLE_NAME + " INNER JOIN " +
                        SmartCitizenContract.PropertyEntry.TABLE_NAME +
                        " ON " + SmartCitizenContract.PropertyEntry.TABLE_NAME +
                        "." + SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_OWNER +
                        " = " + SmartCitizenContract.UserEntry.TABLE_NAME +
                        "." + SmartCitizenContract.UserEntry.COLUMN_USER_ID);
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SmartCitizenContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, SmartCitizenContract.PATH_USER, USER);
        matcher.addURI(authority, SmartCitizenContract.PATH_USER +"/#", USER_ID);
        matcher.addURI(authority, SmartCitizenContract.PATH_PROPERTY, PROPERTY);
        matcher.addURI(authority, SmartCitizenContract.PATH_PROPERTY +"/#", PROPERTY_ID);
        matcher.addURI(authority, SmartCitizenContract.PATH_METER_READING, METER);
        matcher.addURI(authority, SmartCitizenContract.PATH_METER_READING +"/#", METER_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SmartCitizenDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor smartCursor;
        switch (sUriMatcher.match(uri)) {

            case USER:{
                smartCursor = mOpenHelper.getReadableDatabase().query(
                        SmartCitizenContract.UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case USER_ID: {
                smartCursor = mOpenHelper.getReadableDatabase().query(
                        SmartCitizenContract.UserEntry.TABLE_NAME,
                        projection,
                        SmartCitizenContract.UserEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PROPERTY: {
                smartCursor =
                        mOpenHelper.getReadableDatabase().query(
                        SmartCitizenContract.PropertyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }
            case PROPERTY_ID: {
                smartCursor = mOpenHelper.getReadableDatabase().query(
                        SmartCitizenContract.PropertyEntry.TABLE_NAME,
                        projection,
                        SmartCitizenContract.PropertyEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case METER : {
                smartCursor = mOpenHelper.getReadableDatabase().query(
                        SmartCitizenContract.MeterReading.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case METER_ID: {
                smartCursor = mOpenHelper.getReadableDatabase().query(
                        SmartCitizenContract.MeterReading.TABLE_NAME,
                        projection,
                        SmartCitizenContract.MeterReading._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        smartCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return smartCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USER:
                return SmartCitizenContract.UserEntry.CONTENT_TYPE;
            case USER_ID:
                return SmartCitizenContract.UserEntry.CONTENT_ITEM_TYPE;
            case PROPERTY :
                return SmartCitizenContract.PropertyEntry.CONTENT_TYPE;
            case PROPERTY_ID :
                return SmartCitizenContract.PropertyEntry.CONTENT_ITEM_TYPE;
            case METER:
                return SmartCitizenContract.MeterReading.CONTENT_TYPE;
            case METER_ID:
                return SmartCitizenContract.MeterReading.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri smartUri;

        switch (match) {
            case USER: {
                long _id = db.insert(SmartCitizenContract.UserEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    smartUri = SmartCitizenContract.UserEntry.buildUserUri(_id);
                else
                    throw new SQLException("Failed to insert new row into :"+uri);
                break;
            }
            case PROPERTY: {
                long _id = db.insert(SmartCitizenContract.PropertyEntry.TABLE_NAME, null, values);
                if (_id > 0 )
                    smartUri = SmartCitizenContract.PropertyEntry.buildPropertyUri(_id);
                else
                    throw new SQLException("Failed to insert new row into :"+uri);

                break;
            }
            case METER: {
                long _id = db.insert(SmartCitizenContract.MeterReading.TABLE_NAME, null, values);
                if (_id > 0)
                    smartUri = SmartCitizenContract.MeterReading.buildMeterUri(_id);
                else
                    throw new SQLException("Failed to insert new row into :"+uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return smartUri;

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int deleted;

        switch (match) {
            case USER:
                deleted = db.delete(SmartCitizenContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PROPERTY:
                deleted = db.delete(SmartCitizenContract.PropertyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case METER:
                deleted = db.delete(SmartCitizenContract.MeterReading.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);

        }

        if (selection == null || deleted !=0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updated;

        switch (match) {
            case USER : {
                updated = db.update(SmartCitizenContract.UserEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            case PROPERTY: {
                updated = db.update(SmartCitizenContract.PropertyEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            case METER: {
                updated = db.update(SmartCitizenContract.MeterReading.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if ( updated != 0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return updated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PROPERTY:
                db.beginTransaction();
                int count = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SmartCitizenContract.PropertyEntry.TABLE_NAME, null, value);
                        if ( _id != -1) {
                            count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case METER:
                db.beginTransaction();
                int my_count = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SmartCitizenContract.MeterReading.TABLE_NAME, null, value);
                        if ( _id != -1) {
                            my_count++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return my_count;
            default:
                return super.bulkInsert(uri,values);
        }


    }
}
