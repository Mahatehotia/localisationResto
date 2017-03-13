package com.example.mahatehotia.kotnikralijaona;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {


    // All Static variables
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BaseResto";
    public static final String TABLE_RESTO = "Restaurant";

    // Contacts Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NOM = "nom";
    public static final String KEY_Adresse = "adresse";

    private Context context = null;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMC_TABLE = "CREATE TABLE " + TABLE_RESTO + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NOM + " STRING,"
                + KEY_Adresse + " STRING"
                + ")";
        db.execSQL(CREATE_IMC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTO);
        // Create tables again
        onCreate(db);
    }

    public void addResto(RESTO resto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOM, resto.getNom());
        values.put(KEY_Adresse, resto.getAdresse());
        db.insert(TABLE_RESTO, null, values);
        db.close();
    }

    public ArrayList<RESTO> recupereLesRestos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String r = "SELECT * FROM " + TABLE_RESTO +" ORDER BY " + KEY_ID  + " DESC LIMIT 5;";
        Cursor resc = db.rawQuery(r, null);
        ArrayList<RESTO> lesRestos = new ArrayList<RESTO>();
        while (resc.moveToNext()) {
            //RESTO resto = new RESTO(resc.getInt(0),
            //        resc.getString(1),
            //        resc.getString(2));
            //lesRestos.add(resto);
        }
        resc.close();
        return lesRestos;
    }

}