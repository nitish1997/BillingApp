package com.ssl.sspls.billingapp.Activity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.ssl.sspls.billingapp.Localstorage.Datalayer;
import com.ssl.sspls.billingapp.Model.CartItem;
import com.ssl.sspls.billingapp.Model.PaymentDetailListType;
import com.ssl.sspls.billingapp.R;
import com.ssl.sspls.billingapp.ShowMsg;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PayMentDetail extends BaseActivity{
    RecyclerView modeList,modeTypeList,paymentListView;

    LinearLayout Exit,done;
    ArrayList<String> listDataHeader,listDataChild;
    int rowIndexOfList,rowIndexOfList1;
    String Amount,waiterCode,print;
    Button addPayment;
    String modeName="Cash",modeType="Cash";
    int i=1;
    EditText enterAmount;
    ArrayList<PaymentDetailListType> payementList;
    TextView billamount,balance,tenderamount;
    Boolean ischeck = false;
    Boolean ischeck1 = false;

    ArrayList<CartItem> cartItemListDetail;
    Context context;

    Double paymentAmount;
    String subtotal,tax,roundoff,grossamount,Item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_ment_detail);
        getSupportActionBar().hide();
        this.context = this;
        settings =getSharedPreferences("MyPref", MODE_PRIVATE);
        waiterCode=settings.getString("waiterCode","");
        print=settings.getString("print","");
        modelNo=settings.getInt("modelNo",0);
        language=settings.getInt("language",0);//modeType
        targetIp=settings.getString("targetIp","");

        subtotal = getIntent().getStringExtra("subtotal");
        tax = getIntent().getStringExtra("tax");
        roundoff = getIntent().getStringExtra("roundoff");
        grossamount = getIntent().getStringExtra("grossamount");
        Item = getIntent().getStringExtra("totalItem");

        requestQueue=Volley.newRequestQueue(PayMentDetail.this);
        payementList=new ArrayList<>();
        cartItemListDetail=new ArrayList<>();
        paymentListView=findViewById(R.id.payment);
        mContext=this;
        init();
        enterAmount.setText(grossamount);
        billamount.setText(grossamount);
        balance.setText(grossamount);
        tenderamount.setText("0.0");
        getData();
        getPaymentData();
        GetCartItem();
        enterAmount.requestFocus();
        done.setVisibility(View.INVISIBLE);
        totalPrice=billamount.getText().toString();
    }
    public void GetCartItem(){
        cartItemListDetail.clear();
        SQLiteDatabase db = datalayer.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CartDetail", null);
        if (cursor.moveToFirst()) {
            do {
                String code = cursor.getString(cursor.getColumnIndex("code"));
                String itemName = cursor.getString(cursor.getColumnIndex("name"));
                String price = cursor.getString(cursor.getColumnIndex("rate"));
                String quantity=cursor.getString(cursor.getColumnIndex("quantity"));
                String totalPrice=cursor.getString(cursor.getColumnIndex("totalPrice"));
                String comment=cursor.getString(cursor.getColumnIndex("comment"));
                String taxRate=cursor.getString(cursor.getColumnIndex("taxRate"));
                String taxAmount=cursor.getString(cursor.getColumnIndex("finalAmount"));
                cartItemListDetail.add(new CartItem(code,itemName,quantity,price,totalPrice,comment,taxRate,taxAmount));
            }while (cursor.moveToNext());
            cursor.close();
            datalayer.close();
        }
        //GetCartAmount();
    }
    public void getData(){
        SQLiteDatabase db = datalayer.getReadableDatabase();
        listDataHeader=new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT payMode FROM SettlementDetail where mode='"+"Type"+"'", null);
        if (cursor.moveToFirst()) {
            do {
                String payMode = cursor.getString(cursor.getColumnIndex("payMode"));
                if(!payMode.equals("Cheque"))
                    listDataHeader.add(payMode);
            }while (cursor.moveToNext());
            cursor.close();
            datalayer.close();
        }
        Main_ItemAdapter itemAdapter = new Main_ItemAdapter(PayMentDetail.this,listDataHeader);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PayMentDetail.this);
        modeList.setLayoutManager(mLayoutManager);
        modeList.setItemAnimator(new DefaultItemAnimator());
        modeList.addItemDecoration(new DividerItemDecoration(PayMentDetail.this,DividerItemDecoration.VERTICAL));
        modeList.setNestedScrollingEnabled(false);
        modeList.setAdapter(itemAdapter);
    }
    @Override
    public void onPtrReceive(Printer printer, final int code, final PrinterStatusInfo status, String s) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, makeErrorMessage(status), mContext);
                dispPrinterWarnings(status);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
                SQLiteDatabase db = datalayer.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM CartDetail", null);
                if (cursor.moveToFirst()) {
                    do {
                        String Code = cursor.getString(cursor.getColumnIndex("code"));
                        datalayer.UpdateMenuQuantityDetail(Code,"0","0","0");
                    }while (cursor.moveToNext());
                    cursor.close();
                    datalayer.close();
                }
                hideProgressDialouge();
                datalayer.DeleteCart();
                datalayer.DeletePaymentDetail();
                GetCartItem();
                Intent i=new Intent(PayMentDetail.this,MenuDetail.class);
                startActivity(i);
                finish();

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add:
                Amount=enterAmount.getText().toString();
                if(!Amount.isEmpty())
                {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(enterAmount.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String Balance=balance.getText().toString();
                    if(modeName.equals("Cash"))
                    {

                        if(Double.parseDouble(Amount)<Double.parseDouble(Balance))
                        {
                            balance.setText(String.valueOf(Double.parseDouble(Balance)-Double.parseDouble(Amount)));
                            tenderamount.setText(String.valueOf(Double.parseDouble(Amount)+Double.parseDouble(tenderamount.getText().toString())));
                            datalayer.AddpaymentDetail(modeName,modeName,Amount,Amount,"0");
                            getPaymentData();
                            enterAmount.setText("");
                        }
                        else if(Double.parseDouble(Amount)==Double.parseDouble(Balance))
                        {
                            balance.setText("0.0");
                            datalayer.AddpaymentDetail(modeName,modeName,Amount,Amount,"0");
                            tenderamount.setText(String.valueOf(Double.parseDouble(Amount)+Double.parseDouble(tenderamount.getText().toString())));
                            getPaymentData();
                            enterAmount.setText("");
                        }
                        else if(Double.parseDouble(Amount)>Double.parseDouble(Balance))
                        {
                            balance.setText(String.valueOf(Double.parseDouble(Balance)-Double.parseDouble(Amount)));
                            tenderamount.setText(String.valueOf(Double.parseDouble(Amount)+Double.parseDouble(tenderamount.getText().toString())));
                            datalayer.AddpaymentDetail(modeName,modeName,Balance,Amount,balance.getText().toString());
                            getPaymentData();
                            enterAmount.setText("");
                        }
                    }
                    else if(modeName.equals("Credit Card"))
                    {
                            balance.setText("0.0");
                            Double tamount=Double.parseDouble(Amount);
                            Double Tamount=Double.parseDouble(tenderamount.getText().toString());
                            tenderamount.setText(String.valueOf(tamount+Tamount));
                            datalayer.AddpaymentDetail(modeName,modeType,Amount,String.valueOf(tamount+Tamount),"0");
                            getPaymentData();
                            enterAmount.setText("");

                    }

                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PayMentDetail.this);
                        builder.setTitle("Confirmation Message!");
                        builder.setMessage("Please Enter second  Balance Amount");
                        builder.setPositiveButton("OK", null);
                        AlertDialog dialog123 = builder.show();
                        TextView messageText =(TextView)dialog123.findViewById(android.R.id.message);
                        messageText.setGravity(Gravity.CENTER);
                    }
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PayMentDetail.this);
                    builder.setTitle("Confirmation Message!");
                    builder.setMessage("Please Enter Balance Amount");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog123 = builder.show();
                    TextView messageText =(TextView)dialog123.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                }
                SQLiteDatabase sqq=datalayer.getReadableDatabase();
                Cursor cursor111= sqq.rawQuery("SELECT  * FROM  PaymentDetail ",null);
                if(cursor111 != null && cursor111.getCount()>0)
                {
                    double value;
                    paymentAmount =0.0;
                    for (int k=0;k<payementList.size();k++)
                    {
                        value = Double.parseDouble(payementList.get(k).getAmount());
                        paymentAmount+= Math.round(value*100)/100.0;
                    }
                    if(paymentAmount>=Double.parseDouble(grossamount))
                    {
                        done.setVisibility(View.VISIBLE);
                    }
               }
            break;
            case R.id.Exit:
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(enterAmount.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                datalayer.DeletePaymentDetail();
                finish();
                break;
            case R.id.done:
                getCompanydetail();
                for (int i1 = 0; i1 < cartItemListDetail.size(); i1++) {
                    String itemDetail1 = cartItemListDetail.get(i1).getCode() + "~|~" + cartItemListDetail.get(i1).getQuantity() + "~|~" + cartItemListDetail.get(i1).getComment() + "~|~" + cartItemListDetail.get(i1).getPrice() + "~|~" + cartItemListDetail.get(i1).getTaxRate() + "~|~" + cartItemListDetail.get(i1).getTaxAmount();
                    itemDetail = itemDetail + (itemDetail1 + "||");
                }
                   showProgressDialouge();
                   getBillDetail(itemDetail);
                break;
        }
    }
    public void getBillDetail(final String itemdetail){
         StringRequest myReq = new StringRequest(Request.Method.POST,"http://192.168.1.60:4000/tab/saveBill",
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("custName","");
                    jsonObject.put("custPhone","");
                    jsonObject.put("roundOff",0);
                    jsonObject.put("grandTotal",grossamount);
                    jsonObject.put("tableNo",0);
                    jsonObject.put("waiterCode",waiterCode);
                    jsonObject.put("deviceId","abc");
                    jsonObject.put("pax",0);
                    jsonObject.put("billType","T");
                    jsonObject.put("itemDetail",itemdetail);
                } catch (JSONException js) {
                }
                return jsonObject.toString().getBytes();
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        myReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 10000;
            }
            @Override
            public void retry(VolleyError error) {
            }
        });
        requestQueue.add(myReq);
    }
    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(PayMentDetail.this, "success", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONObject jsonObject1=jsonObject.getJSONObject("output");
                    billNumber=jsonObject1.getString("ReturnBill_No");
                    returnBillTime =jsonObject1.getString("ReturnBillTime");
                    returnBillDate =jsonObject1.getString("ReturnBillDate");
                    if(print.equals("print"))
                       runPrintReceiptSequence();
                    else
                       Display();
                  } catch (JSONException e) {
                    hideProgressDialouge();
                    Toast.makeText(PayMentDetail.this,"error: "+e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        };
    }
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialouge();
                Toast.makeText(PayMentDetail.this,"error: "+error.toString(),Toast.LENGTH_LONG).show();
            }
        };
    }
    public class Main_ItemAdapter extends RecyclerView.Adapter<Main_ItemAdapter.MyViewHolder> {
        Context context;
        ArrayList<String> MyList;
        Main_ItemAdapter(Context context, ArrayList<String> MyList) {
            this.context = context;
            this.MyList = MyList;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_main_adapter, parent, false);
            return new MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            holder.title.setText(MyList.get(position));
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modeName=holder.title.getText().toString();
                    SQLiteDatabase db = datalayer.getReadableDatabase();
                    listDataChild=new ArrayList<>();
                    Cursor cursor = db.rawQuery("SELECT payMode FROM SettlementDetail where mode='"+holder.title.getText().toString()+"'", null);
                    if (cursor.moveToFirst()) {
                        do {
                            String payMode = cursor.getString(cursor.getColumnIndex("payMode"));
                            listDataChild.add(payMode);
                        }while (cursor.moveToNext());
                        cursor.close();
                        datalayer.close();
                    }
                    modeTypeList.setLayoutManager(new GridLayoutManager(PayMentDetail.this, 2));
                    ItemAdapter adapter = new ItemAdapter(PayMentDetail.this, listDataChild);
                    modeTypeList.setAdapter(adapter);
                    notifyDataSetChanged();
                    rowIndexOfList =position;
                }
            });
            if(rowIndexOfList ==position)
               holder.title.setBackgroundColor(Color.parseColor("#567845"));
            else
               holder.title.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        @Override
        public int getItemCount() {
            return MyList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            MyViewHolder(View vi) {
                super(vi);
                title =vi.findViewById(R.id.mtitle);
            }
        }
    }
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
        Context context;
        ArrayList<String> menuDetailList;
        ItemAdapter(Context context, ArrayList<String> MyList) {
            this.context = context;
            this.menuDetailList = MyList;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_list_item, parent, false);
            return new MyViewHolder(itemView);
        }
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder1, final int position) {
            holder1.title.setText(menuDetailList.get(position));
            holder1.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ischeck1 = true;
                    modeType=holder1.title.getText().toString();
                    String Balance=balance.getText().toString();
                    enterAmount.setText(Balance);
                    rowIndexOfList1 =position;
                    notifyDataSetChanged();
                }
            });
            if(ischeck1)
            {
                if(rowIndexOfList1 ==position){
                    holder1.select.setBackgroundColor(Color.parseColor("#567845"));
                    holder1.title.setTextColor(Color.parseColor("#ffffff"));
                }
                else
                {
                    holder1.select.setBackgroundColor(Color.parseColor("#ffffff"));
                    holder1.title.setTextColor(Color.parseColor("#000000"));
                }
            }
        }
        @Override
        public int getItemCount(){
            return menuDetailList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            CardView select;
            MyViewHolder(View vi) {
                super(vi);
                title =vi.findViewById(R.id.mode);
                select =vi.findViewById(R.id.card);
            }
        }
    }
    public void getPaymentData(){
        SQLiteDatabase db = datalayer.getReadableDatabase();
        payementList.clear();
        int i=1;
        Cursor cursor = db.rawQuery("SELECT * FROM PaymentDetail", null);
        if (cursor.moveToFirst()) {
            do {
                String srNo = cursor.getString(cursor.getColumnIndex("id"));
                String modename = cursor.getString(cursor.getColumnIndex("modeName"));
                String modetype = cursor.getString(cursor.getColumnIndex("modetype"));
                String amount = cursor.getString(cursor.getColumnIndex("amount"));
                payementList.add(new PaymentDetailListType(String.valueOf(i),modename,modetype,amount));
                i++;
            }while (cursor.moveToNext());
            cursor.close();
            datalayer.close();
        }
        PaymentListadapter itemAdapter = new PaymentListadapter(PayMentDetail.this,payementList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PayMentDetail.this);
        paymentListView.setLayoutManager(mLayoutManager);
        paymentListView.setItemAnimator(new DefaultItemAnimator());
        paymentListView.addItemDecoration(new DividerItemDecoration(PayMentDetail.this,DividerItemDecoration.VERTICAL));
        paymentListView.setNestedScrollingEnabled(false);
        paymentListView.setAdapter(itemAdapter);
    }
    public class PaymentListadapter extends RecyclerView.Adapter<PaymentListadapter.MyViewHolder> {
        Context context;
        ArrayList<PaymentDetailListType> MyList;
        PaymentListadapter(Context context, ArrayList<PaymentDetailListType> MyList) {
            this.context = context;
            this.MyList = MyList;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_detail_list_type, parent, false);
            return new MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            holder.srno.setText(MyList.get(position).getSrNo());
            holder.modename.setText(MyList.get(position).getModeName());
            holder.modetype.setText(MyList.get(position).getModeType());
            holder.amount.setText(MyList.get(position).getAmount());
        }
        @Override
        public int getItemCount() {
            return MyList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView srno,modename,modetype,amount;
            MyViewHolder(View vi) {
                super(vi);
                srno =vi.findViewById(R.id.sno);
                modename =vi.findViewById(R.id.mname);
                modetype =vi.findViewById(R.id.mtype);
                amount =vi.findViewById(R.id.mamount);
            }
        }
    }
    public void init(){
        datalayer=new Datalayer(getApplicationContext());
        modeTypeList = findViewById(R.id.listtype);
        modeList=findViewById(R.id.list);
        tenderamount=findViewById(R.id.tenderamount);
        enterAmount=findViewById(R.id.enterAmount);
        done=findViewById(R.id.done);
        Exit=findViewById(R.id.Exit);
        addPayment=findViewById(R.id.add);
        billamount=findViewById(R.id.billamount);
        balance=findViewById(R.id.balance);
        addPayment.setOnClickListener(this);
        done.setOnClickListener(this);
        Exit.setOnClickListener(this);
    }
    private boolean runPrintReceiptSequence() {
        if (!initializeObject()) {
            return false;
        }
        if (!createReceiptData()) {
            finalizeObject();
            return false;
        }
        if (!printData()) {
            finalizeObject();
            return false;
        }
        return true;
    }
    private boolean createReceiptData() {
        String method = "";
        textData=new StringBuilder();
        if (mPrinter == null) {
            return false;
        }
        try {
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addFeedLine(1);
            method = "addText";

            textData.append(companyName).append("\n");
            textData.append(companyAddress1+"\n"+companyAddress2+"\n"+companyAddress3+"\n");
            textData.append(companyGstNo+"\n");
            if(!companyHeader1.isEmpty())
                textData.append(companyHeader1+"\n");
            if(!companyHeader2.isEmpty())
                textData.append(companyHeader2).append("\n");
            textData.append("----------------------------------------\n");
            textData.append(rightpad("Date : ", 7)+rightpad(returnBillDate, 11)).append(leftpad("TIME :", 11)).append(rightpad(returnBillTime, 11)).append("\n");
            textData.append(rightpad("USER : ", 7)+rightpad(waiterCode,11)+leftpad("BILLNO :",11)+rightpad(billNumber,11)+"\n");
            textData.append("----------------------------------------\n");
            textData.append("Dish Name       "+" QTY"+"     Rate"+"     Amount"+"\n");
            textData.append("----------------------------------------\n");
            for(int i=0;i<cartItemListDetail.size();i++)
            {
                String Name = rightpad(cartItemListDetail.get(i).getItemName(),20);
                String Rate = leftpad(cartItemListDetail.get(i).getPrice(),6);
                String Quantity=leftpad(cartItemListDetail.get(i).getQuantity(),4);
                String Tprice=leftpad(String.format("%.2f",Float.parseFloat(cartItemListDetail.get(i).getTotalPrice())),10);
                textData.append(Name).append(Quantity).append(Rate).append(Tprice).append("\n");
            }
            textData.append("----------------------------------------\n");
            textData.append("Items  "+leftpad(Item,3)+leftpad("SUBTOTAL :  ",23)+leftpad(subtotal,7)+"\n");
            if(!tax.equals("0.0")) {
                textData.append(leftpad("* SGST @ 2.50% :  ", 33) + leftpad(String.format("%.2f",Double.parseDouble(tax)/2), 7) + "\n");
                textData.append(leftpad("* CGST @ 2.50% :  ", 33) + leftpad(String.format("%.2f",Double.parseDouble(tax)/2), 7) + "\n");
            }
            textData.append(leftpad("R. off :  ",33)+leftpad(roundoff,7)+"\n");
            textData.append(leftpad("TOTAL :  ",33)+leftpad(grossamount,7)+"\n");
            textData.append("----------------------------------------\n");
            String bal=balance.getText().toString();
            bal=bal.replace("-","");
            textData.append(leftpad("Tendered Amt. :  ",33)+leftpad(tenderamount.getText().toString(),7)+"\n");
            textData.append(leftpad("Balance Amt. :  ",33)+leftpad(bal,7)+"\n");
            Double db=Double.parseDouble(grossamount);
            int value = db.intValue();
            textData.append(rightpad(" Rupees "+convert(value)+" Only", 40)).append("\n");
            textData.append("\n");
            if(!comapnyFooter1.isEmpty())
                textData.append(comapnyFooter1+"\n");
            if(!comapnyFooter2.isEmpty())
                textData.append(comapnyFooter2+"\n");
            if(!comapnyFooter3.isEmpty())
                textData.append(comapnyFooter3+"\n");
            if(!comapnyFooter4.isEmpty())
                textData.append(comapnyFooter4+"\n");
            if(!comapnyFooter5.isEmpty())
                textData.append(comapnyFooter5+"\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addFeedLine";
            mPrinter.addFeedLine(2);
            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            System.out.println("exception: "+e);
            ShowMsg.showException(e, method, mContext);

            return false;
        }
        return true;
    }
    public void Display(){
        hideProgressDialouge();
        textData=new StringBuilder();
        textData.append(companyName).append("\n");
        textData.append(companyAddress1+"\n"+companyAddress2+"\n"+companyAddress3+"\n");
        textData.append(companyGstNo+"\n");
        if(!companyHeader1.isEmpty())
            textData.append(companyHeader1+"\n");
        if(!companyHeader2.isEmpty())
            textData.append(companyHeader2).append("\n");
        textData.append("----------------------------------------\n");
        textData.append(rightpad("Date : ", 7)+rightpad(returnBillDate, 11)).append(leftpad("TIME :", 11)).append(rightpad(returnBillTime, 11)).append("\n");
        textData.append(rightpad("USER : ", 7)+rightpad(waiterCode,11)+leftpad("BILLNO :",11)+rightpad(billNumber,11)+"\n");
        textData.append("----------------------------------------\n");
        textData.append("Dish Name       "+" QTY"+"     Rate"+"     Amount"+"\n");
        textData.append("----------------------------------------\n");
        for(int i=0;i<cartItemListDetail.size();i++)
        {
            String Name = rightpad(cartItemListDetail.get(i).getItemName(),20);
            String Rate = leftpad(cartItemListDetail.get(i).getPrice(),6);
            String Quantity=leftpad(cartItemListDetail.get(i).getQuantity(),4);
            String Tprice=leftpad(String.format("%.2f",Float.parseFloat(cartItemListDetail.get(i).getTotalPrice())),10);
            textData.append(Name).append(Quantity).append(Rate).append(Tprice).append("\n");
        }
        textData.append("----------------------------------------\n");
        textData.append("Items  "+leftpad(Item,3)+leftpad("SUBTOTAL :  ",23)+leftpad(subtotal,7)+"\n");
        if(!tax.equals("0.0")) {
            textData.append(leftpad("* SGST @ 2.50% :  ", 33) + leftpad(String.format("%.2f",Double.parseDouble(tax)/2), 7) + "\n");
            textData.append(leftpad("* CGST @ 2.50% :  ", 33) + leftpad(String.format("%.2f",Double.parseDouble(tax)/2), 7) + "\n");
        }
            textData.append(leftpad("R. off :  ",33)+leftpad(roundoff,7)+"\n");
            textData.append(leftpad("TOTAL :  ",33)+leftpad(grossamount,7)+"\n");
        textData.append("----------------------------------------\n");
        textData.append(leftpad("Tendered Amt. :  ",33)+leftpad(tenderamount.getText().toString(),7)+"\n");
        textData.append(leftpad("Balance Amt. :  ",33)+leftpad(balance.getText().toString(),7)+"\n");
        Double db=Double.parseDouble(grossamount);
        int value = db.intValue();
        textData.append(leftpad(convert(value), 40)).append("\n");
        textData.append("\n");
        if(!comapnyFooter1.isEmpty())
            textData.append(comapnyFooter1+"\n");
        if(!comapnyFooter2.isEmpty())
            textData.append(comapnyFooter2+"\n");
        if(!comapnyFooter3.isEmpty())
            textData.append(comapnyFooter3+"\n");
        if(!comapnyFooter4.isEmpty())
            textData.append(comapnyFooter4+"\n");
        if(!comapnyFooter5.isEmpty())
            textData.append(comapnyFooter5+"\n");

        ShowDialouge(textData.toString());



    }
    public void ShowDialouge(String data){
        dialog = new Dialog(PayMentDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);
        Button dialogButton =dialog.findViewById(R.id.btn_yes);
        TextView data1 =dialog.findViewById(R.id.txt_dia);
        data1.setText(data);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = datalayer.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM CartDetail", null);
                if (cursor.moveToFirst()) {
                    do {
                        String Code = cursor.getString(cursor.getColumnIndex("code"));
                        datalayer.UpdateMenuQuantityDetail(Code,"0","0","0");

                    }while (cursor.moveToNext());
                    cursor.close();
                    datalayer.close();
                }
                hideProgressDialouge();
                datalayer.DeleteCart();
                datalayer.DeletePaymentDetail();
                GetCartItem();
                Intent i=new Intent(PayMentDetail.this,MenuDetail.class);
                startActivity(i);
                finish();
                dialog.cancel();
            }
        });
        dialog.show();
    }
}
