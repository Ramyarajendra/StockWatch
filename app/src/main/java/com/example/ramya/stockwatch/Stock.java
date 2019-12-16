package com.example.ramya.stockwatch;

import java.io.Serializable;

public class Stock implements Serializable,Comparable<Stock>{

    private String stockSymbol;
    private String stockName;
    private Double stockValue;
    private Double stockChange;
    private Double stockChangePercent;

    //constructor
    public Stock(){
    }
    //constructor
    public Stock(String stksymbol, String stkname, Double stkval, Double stkchange, Double stkchangepercent) {
        //initializes the members of the class
        this.stockSymbol = stksymbol;
        this.stockValue = stkval;
        this.stockChange = stkchange;
        this.stockName = stkname;
        this.stockChangePercent = stkchangepercent;
    }

    //getters and setters
    public String getStockSymbol() {
        return stockSymbol;
    }

    public Double getStockValue() {
        return stockValue;
    }

    public Double getStockChange() {
        return stockChange;
    }

    public String getStockName() {
        return stockName;
    }

    public Double getStockChangePercent() {
        return stockChangePercent;
    }

    public void setStockSymbol(String stksymbol) {
        this.stockSymbol = stksymbol;
    }

    public void setStockValue(Double stkval) {
        this.stockValue = stkval;
    }

    public void setStockChange(Double stkchange) {
        this.stockChange = stkchange;
    }

    public void setStockName(String stkname) {
        this.stockName = stkname;
    }

    public void setStockChangePercent(Double stockChangePercent){
        this.stockChangePercent = stockChangePercent;
    }

    //overriding tostring function
    @Override
    public String toString() {
        return this.stockSymbol + " " + this.stockName + " " + this.stockValue + " "+ this.stockChange + " " + this.stockChangePercent;
    }
    //overriding compareto function
    @Override
    public int compareTo(Stock stkdata) {
        return this.getStockSymbol().compareTo(stkdata.getStockSymbol());
    }
}
