package com.example.ramya.stockwatch;

import org.json.JSONArray;
import org.json.JSONObject;
import android.net.Uri;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//AsyncNameLoader class
public class AsyncNameLoader extends AsyncTask<String, Integer, String> {

    ArrayList<String[]> symbolList = new ArrayList<>(); //arraylist of symbols
    private static final String TAG = "StockSyncTask"; //tag
    private MainActivity mainActivity; //refers to main activity
    public static boolean run = false;
    public static String URLSYMBOL = "https://api.iextrading.com/1.0/ref-data/symbols";//points to URl of symbols
    private List<String[]> listsymcomp = new ArrayList<>();//arraylist of symbol companies
    private String symbol;

    public AsyncNameLoader(MainActivity mact, String symb) { //constructor
        mainActivity = mact;
        this.symbol = symb.replaceAll("\\s+",""); //formats all the symbols
        if (this.symbol.indexOf('.') != -1 ){
            Log.d(TAG, "Async create ignore true");
            run = true;
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute:in postexecute asyncnameloader");
        super.onPostExecute(s);
        mainActivity.whenAsyncTaskSymbolIsDone(listsymcomp);
        run = false;
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: indoinbackground asyncnameloader");
        Uri stkUri = Uri.parse(URLSYMBOL); //parses the stock URI
        String urlToUse = stkUri.toString(); //converts to string
        StringBuilder reqdata = new StringBuilder();//string builder object for data
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); //establishes a http get connection
            InputStream is = connection.getInputStream();//to fetch input stream from get request
            BufferedReader read = new BufferedReader(new InputStreamReader(is));
            String l;
            while ((l = read.readLine()) != null) { //reads till end of file
                reqdata.append(l).append('\n'); //appends new line
            }
            String symallval = reqdata.toString(); //converts to string
            try {
                JSONArray jsarr = new JSONArray(symallval); //json object for symbol value
                for (int i=0;i<jsarr.length();i++ ){
                    JSONObject jStock = (JSONObject) jsarr.get(i);
                    String symdata = jStock.getString("symbol"); //to get string from json symbol
                    String compname = jStock.getString("name");//get string from json company name
                    symbolList.add(new String[]{ symdata,compname});

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();//checks for exception
            }
            ArrayList<String[]> symarr=symbolList; //arraylist of symbols
            for(int i=0;i<symarr.size();i++){
                if(symarr.get(i)[0].startsWith(symbol)){
                    String sym =symarr.get(i)[0].toString(); //symbol
                    String compName=symarr.get(i)[1].toString();//company
                    listsymcomp.add(new String[]{sym,compName});
                }
            }
        }
        catch (Exception e){
            Log.e(TAG,"doInBackgroundException :",e);//checks for exception
        }
        return "done";
    }

}
