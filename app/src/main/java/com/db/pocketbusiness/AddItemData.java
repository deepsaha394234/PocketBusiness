package com.db.pocketbusiness;

public class AddItemData {

    public String productName, quantity, buyPrice,sellPrice,unit;
    public AddItemData(String productName , String quantity, String buyPrice, String sellPrice, String unit ){
        this.buyPrice = buyPrice;
        this.unit = unit;
        this.quantity = quantity;
        this.sellPrice = sellPrice;
        this.productName = productName;
    }


}
