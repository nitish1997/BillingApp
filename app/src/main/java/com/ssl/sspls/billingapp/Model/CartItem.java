package com.ssl.sspls.billingapp.Model;
public class CartItem {
    String code;
    String itemName;
    String quantity;
    String price;
    String totalPrice;
    String comment;
    String taxAmount;



    String taxRate;

    public CartItem(String Code, String itemName, String Quantity, String Price, String totalPrice, String Comment,String taxRate, String taxAmount)
    {
        this.code =Code;
        this.itemName =itemName;
        this.quantity =Quantity;
        this.price =Price;
        this.totalPrice = totalPrice;
        this.comment =Comment;
        this.taxRate =taxRate;
        this.taxAmount =taxAmount;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }
    public String getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }
}
