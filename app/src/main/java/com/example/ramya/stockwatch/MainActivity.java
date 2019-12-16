package com.example.ramya.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.EditText;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.widget.Toast;
import java.util.Collections;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener{

    private List<Stock> stkList =new ArrayList<>(); //arraylist of stocks
    private List<String> symlist = new ArrayList<>(); //arraylist of stock symbols
    private DatabaseHandler dbhandler; //refers to database handler
    private SwipeRefreshLayout swipe; //refers to swipe refresh layout
    public String useripval; //userinput string
    private RecyclerView rView; //refers to recycler view
    private StockAdapter stkadap; //refers to stock adapter
    private int lastpos; //latest position
    private static final String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipe = findViewById(R.id.swiper); //to set swipe refresh
        rView = findViewById(R.id.recycle); //refers to recycler view
        stkadap = new StockAdapter(stkList,this);//object of stock adapter
        rView.setAdapter(stkadap); //sets recycler view
        rView.setLayoutManager(new LinearLayoutManager(this));
        dbhandler = new DatabaseHandler(this); //object of database handler
        dbhandler.dumpdbtoLog();
        ArrayList<String[]> lis = dbhandler.loadStocks(); //arraylist of stocks fetched from database
        for(int j=0;j<lis.size();j++)
        {
            stkList.add(new Stock(lis.get(j)[0],lis.get(j)[1],0.0,0.0,0.0));
            symlist.add(stkList.get(j).getStockSymbol());//append to symbol list
        }
        Collections.sort(stkList,Collections.reverseOrder());//sorts the stocklist
        if(stkList.size()==0){ //checks if size is 0
            Toast.makeText(this, "No stock added", Toast.LENGTH_SHORT).show();
        }
        else{
            if (internetWorking()) { //checks for internet connectivity
                updateStock(); //updates stock on connection
                stkadap.notifyDataSetChanged();
            } else {
                internetError(); //else pops up error message
            }}
        stkadap.notifyDataSetChanged();
        swiper();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateStock(){
        if (internetWorking()){ //checks for internet connectivity
            int lisize = stkList.size(); //stock size
            updateStockVal(lisize); //update stock size
        }else{
            internetError();
        }
        dbhandler.dumpdbtoLog(); //dump to database
        stkadap.notifyDataSetChanged();
    }

void updateStockVal(int listval)
{
    for(int i=0; i< listval;i++){
        String symb = stkList.get(stkList.size()-1).getStockSymbol();//to get stock symbol
        int pos = stkList.size()-1; //gets position
        dbhandler.deleteStock(stkList.get(pos).getStockSymbol()); //delete stock of specified position
        stkList.remove(pos); //remove from stocklist
        symlist.remove(pos); //remove from symbollist
        stkadap.notifyDataSetChanged();//notify adapter of change
        AsyncStockLoader.run = true;
        new AsyncStockLoader(this,symb).execute();
    }
}


    @RequiresApi(api = Build.VERSION_CODES.M)
    void swiper()
    {
        if(internetWorking()) { //checks for internet connectivity
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateStock();
                    swipe.setRefreshing(false);
                }
            });
        }
        else{
            internetError();
        }
    }

    public void whenAsyncTaskStockDone (ArrayList<Stock> stkvals){ //called by async task
        Log.d(TAG, "whenAsyncTaskStockDone:in mainactivity asynctask");
        if (stkvals.size() != 0){ //checks for stock size
            stkList.addAll(stkvals); //appends to list
            Collections.sort(stkList); //sorts the list
            addstockValues(stkvals);
            stkadap.notifyDataSetChanged(); //notifies adapter
        }else {
            errorStockNotfound(stkvals.get(0).toString()); //else stock not found
        }

    }


    void addstockValues(ArrayList<Stock> stkval){ //function to add stock values
        if (! symlist.contains(stkval.get(0).getStockSymbol())) {
            dbhandler.addStock(stkval); //adds stockvalues to database
            symlist.add(0, stkval.get(0).getStockSymbol()); //appends to symbol list
        }
        stkadap.notifyDataSetChanged(); //notifies adapter
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean internetWorking() { //checks for internet connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network ninfo=cm.getActiveNetwork(); //gets network info
        NetworkCapabilities checkconnection=cm.getNetworkCapabilities(ninfo);
        if (checkconnection != null) {
            return true;
        } else {
            return false;
        }
    }
    public void errorStockNotfound(String symbolValue){ //checks if stock symbol is found
        final AlertDialog alertDialogFDMatching = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Symbol not found : symbolValue")
                .setMessage("Data for stock symbol")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {    }})
                .show();
    }

    private void internetError () {//checks if internet is not connected
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(this)//pops alert dialog
                .setCancelable(true)
                .setTitle("No Network Connection")
                .setMessage("Stock cannot be added without internet connection")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {    }})
                .show();

    }
    public void whenAsyncTaskSymbolIsDone(List<String[]> lisym) {//called by asynctask
        if (lisym.size() == 0){ //checks for list size empty
            final AlertDialog alertDialogStockMatching = new AlertDialog.Builder(this)//pops alert dialog
                    .setCancelable(true)
                    .setTitle("Symbol not found: "+ useripval)
                    .setMessage("Data for stock symbol")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {    }})
                    .show();
        }
        else if (lisym.size() == 1) //if list size is 1
        {
            oneStockSelect(lisym.get(0)[0]);
        }
        else{ //to choose from a list of stocks
            final String[] posssym = new String[lisym.size()];
            String[] lisposname = new String[lisym.size()];
            for (int i = 0; i< lisym.size(); i++){ //loops over the list to fetch list position name
                posssym[i]=lisym.get(i)[0];
                lisposname[i]=lisym.get(i)[0] + "-" + lisym.get(i)[1];
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make a selection"); //alert dialog box
            builder.setNegativeButton("NeverMind",dialogClickListenerDelete);//on negative selection
            builder.setItems(lisposname, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {oneStockSelect(posssym[which]);}});
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        stkadap.notifyDataSetChanged(); //notifies adapter
    }

    private void searchStocks(String stksym){ //searches for stock symbols
        if(stksym.isEmpty() || stksym.equals("")){ //checks for empty value
            final AlertDialog alertDialogDuplicate = new AlertDialog.Builder(this)//pops alert dialog
                    .setCancelable(true)
                    .setTitle("You have not entered any symbol")
                    .setMessage("Please enter symbol")
                    .setNegativeButton("OK",dialogClickListenerDelete)
                    .show();
        }
        else if(symlist.contains(stksym)){ //checks if symbol is already present
            final AlertDialog alertDialogDuplicate = new AlertDialog.Builder(this)//pops alert dialog
                    .setCancelable(true)
                    .setTitle("Duplicate Stock")
                    .setMessage("Stock symbol "+ stksym +" is already displayed")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else{

            AsyncNameLoader.run = true;
            new AsyncNameLoader(this, stksym).execute();

        }

    }

    @Override

    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.add_menu,m);
        return true;
    }



    public void oneStockSelect (String symb) //if one stock is selected
    {
        if(symlist.contains(symb)){ //checks if the symbol list contains the passed parameter
            final AlertDialog alertDialogDuplicate = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle("Duplicate Stock")
                    .setMessage("Stock symbol "+ symb + " is already displayed")
                    .show();
        }
        else{
            AsyncStockLoader.run = true;
            new AsyncStockLoader(this, symb).execute();
        }
    }
    @Override
    public boolean onLongClick(View v) {
        int pos = rView.getChildLayoutPosition(v); //gets position
        Stock stk = stkList.get(pos); //stock is fetched
        lastpos = pos;
        final AlertDialog alertDialogDelete = new AlertDialog.Builder(this)//alert dialog to delete stock on long press
                .setCancelable(false)
                .setTitle("Delete Stock")
                .setMessage("Delete Stock Symbol" + stk.getStockSymbol()+" ?")
                .setPositiveButton("Yes",dialogClickListenerDelete)
                .setNegativeButton("No",dialogClickListenerDelete)
                .setIcon(android.R.drawable.ic_menu_delete)
                .show();
        return false;
    }

    DialogInterface.OnClickListener dialogClickListenerDelete = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE: //on positive selection
                    int position = lastpos; //fetch position
                    dbhandler.deleteStock(stkList.get(position).getStockSymbol());//delete stock from handler
                    stkList.remove(position); //remove from stock list
                    symlist.remove(position); //remove from symbol list
                    stkadap.notifyDataSetChanged();
                    stkadap.notifyDataSetChanged();
                    break;

                case DialogInterface.BUTTON_NEGATIVE: //on negative selection
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addNewStock: //if option selected is to add new stock item
                LayoutInflater liflat = LayoutInflater.from(MainActivity.this);
                View v = liflat.inflate(R.layout.searchstock_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(v);
                final EditText ui = (EditText) v
                        .findViewById(R.id.search); //refers to the edit text field
                ui.setFilters(new InputFilter[] {new InputFilter.AllCaps()}); //sets it all capital values
                ui.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                ui.setGravity(Gravity.CENTER_HORIZONTAL);

                final String stockSymbolAdded = ui.getText().toString();
                useripval = ui.getText().toString();//user input fetched from edit text field
                if(internetWorking()){ //checking for internet connectivity
                builder.setTitle("Stock Selection")
                        .setMessage("Please enter a Stock Symbol")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                searchStocks(ui.getText().toString()); //searches for stock symbols

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"Stock not added",Toast.LENGTH_SHORT).show();
                                //displays toast as stock not added on negative button selection
                            }
                        })
                        .show();}
                else{
                    internetError();//else internet not connected error
                }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        int pos = rView.getChildLayoutPosition(v); //fetches position
        Stock stk = stkList.get(pos);
        String symdata;
        if (internetWorking()){ //checks for internet connectivity
            symdata=stk.getStockSymbol(); //gets stock symbol
            String url = "http://www.marketwatch.com/investing/stock/" + symdata;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url)); //parses the URI
            startActivity(i);

        }else {
            internetError();//else internet not connected

        }
    }



}
