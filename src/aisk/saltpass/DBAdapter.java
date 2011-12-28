package aisk.saltpass;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBAdapter {
    private static final String DATABASE_NAME = "saltpass.db";
    private static final String DATABASE_TABLE = "sites";
    static final String KEY_SITE = "site";
    private static final int DATABASE_VERSION = 3;

    private SQLiteDatabase db;
    private Context context;
    private saltDBOpenHelper dbHelper;

    public DBAdapter(Context _context) {
        this.context = _context;
        dbHelper = new saltDBOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLiteException {
        try {
          db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();    //Feel not nessesary
        }
    }

    public long insertSite(String siteName) {
        ContentValues siteValue = new ContentValues();
        siteValue.put(KEY_SITE, siteName);
        return db.insert(DATABASE_TABLE, null, siteValue);
    }

    public Cursor getAllSitesCursor() {
        //WTF.....
        Cursor cursor = db.query(DATABASE_TABLE, null, null, null, null, null, null);
        return cursor;
    }

    public void close() {
        db.close();
    }

    private static class saltDBOpenHelper extends SQLiteOpenHelper {

        public saltDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name ,factory, version);
        }

        private static final String DATABASE_CREATE = 
            "create table " + 
            DATABASE_TABLE + 
            "(_id integer primary key autoincrement, "+ 
            KEY_SITE +
            " text not null);";

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE + ";");
            onCreate(_db);
        }

    }
}

