package com.ssl.sspls.billingapp.Model;
public class MenuDetailList {
    private String code;
    private String name;
    private String desc;
    private String rate;
    private String dept;
    private String head;
    private String status;
    private String imgpath;
    private String quantity;
    String TotalPrice;
    private String veg;
    private String tax;
    private String taxAmount;
    public MenuDetailList(String Code, String Name, String Desc, String Rate, String Dept, String Head, String Status,String  imgpath, String Quantity, String TotalPrice,String Veg, String Tax, String taxAmount)
    {
        this.code =Code;
        this.name =Name;
        this.desc =Desc;
        this.rate =Rate;
        this.dept =Dept;
        this.head =Head;
        this.status =Status;
        this.imgpath = imgpath;
        this.quantity =Quantity;
        this.TotalPrice =TotalPrice;
        this.veg =Veg;
        this.tax =Tax;
        this.taxAmount = taxAmount;
    }
    public String getImgpath() {
        return imgpath;
    }
    public String getTotalPrice() {
        return TotalPrice;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getRate() {
        return rate;
    }
    public void setRate(String rate) {
        this.rate = rate;
    }
    public String getDept() {
        return dept;
    }
    public void setDept(String dept) {
        this.dept = dept;
    }
    public String getHead() {
        return head;
    }
    public void setHead(String head) {
        this.head = head;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public String getVeg() {
        return veg;
    }
    public void setVeg(String veg) {
        this.veg = veg;
    }
    public String getTax() {
        return tax;
    }
    public void setTax(String tax) {
        this.tax = tax;
    }
    public String getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }
}
