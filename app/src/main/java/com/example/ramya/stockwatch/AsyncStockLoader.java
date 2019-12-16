package com.example.ramya.stockwatch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//asyncstockloader class
public class AsyncStockLoader extends AsyncTask<String, Integer, String> {

    private MainActivity mainActivity;//refers to main activity
    private static final String TAG = "StockSyncTask";//tag variable
    private String symbol;
    public static boolean run = false;
    public static String STKURL = "https://cloud.iexapis.com/stable/stock/",  appendURL="/quote?token=sk_d6dde12fa405401b85634f2e2daf98f4";
    public String REQURL;


    public AsyncStockLoader(MainActivity maact, String sym) {//constructor
        mainActivity = maact;//intializes member variables
        this.symbol = sym;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute:in onpostexecute stockloader");
        ArrayList<Stock> stklist = parseJSON(s); //parsed array list of stocks
        if (stklist != null)
            mainActivity.whenAsyncTaskStockDone(stklist); //calls the asynctask function in mainactivity
        run = false;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "doInBackground:in doinbackground stockloader");
        REQURL = STKURL + symbol + appendURL; //concatenation of stock url,symbol and token value
        Uri stkcompleteurl = Uri.parse(REQURL); //parse the uri
        String urlfinal = stkcompleteurl.toString(); //convert to string
        StringBuilder reqdata = new StringBuilder();
        String sentence;
        try {
            URL urlNew = new URL(urlfinal); //url object
            HttpURLConnection connect = (HttpURLConnection) urlNew.openConnection();
            connect.setRequestMethod("GET"); //establishes a http get connection
            InputStream is = connect.getInputStream(); //input stream object to fetch data from get request
            BufferedReader read = new BufferedReader(new InputStreamReader(is));
            while ((sentence = read.readLine()) != null) { //reads till end of file
                reqdata.append(sentence).append('\n');
            }

        } catch (Exception e) { //to catch exception
        }
        return reqdata.toString(); //returned converted string
    }

    public ArrayList<Stock> parseJSON(String s){
        Log.d(TAG, "parseJSON:in parsejson");
        ArrayList<Stock> stkList = new ArrayList<>();//arrylist of stocks
        try{
            if(!s.equals("httpserver.cc: Response Code 404")) { //checks for http request not found
                JSONObject jsonobj = new JSONObject(s);
                        String symb = jsonobj.getString("symbol");
                        String compname = jsonobj.getString("companyName");
                        String latestPrice = jsonobj.getString("latestPrice");
                        String change = jsonobj.getString("change");
                        Double stockChange = Double.parseDouble(change);
                        Double latestStockVal = Double.parseDouble(latestPrice);
                        String stkdiffper = jsonobj.getString("changePercent");
                        Double stkPercentDiff = Double.parseDouble(stkdiffper);
                        stkList.add(
                                new Stock(symb, compname, latestStockVal, stockChange, stkPercentDiff));
                        //append all values to stocklist arraylist
            }
        }
         catch (JSONException e1) //catches JSON exception
        {
            e1.printStackTrace(); //prints stacktrace on exception
        }
        return stkList;//returns stocklist
    }


}
