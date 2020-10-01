package com.ssl.sspls.billingapp.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.RequestQueue;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.ssl.sspls.billingapp.Localstorage.Datalayer;
import com.ssl.sspls.billingapp.R;
import com.ssl.sspls.billingapp.ShowMsg;

import java.util.List;
public class BaseActivity extends AppCompatActivity implements View.OnClickListener,ReceiveListener {
    ProgressDialog pDialog;
    String companyName,companyAddress1,companyAddress2,companyAddress3,companyGstNo,companyHeader1,companyHeader2,comapnyFooter1,
            comapnyFooter2,comapnyFooter3,comapnyFooter4,comapnyFooter5;
    StringBuilder textData;
    int count=1,totalItem,headno;
    Dialog dialog;
    SharedPreferences settings;
    RequestQueue requestQueue;
    String grandTotal,billNumber, returnBillDate, returnBillTime;
    String Ip, itemDetail ="";
    Context mContext = null;
    Printer  mPrinter = null;
    int modelNo,language;
    String targetIp,totalTax,totalPrice,know;
    public Datalayer datalayer;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        datalayer=new Datalayer(getApplicationContext());
    }
    @Override
    public void onClick(View v){}
    @Override
    public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s){}
    public String rightpad(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }
    public String leftpad(String text, int length) {
        return String.format("%" + length + "." + length + "s", text);
    }
    public static final String[] units = { "", "One", "Two", "Three", "Four",
            "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
            "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
            "Eighteen", "Nineteen" };
    public static final String[] tens = {
            "", 		// 0
            "",		// 1
            "Twenty", 	// 2
            "Thirty", 	// 3
            "Forty", 	// 4
            "Fifty", 	// 5
            "Sixty", 	// 6
            "Seventy",	// 7
            "Eighty", 	// 8
            "Ninety" 	// 9
    };
    public static String convert(final int n) {
        if (n < 0)
            return "Minus " + convert(-n);
        if (n < 20)
            return units[n];
        if (n < 100)
            return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
        if (n < 1000)
            return units[n / 100] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
        if (n < 100000)
            return convert(n / 1000) + " Thousand" + ((n % 10000 != 0) ? " " : "") + convert(n % 1000);
        if (n < 10000000)
            return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " : "") + convert(n % 100000);
        return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " : "") + convert(n % 10000000);
    }
    public void showProgressDialouge() {
        pDialog = new ProgressDialog(BaseActivity.this);
        pDialog.setMessage("Loading..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }
    public void hideProgressDialouge() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void getCompanydetail(){
        SQLiteDatabase db = datalayer.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CompanyDetail", null);
        if (cursor.moveToFirst()) {
            do {
                companyName = cursor.getString(cursor.getColumnIndex("companyName"));
                companyAddress1 = cursor.getString(cursor.getColumnIndex("companyAddress1"));
                companyAddress2 = cursor.getString(cursor.getColumnIndex("companyAddress2"));
                companyAddress3 = cursor.getString(cursor.getColumnIndex("companyAddress3"));
               // comapnyPhone = cursor.getString(cursor.getColumnIndex("comapnyPhone"));
               // companyEmail = cursor.getString(cursor.getColumnIndex("companyEmail"));
                companyGstNo=cursor.getString(cursor.getColumnIndex("companyGstNo"));
                companyHeader1 = cursor.getString(cursor.getColumnIndex("companyHeader1"));
                companyHeader2 = cursor.getString(cursor.getColumnIndex("companyHeader2"));
                comapnyFooter1=cursor.getString(cursor.getColumnIndex("comapnyFooter1"));
                comapnyFooter2=cursor.getString(cursor.getColumnIndex("comapnyFooter2"));
                comapnyFooter3=cursor.getString(cursor.getColumnIndex("comapnyFooter3"));
                comapnyFooter4=cursor.getString(cursor.getColumnIndex("comapnyFooter4"));
                comapnyFooter5=cursor.getString(cursor.getColumnIndex("comapnyFooter5"));
            }while (cursor.moveToNext());
            cursor.close();
            datalayer.close();
        }
    }
    public boolean initializeObject() {
        try {
            mPrinter = new Printer(modelNo,language,mContext);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "Printer", mContext);
            return false;
        }
        mPrinter.setReceiveEventListener(this);
        return true;
    }
    public void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }
    public boolean printData() {
        if (mPrinter == null)
            return false;
        if (!connectPrinter()) {
            return false;
        }
        PrinterStatusInfo status = mPrinter.getStatus();
        dispPrinterWarnings(status);
        if (!isPrintable(status)) {
            ShowMsg.showMsg(makeErrorMessage(status), mContext);
            try { mPrinter.disconnect();}
            catch (Exception ignored) {}
            return false;
        }
        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "sendData", mContext);
            try {
                mPrinter.disconnect();
            }
            catch (Exception ignored) {

            }
            return false;
        }
        return true;
    }
    public boolean connectPrinter() {
        boolean isBeginTransaction = false;
        if (mPrinter == null) {
            //  Toast.makeText(MainActivity.this,"connect Printer",Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            mPrinter.connect(targetIp, Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "connect", mContext);
            return false;
        }
        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", mContext);
        }
        if (!isBeginTransaction) {
            try {
                mPrinter.disconnect();
            }
            catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }
        return true;
    }
    public void disconnectPrinter() {
        if (mPrinter == null) {
            // Toast.makeText(MainActivity.this,"disconnect Printer",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            mPrinter.endTransaction();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "endTransaction", mContext);
                }
            });
        }
        try {
            mPrinter.disconnect();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "disconnect", mContext);
                }
            });
        }
        finalizeObject();
    }
    public boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }
        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else return status.getOnline() != Printer.FALSE;

    }
    public String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }
        return msg;
    }
    public void dispPrinterWarnings(PrinterStatusInfo status) {
        String warningsMsg = "";
        if (status == null)
            return;
        if (status.getPaper() == Printer.PAPER_NEAR_END)
            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1){}
    }
}

