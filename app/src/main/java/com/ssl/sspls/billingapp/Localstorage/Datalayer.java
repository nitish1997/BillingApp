package com.ssl.sspls.billingapp.Localstorage;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ssl.sspls.billingapp.Model.MenuDetailList;

public class Datalayer extends SQLiteOpenHelper {
    //////////////////Table Name////////////////////////////
    public static final String TABLE_MENU = "MenuDetail";
    //public static final String TABLE_HEADING = "MenuDetail";
    public static final String CODE ="code";
    public static final String NAME ="name";
    public static final String DESCRIPTION ="description";
    public static final String RATE ="rate";
    public static final String DEPT ="department";
    public static final String HEADING ="heading";
    public static final String STATUS ="status";
    public static final String IMAGE ="imgPath";
    public static final String QUANTITY ="quantity";
    public static final String TOTALPRICE ="totalPrice";
    public static final String VEG ="veg";
    public static final String TAXRATE ="taxRate";
    public static final String TAXAMOUNT="taxAmount";

    private SQLiteDatabase mDb;
    // Database Information
    static final String DB_NAME = "MenuDetail.db";
    // database version
    static final int DB_VERSION = 1;
    public Datalayer(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table CompanyDetail(companyName text,companyAddress1 text,companyAddress2 text,companyAddress3 text,comapnyPhone text,companyEmail text,companyGstNo,companyHeader1 text,companyHeader2 text,comapnyFooter1 text,comapnyFooter2 text,comapnyFooter3 text,comapnyFooter4 text,comapnyFooter5 text)");
        db.execSQL("create table SettlementDetail(payMode text,mode text)");
        db.execSQL("create table MenuDetail("+CODE+" text,"+NAME+" text,"+DESCRIPTION+" text,"+RATE+" text,"+DEPT+" text,"+HEADING+" text,"+STATUS+" text,"+IMAGE+" text,"+QUANTITY+"  text,"+TOTALPRICE+"  text,"+VEG+"  text,"+TAXRATE+"  text,"+TAXAMOUNT+"  text)");
        db.execSQL("create table CartDetail(code text,name text,quantity text,rate text,totalPrice text,comment text,taxRate text,finalAmount text)");
        db.execSQL("create table CartOnHoldDetail(billNo text,code text,name text,quantity text,rate text,totalPrice text,comment text,taxRate text,dateTimeVal text,finalAmount text)");
        db.execSQL("create table WaiterDetail(crCode text,crName text,password text)");
        db.execSQL("create table PaymentDetail(id Integer Primary Key Autoincrement,modeName text,modetype text,amount text,TenderAmount text,RefundedAmount text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP Table IF EXISTS CompanyDetail");
        db.execSQL("DROP Table IF EXISTS SettlementDetail");
        db.execSQL("DROP Table IF EXISTS PaymentDetail");
        db.execSQL("DROP Table IF EXISTS "+TABLE_MENU+"");
        db.execSQL("DROP Table IF EXISTS CartDetail");
        db.execSQL("DROP Table IF EXISTS CartOnHoldDetail");
        db.execSQL("DROP Table IF EXISTS WaiterDetail");
        onCreate(db);
    }

    public void AddpaymentDetail(String mode,String modeType,String amount,String tenderAmount,String refundedAmount) {
        SQLiteDatabase sq1=getWritableDatabase();
        String insert=("insert into PaymentDetail(modeName,modetype,amount,TenderAmount,RefundedAmount)values('"+mode+"','"+modeType+"','"+amount+"','"+tenderAmount+"','"+refundedAmount+"')");
        sq1.execSQL(insert);
        sq1.close();
    }

    public void AddCompanyDetail(String companyName, String companyAddress1, String companyAddress2, String companyAddress3,String comapnyPhone, String companyEmail, String companyGstNo, String companyHeader1,String companyHeader2, String comapnyFooter1, String comapnyFooter2, String comapnyFooter3,String comapnyFooter4,String comapnyFooter5){
        SQLiteDatabase sq1=getWritableDatabase();
        String insert=("insert into CompanyDetail(companyName,companyAddress1,companyAddress2,companyAddress3,comapnyPhone,companyEmail,companyGstNo,companyHeader1,companyHeader2,comapnyFooter1,comapnyFooter2,comapnyFooter3,comapnyFooter4,comapnyFooter5)values('"+companyName+"','"+companyAddress1+"','"+companyAddress2+"','"+companyAddress3+"','"+comapnyPhone+"','"+companyEmail+"','"+companyGstNo+"','"+companyHeader1+"','"+companyHeader2+"','"+comapnyFooter1+"','"+comapnyFooter2+"','"+comapnyFooter3+"','"+comapnyFooter4+"','"+comapnyFooter5+"')");
        sq1.execSQL(insert);
        sq1.close();
    }
    public void AddSettlementDetail(String payMode,String Mode){
        SQLiteDatabase sq1=getWritableDatabase();
        String insert=("insert into SettlementDetail(payMode,mode)values('"+payMode+"','"+Mode+"')");
        sq1.execSQL(insert);
        sq1.close();
    }
    public void AddMenu(MenuDetailList detailList) {
        ContentValues cv = new ContentValues();
        cv.put(CODE,detailList.getCode());
        cv.put(NAME,detailList.getName());
        cv.put(DESCRIPTION,detailList.getDesc());
        cv.put(RATE,detailList.getRate());
        cv.put(DEPT,detailList.getDept());
        cv.put(HEADING,detailList.getHead());
        cv.put(STATUS,detailList.getStatus());
        cv.put(IMAGE,detailList.getImgpath());
        cv.put(QUANTITY,detailList.getQuantity());
        cv.put(TOTALPRICE,detailList.getTotalPrice());
        cv.put(VEG,detailList.getVeg());
        cv.put(TAXRATE,detailList.getTax());
        cv.put(TAXAMOUNT,detailList.getTaxAmount());
        mDb.insert("MenuDetail", null, cv);
    }
    public void AddCartDetail(String Code, String Name_Item, String Quantity, String Price, String Total_price, String Comment, String RateTax, String FinalAmount){
        SQLiteDatabase sq1=getWritableDatabase();
        String insert=("insert into CartDetail(code,name,quantity,rate,totalPrice,comment,taxRate,finalAmount)values('"+Code+"','"+Name_Item+"','"+Quantity+"','"+Price+"','"+Total_price+"','"+Comment+"','"+RateTax+"','"+FinalAmount+"')");
        sq1.execSQL(insert);
        sq1.close();
    }
    public void AddCartOnHoldDetail(String billNo,String Code, String Name, String Quantity, String Price, String totalPrice, String Comment,String taxAmount,String dateTime,String finalAmount){
        SQLiteDatabase sq1=getWritableDatabase();
        String insert=("insert into CartOnHoldDetail(billNo,code,name,quantity,rate,totalPrice,comment,taxRate,dateTimeVal,finalAmount)values('"+billNo+"','"+Code+"','"+Name+"','"+Quantity+"','"+Price+"','"+totalPrice+"','"+Comment+"','"+taxAmount+"','"+dateTime+"','"+finalAmount+"')");
        sq1.execSQL(insert);
        sq1.close();
    }
    public void DeleteMenuDetail(){
        SQLiteDatabase sq=getWritableDatabase();
        String delete="delete from "+TABLE_MENU+"";
        sq.execSQL(delete);
        sq.close();
    }
    public void DeleteOnHoldDetail(String biiNo){
        SQLiteDatabase sq=getWritableDatabase();
        String update=("delete from CartOnHoldDetail where billNo='"+biiNo+"'");
        sq.execSQL(update);
        sq.close();
    }
    public void DeleteCartItem(String Code){
        SQLiteDatabase sq=getWritableDatabase();
        String update=("delete from CartDetail where code='"+Code+"'");
        sq.execSQL(update);
        System.out.println("your query: "+update);
        sq.close();
    }
    public void DeleteCart(){
        SQLiteDatabase sq=getWritableDatabase();
        String update=("delete from CartDetail");
        sq.execSQL(update);
        sq.close();
    }
    public void UpdateMenuQuantityDetail(String Code, String Quantity, String Total_price,String TaxAmount){
        SQLiteDatabase sq=getWritableDatabase();
        String update=("UPDATE MenuDetail set quantity='"+Quantity+"',totalPrice='"+Total_price+"',taxAmount='"+TaxAmount+"' where code='"+Code+"'");
        sq.execSQL(update);
        System.out.println("your Updated");
        sq.close();
    }
    public void UpdateCartQuantity(String Code, String quantity,String totalPrice){
        SQLiteDatabase sq=getWritableDatabase();
        String update=("UPDATE CartDetail set quantity='"+quantity+"',totalPrice='"+totalPrice+"' where code='"+Code+"'");
        sq.execSQL(update);
        sq.close();
    }
    public void UpdateCartQuantityWithTaxAmmount(String Code, String quantity,String totalPrice,String taxRate){
        SQLiteDatabase sq=getWritableDatabase();
        String update=("UPDATE CartDetail set quantity='"+quantity+"',totalPrice='"+totalPrice+"',finalAmount='"+taxRate+"' where code='"+Code+"'");
        sq.execSQL(update);
        sq.close();
    }
    public void UpdateCartComment(String Code, String comment){
        SQLiteDatabase sq=getWritableDatabase();
        String update=("UPDATE CartDetail set comment='"+comment+"' where code='"+Code+"'");
        sq.execSQL(update);
        System.out.println("your query: "+update);
        sq.close();
    }
    public void DeleteCompanyDetail(){
        SQLiteDatabase sq=getWritableDatabase();
        String delete="delete from CompanyDetail";
        sq.execSQL(delete);
        sq.close();
    }
    public void DeleteSettlementDetail(){
        SQLiteDatabase sq=getWritableDatabase();
        String delete="delete from SettlementDetail";
        sq.execSQL(delete);
        sq.close();
    }
    public Datalayer open() throws SQLException {
        mDb = getWritableDatabase();
        return this;
    }
    public void DeletePaymentDetail(){
        SQLiteDatabase sq=getWritableDatabase();
        String delete="delete from PaymentDetail";
        sq.execSQL(delete);
        sq.close();
    }
    public void UpdatePaymentdetail(String id, String TenderAmount,String RefundedAmount){
        SQLiteDatabase sq=getWritableDatabase();
        String update=("UPDATE PaymentDetail set TenderAmount='"+TenderAmount+"',RefundedAmount='"+RefundedAmount+"' where code='"+id+"'");
        sq.execSQL(update);
        System.out.println("your query: "+update);
        sq.close();
    }
}
