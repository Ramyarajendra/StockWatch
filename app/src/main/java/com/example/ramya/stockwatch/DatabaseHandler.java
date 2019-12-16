package com.example.ramya.stockwatch;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//database handler class
public class DatabaseHandler extends SQLiteOpenHelper{
    private SQLiteDatabase db; //refers to sql database
    private static final String SYMB = "Symbol";
    private static final String TAB = "StockWatchTable";
    private static final String TAG = "DatabaseHandler";
    private static final String DBNAME = "StockWatchDB";
    private static final String COMPNAME = "CompanyName";
    private MainActivity ma; //refers to main actvity
    private static final int DBV = 1;

    public DatabaseHandler(MainActivity mact) {//constructor
        super(mact, DBNAME,null, DBV);//calls parent constructor
        ma = mact;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //executes sql query
       db.execSQL( "CREATE TABLE " + TAB + " (" + SYMB + " TEXT not null unique," + COMPNAME + " TEXT not null)");
    }

    public void dumpdbtoLog() {
        Cursor cur = db.rawQuery("Select * from " + TAB,null);//sql query from database
        if(cur!=null){
            cur.moveToFirst();
            for(int i=0;i<cur.getCount();i++){
                String symbol = cur.getString(0); //fetches symbol & company name
                String compName = cur.getString(1);
                Log.d(TAG,"Dump data : " + String.format("%s %-18s",SYMB + ":",symbol) + String.format("%s %-18s", COMPNAME +":",compName));
                cur.moveToNext();
            }
            cur.close();
        }
    }

    public ArrayList<String[]> loadStocks() {
        //function to load all the stocks
        ArrayList<String[]> stkList = new ArrayList<>();//arraylist of stocks
        Cursor cur = db.query(
                TAB,
                new String[]{SYMB, COMPNAME},
                null,
                null,
                null,
                null,
                null);
        if (cur != null) {
            cur.moveToFirst();
            for (int i = 0; i < cur.getCount(); i++) { //loops over stocks to fetch symbol and company name
                String symb = cur.getString(0);
                String comp = cur.getString(1);
                stkList.add(new String[] {symb, comp});
                cur.moveToNext();
            }
            cur.close();
        }
        Log.d(TAG, "loadStocks: DONE");
        return stkList; //returns stock list
    }

    public void addStock(ArrayList<Stock> stkVal) {
        ContentValues val = new ContentValues();
        val.put(SYMB, stkVal.get(0).getStockSymbol()); //adds symbol to db
        val.put(COMPNAME, stkVal.get(0).getStockName());//adds company name to db
        long key = db.insert(TAB, null, val);
        }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void deleteStock(String symbName) {
        //function to delete stock from db
        int cnt = db.delete(TAB, SYMB + " = ?", new String[]{symbName});
    }
}
