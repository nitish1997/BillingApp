package com.ssl.sspls.billingapp.Model;

public class UnholdBillDetail {
    String time;
    String quantity;
    String totalPrice;
    String billNo;
    public UnholdBillDetail(String billNo,String time,String quantity,String totalPrice)
    {
        this.billNo=billNo;
        this.time=time;
        this.quantity=quantity;
        this.totalPrice=totalPrice;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public String getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getBillNo() {
        return billNo;
    }
    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }
}
