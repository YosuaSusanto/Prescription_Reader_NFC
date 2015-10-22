package com.example.reico_000.prescriptionreadernfc;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by Yosua Susanto on 17/10/2015.
 */
public class MedicationProvider extends ContentProvider {
    /*
     * Always return true, indicating that the
     * provider loaded correctly.
     */
    private MedicationDatabaseSQLiteHandler dbHandler = null;

    // helper constants for use with the UriMatcher
    private static final int MED_LIST = 1;
    private static final int MED_ID = 2;
    private static final int CONSUMPTION_LIST = 5;
    private static final int CONSUMPTION_ID = 6;
    private static final int ENTITY_LIST = 10;
    private static final int ENTITY_ID = 11;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    // prepare the UriMatcher
    static {
        URI_MATCHER.addURI(MedicationContract.AUTHORITY,
                "medicationTable",
                MED_LIST);
        URI_MATCHER.addURI(MedicationContract.AUTHORITY,
                "medicationTable/#",
                MED_ID);
        URI_MATCHER.addURI(MedicationContract.AUTHORITY,
                "consumptionTable",
                CONSUMPTION_LIST);
        URI_MATCHER.addURI(MedicationContract.AUTHORITY,
                "consumptionTable/#",
                CONSUMPTION_ID);
//        URI_MATCHER.addURI(MedicationContract.AUTHORITY,
//                "entities",
//                ENTITY_LIST);
//        URI_MATCHER.addURI(MedicationContract.AUTHORITY,
//                "entities/#",
//                ENTITY_ID);
    }

    @Override
    public boolean onCreate() {
        dbHandler = MedicationDatabaseSQLiteHandler.getInstance(getContext());
        return true;
    }

    /*
     * Return no type for MIME type
     */
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case MED_LIST:
                return MedicationContract.Medications.CONTENT_TYPE;
            case MED_ID:
                return MedicationContract.Medications.CONTENT_MED_TYPE;
            case CONSUMPTION_ID:
                return MedicationContract.Consumption.CONTENT_TYPE;
            case CONSUMPTION_LIST:
                return MedicationContract.Consumption.CONTENT_CONSUMPTION_TYPE;
//            case ENTITY_ID:
//                return ItemEntities.CONTENT_ENTITY_TYPE;
//            case ENTITY_LIST:
//                return ItemEntities.CONTENT_TYPE;
            default:
                return null;
        }
    }
    /*
     * query() always returns no results
     *
     */
    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false;
        switch (URI_MATCHER.match(uri)) {
            case MED_LIST:
                builder.setTables(dbHandler.TABLE_MEDICATIONS);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = MedicationContract.Medications.SORT_ORDER_DEFAULT;
                }
                break;
            case MED_ID:
                builder.setTables(dbHandler.TABLE_MEDICATIONS);
                // limit query to one row at most:      //WHY LIKE THIS??
//                builder.appendWhere(MedicationContract.Medications._ID + " = " +
//                        uri.getLastPathSegment());
                break;
            case CONSUMPTION_LIST:
                builder.setTables(dbHandler.TABLE_CONSUMPTIONS);
                break;
            case CONSUMPTION_ID:
                builder.setTables(dbHandler.TABLE_CONSUMPTIONS);
                // limit query to one row at most:      //WHY LIKE THIS??
//                builder.appendWhere(MedicationContract.Consumption._ID + " = " +
//                        uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
        // if we want to be notified of any changes:
        if (useAuthorityUri) {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    MedicationContract.CONTENT_URI);
        }
        else {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    uri);
        }
        return cursor;
    }
    /*
     * insert() always returns null (no URI)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != MED_LIST
                && URI_MATCHER.match(uri) != CONSUMPTION_LIST) {
            throw new IllegalArgumentException(
                    "Unsupported URI for insertion: " + uri);
        }
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        if (URI_MATCHER.match(uri) == MED_LIST) {
            long id =
                    db.insert(
                            dbHandler.TABLE_MEDICATIONS,
                            null,
                            values);
            return getUriForId(id, uri);
        } else {
            long id =
                    db.insert(
                            dbHandler.TABLE_CONSUMPTIONS,
                            null,
                            values);
            return getUriForId(id, uri);
        }
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
//            if (!isInBatchMode()) {
                // notify all listeners of changes:
            getContext().getContentResolver().notifyChange(itemUri, null);
//            }
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }
    /*
     * delete() always returns "no rows affected" (0)
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        int delCount = 0;
        String idStr;
        String where;
        switch (URI_MATCHER.match(uri)) {
            case MED_LIST:
                delCount = db.delete(
                        dbHandler.TABLE_MEDICATIONS,
                        selection,
                        selectionArgs);
                break;
            case MED_ID:
                idStr = uri.getLastPathSegment();
                where = MedicationContract.Medications._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        dbHandler.TABLE_MEDICATIONS,
                        where,
                        selectionArgs);
                break;
            case CONSUMPTION_LIST:
                delCount = db.delete(
                        dbHandler.TABLE_CONSUMPTIONS,
                        selection,
                        selectionArgs);
                break;
            case CONSUMPTION_ID:
                idStr = uri.getLastPathSegment();
                where = MedicationContract.Consumption._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        dbHandler.TABLE_CONSUMPTIONS,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for deleting photos or entities –
                // photos are deleted by a trigger when the item is deleted
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (delCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }
    /*
     * update() always returns "no rows affected" (0)
     */
    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String idStr;
        String where;
        int updateCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case MED_LIST:
                updateCount = db.update(
                        dbHandler.TABLE_MEDICATIONS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case MED_ID:
                idStr = uri.getLastPathSegment();
                where = MedicationContract.Consumption._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        dbHandler.TABLE_MEDICATIONS,
                        values,
                        where,
                        selectionArgs);
                break;
            case CONSUMPTION_LIST:
                updateCount = db.update(
                        dbHandler.TABLE_CONSUMPTIONS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CONSUMPTION_ID:
                idStr = uri.getLastPathSegment();
                where = MedicationContract.Consumption._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        dbHandler.TABLE_CONSUMPTIONS,
                        values,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for updating photos or entities!
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }
}