package com.tourist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the Tourist application, and gives the ability to list all RANGES as well as
 * retrieve or modify a specific RANGE.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class TouristDbAdapter {
	public static final String KEY_METERS = "meters";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "TouristDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table ranges (_id integer primary key autoincrement, "
        + "range text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "ranges";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS ranges");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public TouristDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the ranges database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public TouristDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new range using the meters provided. If the range is
     * successfully created return the new rowId for that range, otherwise return
     * a -1 to indicate failure.
     * 
     * @param meters the meters of the range
     * @return rowId or -1 if failed
     */
    public long createRange(String meters) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_METERS, meters);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the range with the given rowId
     * 
     * @param rowId id of range to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteRange(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all ranges in the database
     * 
     * @return Cursor over all ranges
     */
    public Cursor fetchAllRanges() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_METERS}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the range that matches the given rowId
     * 
     * @param rowId id of range to retrieve
     * @return Cursor positioned to matching range, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchRange(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
            		KEY_METERS}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the range using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the meters
     * value passed in
     * 
     * @param rowId id of note to update
     * @param meters value to set ranges meters to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateRange(long rowId, String meters) {
        ContentValues args = new ContentValues();
        args.put(KEY_METERS, meters);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
