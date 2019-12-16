package com.example.ramya.stockwatch;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import android.graphics.Color;
import java.util.List;

//stockadpater class
public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private List<Stock> stkList; //list of stocks
    private MainActivity mact; //refers to main activity

    //constructor
    public StockAdapter(List<Stock> liststock, MainActivity maAct)
    {
        this.stkList = liststock;
        mact =maAct;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup vgroup, int i) {
        View iV = LayoutInflater.from(vgroup.getContext()).inflate(R.layout.stock_layout,vgroup,false);
        iV.setOnClickListener(mact);
        iV.setOnLongClickListener((View.OnLongClickListener) mact);
        return new StockViewHolder(iV);

    }
    @Override
    public int getItemCount() {
        return stkList.size(); //gets count of stocks
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder stkVHolder, int position) {
        Stock stkData =stkList.get(position); //gets position of stock data
        stkVHolder.stockSymbol.setText(stkData.getStockSymbol()); //sets stock symbol
        stkVHolder.stockValue.setText(String.format("%.02f",stkData.getStockValue())); //sets stock value
        stkVHolder.stockChange.setText(String.format("%.02f",stkData.getStockChange())); //sets stock change
        String stockDiff = (String.format("%.02f",stkData.getStockChangePercent()));
        stkVHolder.stockChangePercent.setText("(" + stockDiff + "%)");
        stkVHolder.stockName.setText(stkData.getStockName());

        if(stkData.getStockChange()<0){ //if stockchange is less than 0
            stkVHolder.stockSymbol.setTextColor(Color.parseColor("#FF0040"));
            stkVHolder.stockName.setTextColor(Color.parseColor("#FF0040"));
            stkVHolder.stockValue.setTextColor(Color.parseColor("#FF0040"));
            stkVHolder.stockChange.setText('\u25BC'+stkData.getStockChange().toString());
            stkVHolder.stockChange.setTextColor(Color.parseColor("#FF0040"));
            stkVHolder.stockChangePercent.setTextColor(Color.parseColor("#FF0040"));
        }
        else {
            stkVHolder.stockSymbol.setTextColor(Color.parseColor("#00FF00"));
            stkVHolder.stockName.setTextColor(Color.parseColor("#00FF00"));
            stkVHolder.stockValue.setTextColor(Color.parseColor("#00FF00"));
            stkVHolder.stockChange.setText('\u25B2'+stkData.getStockChange().toString());
            stkVHolder.stockChange.setTextColor(Color.parseColor("#00FF00"));
            stkVHolder.stockChangePercent.setTextColor(Color.parseColor("#00FF00"));

        }


    }


}
