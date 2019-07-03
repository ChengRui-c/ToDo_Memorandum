package com.example.note;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {
    public static final int NOTE_DIR=0;//访问所有数据

    public static final int NOTE_ITEM=1;//访问单条数据

    public static final String AUTHORITY = "com.example.note.provider";

    private static UriMatcher uriMatcher;

    private MyDatabaseHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "note", NOTE_DIR);
        uriMatcher.addURI(AUTHORITY, "note/#", NOTE_ITEM);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // 删除数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = 0;
        switch (uriMatcher.match(uri)) {
            case NOTE_DIR:
                deletedRows = db.delete("Note", selection, selectionArgs);
                break;
            case NOTE_ITEM:
                String noteId = uri.getPathSegments().get(1);
                deletedRows = db.delete("Note", "id = ?", new String[] { noteId });
                break;
            default:
                break;
        }
        if (deletedRows>0){
            getContext().getContentResolver().notifyChange(uri, null);//通知改变
        }
        return deletedRows;
    }

    @Override
    public String getType(Uri uri) {
        //根据传入的内容URI来返回相应的MIME类型。
        switch (uriMatcher.match(uri)) {
            case NOTE_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.note.provider.note";
            case NOTE_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.note.provider.note";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // 添加数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)) {
            case NOTE_DIR:
            case NOTE_ITEM:
                long newNoteId = db.insert("Note", null, values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/note/" + newNoteId);
                break;
            default:
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);//通知改变
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
        //创建数据库和表
        dbHelper=new MyDatabaseHelper(getContext(),"Notes.db",null,1);
        dbHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //查询数据
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case NOTE_DIR:
                cursor = db.query("Note", projection, selection, selectionArgs, null,
                        null, sortOrder);
                break;
            case NOTE_ITEM:
                String noteId = uri.getPathSegments().get(1);
                cursor = db.query("Note", projection, "id = ?", new String[] { noteId },
                        null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // 更新数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRows = 0;
        switch (uriMatcher.match(uri)) {
            case NOTE_DIR:
                updatedRows = db.update("Note", values, selection, selectionArgs);
                break;
            case NOTE_ITEM:
                String noteId = uri.getPathSegments().get(1);
                updatedRows = db.update("Note", values, "id = ?", new String[]{ noteId });
                break;
            default:
                break;
        }
        if (updatedRows>0){
            getContext().getContentResolver().notifyChange(uri, null);//通知改变
        }
        return updatedRows;
    }
}
