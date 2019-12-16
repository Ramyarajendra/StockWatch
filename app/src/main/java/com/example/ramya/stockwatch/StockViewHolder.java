package com.example.ramya.stockwatch;

import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

//stockviewholder class
public class StockViewHolder extends RecyclerView.ViewHolder {

    public TextView stockSymbol;
    public TextView stockValue;
    public TextView stockChange;
    public TextView stockName;
    public TextView stockChangePercent;

    public StockViewHolder(View v){
        super(v);
        stockSymbol = v.findViewById(R.id.stockSymbol);//refers to stocksymbol textfield
        stockValue = v.findViewById(R.id.stockValue);//refers to stockvalue textfield
        stockChange = v.findViewById(R.id.stockChange);//refers to stockchange textfield
        stockName = v.findViewById(R.id.stockName);//refers to stockname textfield
        stockChangePercent = v.findViewById(R.id.stockChangePercent);//refers to stockchangepercent tectfield
    }

}
