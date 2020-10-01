package com.ssl.sspls.billingapp.Activity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.squareup.picasso.Picasso;
import com.ssl.sspls.billingapp.Localstorage.Datalayer;
import com.ssl.sspls.billingapp.Model.CartItem;
import com.ssl.sspls.billingapp.Model.MenuDetailList;
import com.ssl.sspls.billingapp.Model.UnholdBillDetail;
import com.ssl.sspls.billingapp.R;
import com.ssl.sspls.billingapp.ShowMsg;
import com.ssl.sspls.billingapp.Url.Constent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
public class MenuDetail extends BaseActivity{
    ArrayList<String> headingList;
    Datalayer  datalayer;
    ArrayList<MenuDetailList> itemDetailList;
    RecyclerView headname;
    RecyclerView menuListView, cartListView;
    TextView itemCount,netAmount,tax,roundoff,grossAmount;
    ArrayList<CartItem> cartItemListDetail;
    int rowIndexOfList;
    Double roundOffValue;
    LinearLayout unHoldBill,billOnHold,dishComment,logoff,done,setup,tender;
    String menuItemCode,time,waiterCode,custName,custPhone,print;
    Double taxAmount=0.0,totalPrice1=0.0;
    RelativeLayout parentLayout;

    Dialog dialougeUnholdBill;
    int screenWidth,screenHeight,numberOfColumns;
    EditText search;
    Context context;

    Boolean isCheck=false;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_detail);
        this.context = this;
        settings =getSharedPreferences("MyPref", MODE_PRIVATE);
        waiterCode=settings.getString("waiterCode","");
        print=settings.getString("print","");
        modelNo=settings.getInt("modelNo",0);
        language=settings.getInt("language",0);//modeType
        headno=settings.getInt("headno",0);
        targetIp=settings.getString("targetIp","");
        cartItemListDetail=new ArrayList<>();

        requestQueue=Volley.newRequestQueue(MenuDetail.this);
        Objects.requireNonNull(getSupportActionBar()).hide();
        init();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight= size.y;
        mContext=this;
        getHeading();
        GetCartItem();
        /////////////////////////getting Current Time////////////////////
        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        time= dateFormat.format(date);
        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s)
            {

            }
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                String value=search.getText().toString();
                if(value.length()==0)
                   GetCartItem();
                else
                   getSearchList("%"+value+"%");
            }
        });
    }
    public void getSearchList(String value){
        itemDetailList.clear();
        SQLiteDatabase db = datalayer.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MenuDetail where name like '"+value+"'", null);
        if (cursor.moveToFirst()) {
            do {
                String Code = cursor.getString(cursor.getColumnIndex(Datalayer.CODE));
                String Name = cursor.getString(cursor.getColumnIndex(Datalayer.NAME));
                String Desc = cursor.getString(cursor.getColumnIndex(Datalayer.DESCRIPTION));
                String Rate = cursor.getString(cursor.getColumnIndex(Datalayer.RATE));
                String Dept = cursor.getString(cursor.getColumnIndex(Datalayer.DEPT));
                String Heading = cursor.getString(cursor.getColumnIndex(Datalayer.HEADING));
                String Status=cursor.getString(cursor.getColumnIndex(Datalayer.STATUS));
                String ImgPath = cursor.getString(cursor.getColumnIndex(Datalayer.IMAGE));
                String Quantity = cursor.getString(cursor.getColumnIndex(Datalayer.QUANTITY));
                String TotalPrice = cursor.getString(cursor.getColumnIndex(Datalayer.TOTALPRICE));
                String Veg=cursor.getString(cursor.getColumnIndex(Datalayer.VEG));
                String Tax=cursor.getString(cursor.getColumnIndex(Datalayer.TAXRATE));
                String FinalAmount=cursor.getString(cursor.getColumnIndex(Datalayer.TAXAMOUNT));

                itemDetailList.add(new MenuDetailList(Code,Name,Desc,Rate,Dept,Heading,Status,ImgPath,Quantity,TotalPrice,Veg,Tax,FinalAmount));
            }while (cursor.moveToNext());
            cursor.close();
            datalayer.close();
        }
        if(screenWidth>=1920&& screenHeight==1024)
            numberOfColumns = 6;
        else
            numberOfColumns = 5;
        menuListView.setLayoutManager(new GridLayoutManager(MenuDetail.this, numberOfColumns));
        ItemAdapter adapter = new ItemAdapter(MenuDetail.this, itemDetailList);
        menuListView.setAdapter(adapter);
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
            GetCartAmount();
            cartItemDetailAdapter  mAdapter = new cartItemDetailAdapter(MenuDetail.this, cartItemListDetail);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(MenuDetail.this);
            cartListView.setLayoutManager(mLayoutManager);
            cartListView.setAdapter(mAdapter);
    }
    public void getHeading(){
        itemDetailList.clear();
        SQLiteDatabase db = datalayer.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT heading FROM MenuDetail order by heading", null);
        if (cursor.moveToFirst()) {
            do {
                headingList.add(cursor.getString(cursor.getColumnIndex("heading")));
               }while (cursor.moveToNext());
            cursor.close();
            datalayer.close();
        }
        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        headname.setLayoutManager(layoutManager);
        HeadItemAdapter adapter = new HeadItemAdapter(MenuDetail.this,headingList);
        headname.setAdapter(adapter);
        String head=headingList.get(0);
        fetchDataFromSQLite(head);
    }
    public void fetchDataFromSQLite(String head){
        itemDetailList.clear();
        SQLiteDatabase db = datalayer.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MenuDetail where Heading='"+head+"'", null);
        if (cursor.moveToFirst()) {
            do {
                String Code = cursor.getString(cursor.getColumnIndex(Datalayer.CODE));
                String Name = cursor.getString(cursor.getColumnIndex(Datalayer.NAME));
                String Desc = cursor.getString(cursor.getColumnIndex(Datalayer.DESCRIPTION));
                String Rate = cursor.getString(cursor.getColumnIndex(Datalayer.RATE));
                String Dept = cursor.getString(cursor.getColumnIndex(Datalayer.DEPT));
                String Heading = cursor.getString(cursor.getColumnIndex(Datalayer.HEADING));
                String Status=cursor.getString(cursor.getColumnIndex(Datalayer.STATUS));
                String ImgPath = cursor.getString(cursor.getColumnIndex(Datalayer.IMAGE));
                String Quantity = cursor.getString(cursor.getColumnIndex(Datalayer.QUANTITY));
                String TotalPrice = cursor.getString(cursor.getColumnIndex(Datalayer.TOTALPRICE));
                String Veg=cursor.getString(cursor.getColumnIndex(Datalayer.VEG));
                String Tax=cursor.getString(cursor.getColumnIndex(Datalayer.TAXRATE));
                String FinalAmount=cursor.getString(cursor.getColumnIndex(Datalayer.TAXAMOUNT));

                itemDetailList.add(new MenuDetailList(Code,Name,Desc,Rate,Dept,Heading,Status,ImgPath,Quantity,TotalPrice,Veg,Tax,FinalAmount));
            }while (cursor.moveToNext());
            cursor.close();
            datalayer.close();
        }
        if(screenWidth>=1920&& screenHeight==1024)
            numberOfColumns = 6;
        else
            numberOfColumns = 5;
        menuListView.setLayoutManager(new GridLayoutManager(MenuDetail.this, numberOfColumns));
        ItemAdapter adapter = new ItemAdapter(MenuDetail.this, itemDetailList);
        menuListView.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.billonhold:
                cartItemListDetail.clear();
                SQLiteDatabase db = datalayer.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM CartDetail", null);
                if (cursor.moveToFirst()) {
                    do {
                        String billNo = "b00" + count;
                        menuItemCode = cursor.getString(cursor.getColumnIndex("code"));
                        String itemName = cursor.getString(cursor.getColumnIndex("name"));
                        String price = cursor.getString(cursor.getColumnIndex("rate"));
                        String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                        String totalPrice = cursor.getString(cursor.getColumnIndex("totalPrice"));
                        String comment = cursor.getString(cursor.getColumnIndex("comment"));
                        String taxRate = cursor.getString(cursor.getColumnIndex("taxRate"));
                        String taxAmount = cursor.getString(cursor.getColumnIndex("finalAmount"));
                        datalayer.AddCartOnHoldDetail(billNo, menuItemCode, itemName, quantity, price, totalPrice, comment, taxRate,time,taxAmount);
                    } while (cursor.moveToNext());
                    cursor.close();
                    datalayer.close();
                }
                count++;
                datalayer.DeleteCart();
                GetCartItem();
                break;
            case R.id.unholdbill:
                SQLiteDatabase db23 = datalayer.getWritableDatabase();
                String count = "SELECT count(*) FROM CartDetail";
                @SuppressLint("Recycle")
                Cursor mcursor = db23.rawQuery(count, null);
                mcursor.moveToFirst();
                int icount = mcursor.getInt(0);
                if (icount > 0){Toast.makeText(MenuDetail.this,"Please Complite the existing bill?",Toast.LENGTH_LONG).show();}
                else
                {
                dialougeUnholdBill = new Dialog(MenuDetail.this);
                dialougeUnholdBill.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialougeUnholdBill.setCancelable(false);
                dialougeUnholdBill.setContentView(R.layout.holdbill_popup);
                ImageView cancel1 = dialougeUnholdBill.findViewById(R.id.back);
                RecyclerView holdBillList = dialougeUnholdBill.findViewById(R.id.billhold);
                ArrayList<UnholdBillDetail> unholdBillList = new ArrayList<>();
                cancel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialougeUnholdBill.dismiss();

                    }
                });
                SQLiteDatabase db1 = datalayer.getReadableDatabase();
                Cursor cursor1 = db1.rawQuery("SELECT DISTINCT billNo FROM CartOnHoldDetail", null);
                if (cursor1.moveToFirst()) {
                    do {
                        String billNo = cursor1.getString(cursor1.getColumnIndex("billNo"));
                        System.out.println("your date time: " + billNo);
                        int quan = 0;
                        double totalPrice = 0.0;
                        SQLiteDatabase db2 = datalayer.getReadableDatabase();
                        String dateTimeVal = "";
                        Cursor cursor11 = db2.rawQuery("SELECT * FROM CartOnHoldDetail where billNo='" + billNo + "'", null);
                        if (cursor11.moveToFirst()) {
                            do {
                                String quantity = cursor11.getString(cursor11.getColumnIndex("quantity"));
                                String tPrice = cursor11.getString(cursor11.getColumnIndex("totalPrice"));
                                dateTimeVal = cursor11.getString(cursor11.getColumnIndex("dateTimeVal"));
                                quan = quan + Integer.parseInt(quantity);
                                totalPrice = totalPrice + Double.parseDouble(tPrice);
                            } while (cursor11.moveToNext());
                            cursor11.close();
                            datalayer.close();
                        }
                        unholdBillList.add(new UnholdBillDetail(billNo, dateTimeVal, String.valueOf(quan), String.valueOf(totalPrice)));
                    } while (cursor1.moveToNext());
                    cursor1.close();
                    datalayer.close();
                }
                int numberOfColumns = 3;
                holdBillList.setLayoutManager(new GridLayoutManager(MenuDetail.this, numberOfColumns));
                CartOnHoldBillAdapter adapter = new CartOnHoldBillAdapter(MenuDetail.this, unholdBillList);
                holdBillList.setAdapter(adapter);
                dialougeUnholdBill.show();
                }
                break;
            case R.id.dishcomment:
                SQLiteDatabase sq=datalayer.getReadableDatabase();
                @SuppressLint("Recycle") Cursor cursor1= sq.rawQuery("SELECT  * FROM  CartDetail ",null);
                if(cursor1 != null && cursor1.getCount()>0)
                {
                    final Dialog dialog = new Dialog(MenuDetail.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.custome_comment);
                    TextView cancel =dialog.findViewById(R.id.cancel);
                    final EditText comment =dialog.findViewById(R.id.comment);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    TextView ok =dialog.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String comment1=comment.getText().toString();
                            if(comment1.equals("")||comment1.equals("Abc"))
                            {
                                Toast.makeText(MenuDetail.this,"Please Write Comment here",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                datalayer.UpdateCartComment(menuItemCode,comment.getText().toString());
                                GetCartItem();
                                MenuDetail.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                dialog.dismiss();
                            }
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MenuDetail.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuDetail.this);
                    builder.setTitle("Confirmation Message!");
                    builder.setMessage("Please select a Dish");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog123 = builder.show();
                    TextView messageText =(TextView)dialog123.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                }
            break;
            case R.id.logoff:
                datalayer.DeleteCart();
                Intent i=new Intent(MenuDetail.this,LoginScreen.class);
                startActivity(i);
                finish();
                break;
            case R.id.done:
                SQLiteDatabase sq1=datalayer.getReadableDatabase();
                Cursor cursor11= sq1.rawQuery("SELECT  * FROM  CartDetail ",null);
                if(cursor11 != null && cursor11.getCount()>0)
                {
                    getCompanydetail();
                    for (int i1 = 0; i1 < cartItemListDetail.size(); i1++) {
                        String itemDetail1 = cartItemListDetail.get(i1).getCode() + "~|~" + cartItemListDetail.get(i1).getQuantity() + "~|~" + cartItemListDetail.get(i1).getComment() + "~|~" + cartItemListDetail.get(i1).getPrice() + "~|~" + cartItemListDetail.get(i1).getTaxRate() + "~|~" + cartItemListDetail.get(i1).getTaxAmount();
                        itemDetail = itemDetail + (itemDetail1 + "||");
                    }
                    Log.i("item Detail before: ",itemDetail);
                    itemDetail = itemDetail.substring(4);
                    Log.i("item Detail: ",itemDetail);
                    showProgressDialouge();
                    getBillDetail(itemDetail);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuDetail.this);
                    builder.setTitle("Confirmation Message!");
                    builder.setMessage("Please select a Dish");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog123 = builder.show();
                    TextView messageText =(TextView)dialog123.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                }
                break;
            case R.id.setup:
                Intent i1=new Intent(MenuDetail.this,PrintSetup.class);
                startActivity(i1);
                break;

            case R.id.tender:
                SQLiteDatabase sqq=datalayer.getReadableDatabase();
                Cursor cursor111= sqq.rawQuery("SELECT  * FROM  CartDetail ",null);
                if(cursor111 != null && cursor111.getCount()>0)
                {
                    Intent intent=new Intent(MenuDetail.this,PayMentDetail.class);
                    intent.putExtra("subtotal",netAmount.getText().toString());
                    intent.putExtra("tax",tax.getText().toString());
                    intent.putExtra("roundoff",roundoff.getText().toString());
                    intent.putExtra("grossamount",grossAmount.getText().toString());
                    intent.putExtra("totalItem",String.valueOf(totalItem));
                    startActivity(intent);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MenuDetail.this);
                    builder.setTitle("Confirmation Message!");
                    builder.setMessage("Please select a Dish");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog123 = builder.show();
                    TextView messageText =(TextView)dialog123.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                }
                break;
        }
    }
    public class HeadItemAdapter extends RecyclerView.Adapter<HeadItemAdapter.MyViewHolder> {
        Context context;
        ArrayList<String> tabNameList;
        HeadItemAdapter(Context context, ArrayList<String> MyList)
        {
            this.context = context;
            this.tabNameList = MyList;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.headitem, parent, false);
            return new MyViewHolder(itemView);
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public int getItemViewType(int position)
        {
            return position;
        }
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder1, @SuppressLint("RecyclerView") final int position) {
            holder1.title.setText(tabNameList.get(position));
            holder1.title.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    String name=holder1.title.getText().toString();
                    fetchDataFromSQLite(name);
                    rowIndexOfList =position;
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editorSP = sharedPreferences.edit();
                    editorSP.putInt("headno",rowIndexOfList);
                    editorSP.apply();
                    notifyDataSetChanged();
               }
            });
            if(rowIndexOfList ==position){
                holder1.title.setBackgroundColor(Color.parseColor("#567845"));
                holder1.title.setTextColor(Color.parseColor("#ffffff"));
            }
            else
            {
                holder1.title.setBackgroundColor(Color.parseColor("#ffffff"));
                holder1.title.setTextColor(Color.parseColor("#000000"));
            }
        }
        @Override
        public int getItemCount(){
            return tabNameList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            MyViewHolder(View vi) {
                super(vi);
                title = vi.findViewById(R.id.item);
            }
        }
    }
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
        Context context;
        ArrayList<MenuDetailList> menuDetailList;
        ItemAdapter(Context context, ArrayList<MenuDetailList> MyList) {
            this.context = context;
            this.menuDetailList = MyList;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
            return new ItemAdapter.MyViewHolder(itemView);
        }
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder1, final int position) {
            holder1.title.setText(menuDetailList.get(position).getName());
            String food = "@drawable/food";
            String salad = "@drawable/salad";
            if(menuDetailList.get(position).getHead().contains("SALAD"))
            {
                int imageResource = getResources().getIdentifier(salad, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                holder1.image.setImageDrawable(res);
            }
            else
            {
                int imageResource = getResources().getIdentifier(food, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                holder1.image.setImageDrawable(res);
            }
            final String code=menuDetailList.get(position).getCode();
            final String itemName=menuDetailList.get(position).getName();
            final String Price=menuDetailList.get(position).getRate();
            final String tax=menuDetailList.get(position).getTax();
            if(menuDetailList.get(position).getImgpath().equals("abcd"))
            {
                holder1.image.setVisibility(View.GONE);
            }
            else
            {

                Picasso.with(context).load(menuDetailList.get(position).getImgpath()).placeholder(R.drawable.food).into(holder1.image);

            }
            holder1.select.setClickable(true);
            holder1.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase sq=datalayer.getReadableDatabase();
                    Cursor cursor= sq.rawQuery("SELECT  * FROM  CartDetail WHERE code ='"+code+"'",null);
                    if(cursor != null && cursor.getCount()>0)
                    {
                        @SuppressLint("Recycle")
                        Cursor cursor1= sq.rawQuery("SELECT  quantity FROM  CartDetail WHERE code ='"+code+"'",null);
                        if (cursor1.moveToFirst()) {
                            do {
                                String quantity=cursor1.getString(cursor1.getColumnIndex("quantity"));
                                Double quan=Double.parseDouble(quantity)+1;
                                String totalPrice=String.valueOf(Double.parseDouble(Price)*quan);
                                System.out.println("your price: "+totalPrice);
                                String taxAmount1=String.valueOf(Double.parseDouble(totalPrice)*(Double.parseDouble(tax)/100));
                                datalayer.UpdateCartQuantityWithTaxAmmount(code,String.valueOf(Integer.parseInt(quantity)+1),totalPrice,taxAmount1);
                                GetCartItem();
                            }while (cursor.moveToNext());
                            cursor.close();
                            datalayer.close();
                        }
                    }
                    else
                    {
                        String taxAmount1=String.valueOf(Double.parseDouble(Price)*(Double.parseDouble(tax)/100));
                        datalayer.AddCartDetail(code,itemName,"1",Price,Price,"abc",tax,taxAmount1);
                        GetCartItem();
                    }
                    assert cursor != null;
                    cursor.close();
                }
            });
        }
        @Override
        public int getItemCount(){
            return menuDetailList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            LinearLayout select;
            ImageView image;
            MyViewHolder(View vi) {
                super(vi);
                title =vi.findViewById(R.id.item);
                select =vi.findViewById(R.id.select);
                image =vi.findViewById(R.id.img);
            }
        }
    }
    public class CartOnHoldBillAdapter extends RecyclerView.Adapter<CartOnHoldBillAdapter.MyViewHolder> {
        Context context;
        ArrayList<UnholdBillDetail> holditemList;
        CartOnHoldBillAdapter(Context context, ArrayList<UnholdBillDetail> MyList) {
            this.context = context;
            this.holditemList = MyList;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.holdbilllistitem, parent, false);
            return new MyViewHolder(itemView);
        }
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder1, @SuppressLint("RecyclerView") final int position) {
            holder1.time.setText(holditemList.get(position).getTime());
            holder1.item.setText("Total items: "+holditemList.get(position).getQuantity());
            holder1.amount.setText("Total Price: "+holditemList.get(position).getTotalPrice());
            holder1.unholdlistlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String billNo=holditemList.get(position).getBillNo();
                    SQLiteDatabase db = datalayer.getReadableDatabase();
                    Cursor cursor = db.rawQuery("SELECT * FROM CartOnHoldDetail where billNo='"+billNo+"'", null);
                    if (cursor.moveToFirst()) {
                        do {
                            String code = cursor.getString(cursor.getColumnIndex("code"));
                            String itemName = cursor.getString(cursor.getColumnIndex("name"));
                            String price = cursor.getString(cursor.getColumnIndex("rate"));
                            String quantity=cursor.getString(cursor.getColumnIndex("quantity"));
                            String totalPrice=cursor.getString(cursor.getColumnIndex("totalPrice"));
                            String comment=cursor.getString(cursor.getColumnIndex("comment"));
                            String tax=cursor.getString(cursor.getColumnIndex("taxRate"));
                            String taxAmount=cursor.getString(cursor.getColumnIndex("finalAmount"));
                            datalayer.AddCartDetail(code,itemName,quantity,price,totalPrice,comment,tax,taxAmount);
                        }while (cursor.moveToNext());
                        cursor.close();
                        datalayer.close();
                        GetCartItem();
                        datalayer.DeleteOnHoldDetail(billNo);
                        dialougeUnholdBill.dismiss();
                    }
                }
            });
        }
        @Override
        public int getItemCount(){
            return holditemList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView time,item,amount;
            LinearLayout unholdlistlayout;
            MyViewHolder(View vi) {
                super(vi);
                time =vi.findViewById(R.id.time);
                item =vi.findViewById(R.id.item);
                amount =vi.findViewById(R.id.Amount);
                unholdlistlayout =vi.findViewById(R.id.unholdlistlayout);

            }
        }
    }
    public class cartItemDetailAdapter extends RecyclerView.Adapter<cartItemDetailAdapter.MyViewHolder> {
        Context context;
        ArrayList<CartItem> cartItemList;
        cartItemDetailAdapter(Context context, ArrayList<CartItem> MyList) {
            this.context = context;
            this.cartItemList = MyList;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_, parent, false);
            return new MyViewHolder(itemView);
        }
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder1, @SuppressLint("RecyclerView") final int position) {
            holder1.qty.setText(cartItemList.get(position).getQuantity());
            String comment=cartItemList.get(position).getComment();
            holder1.name.setText(cartItemList.get(position).getItemName());
            if("abc".equalsIgnoreCase(comment))
                holder1.comment.setVisibility(View.GONE);
            else{
                holder1.comment.setVisibility(View.VISIBLE);
                holder1.comment.setText(cartItemList.get(position).getComment());
                }
            holder1.rate.setText(cartItemList.get(position).getPrice());
            holder1.trate.setText(cartItemList.get(position).getTotalPrice());
            holder1.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(MenuDetail.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.custome_dialouge);
                    Button yes = dialog.findViewById(R.id.yes);
                    Button no = dialog.findViewById(R.id.no);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            datalayer.DeleteCartItem(cartItemList.get(position).getCode());
                            GetCartItem();
                            GetCartAmount();
                            dialog.dismiss();
                        }
                    });
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            holder1.qty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(MenuDetail.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.quantity_dialouge);
                    final EditText quantity =dialog.findViewById(R.id.comment);
                    ImageView back= dialog.findViewById(R.id.back);
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    TextView ok =dialog.findViewById(R.id.ok);
                    quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                        @Override
                        public void onFocusChange(View view, boolean focused) {
                            quantity.setRawInputType(Configuration.KEYBOARD_12KEY);
                        }
                    });
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String quanti=quantity.getText().toString();
                            if(quanti.equals("")||quanti.equals("Abc"))
                                  Toast.makeText(MenuDetail.this,"Please Update quantity here",Toast.LENGTH_SHORT).show();
                            else
                            {
                                String price=cartItemList.get(position).getPrice();
                                String totalPrice=String.valueOf(Double.parseDouble(quanti)*(Double.parseDouble(price)));
                                datalayer.UpdateCartQuantity(cartItemList.get(position).getCode(),quanti,totalPrice);
                                GetCartAmount();
                                GetCartItem();
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                }
            });
            holder1.itemSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                   isCheck=true;
                   rowIndexOfList =position;
                   menuItemCode =cartItemList.get(position).getCode();
                   notifyDataSetChanged();
                }
            });
            if(isCheck)
            {
                if(rowIndexOfList ==position)
                    holder1.itemSelect.setBackgroundColor(Color.parseColor("#567845"));
                else
                    holder1.itemSelect.setBackgroundColor(Color.parseColor("#ffffff"));
            }

        }
        @Override
        public int getItemCount(){
            return cartItemList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView qty,name,rate,trate,comment;
            ImageView delete;
            LinearLayout itemSelect;
            MyViewHolder(View vi) {
                super(vi);
                qty =vi.findViewById(R.id.qty);
                name =vi.findViewById(R.id.name);
                comment =vi.findViewById(R.id.comment);
                rate =vi.findViewById(R.id.rate);
                trate =vi.findViewById(R.id.amount);
                delete =vi.findViewById(R.id.delete);
                itemSelect=vi.findViewById(R.id.l2);
            }
        }
    }
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void GetCartAmount(){
        double value,value1,value2;
        totalPrice1 =0.0;
        taxAmount =0.0;
        totalItem =0;
        for (int k=0;k<cartItemListDetail.size();k++)
        {
            value = Double.parseDouble(cartItemListDetail.get(k).getTotalPrice());
            totalPrice1+= Math.round(value * 100) / 100.0;
            value1 =Double.parseDouble(cartItemListDetail.get(k).getTaxAmount());
            taxAmount+= Math.round(value1 * 100) / 100.0;
            value2 =Integer.parseInt(cartItemListDetail.get(k).getQuantity());
            totalItem+=value2;
        }
        itemCount.setText("Total items:  "+String.valueOf(totalItem));
        netAmount.setText(String.format("%.2f",totalPrice1));
        tax.setText(String.format("%.2f",taxAmount));

        grandTotal=String.valueOf(totalPrice1+taxAmount);
        grossAmount.setText(String.valueOf(grandTotal));

        Double mgrandTotal=Double.parseDouble(grandTotal);
        int grandTotalInt = mgrandTotal.intValue();
        double mGrandTotal=(double)grandTotalInt+1;
        Double finalval=mGrandTotal-mgrandTotal;

        roundOffValue =Double.parseDouble(String.format("%.2f", finalval));
        if(roundOffValue >=0.50 && roundOffValue <1.0)
        {
            know="a";
            roundOffValue = roundOffValue -1;
            grandTotal=String.format("%.2f",Float.parseFloat(grandTotal)+ roundOffValue);
            roundoff.setText(String.format("%.2f", roundOffValue));
        }
        else if(roundOffValue > 0.00 && roundOffValue <0.50)
        {
            know="b";
             grandTotal=String.format("%.2f",Float.parseFloat(grandTotal)+roundOffValue);
             roundoff.setText(String.format("%.2f", roundOffValue));
        }
        else{
            know="d";
             grandTotal=String.format("%.2f",Float.parseFloat(grandTotal));
             roundoff.setText("");
        }
        grossAmount.setText(String.valueOf(grandTotal));
        totalPrice=netAmount.getText().toString();
        totalTax=tax.getText().toString();
    }
    public void init(){
        parentLayout=findViewById(R.id.ll);
        search=findViewById(R.id.search);
        datalayer=new Datalayer(getApplicationContext());
        headingList=new ArrayList<>();
        datalayer=new Datalayer(getApplicationContext());
        itemDetailList=new ArrayList<>();
        headname=findViewById(R.id.headname);
        menuListView =findViewById(R.id.menulist);
        cartListView =findViewById(R.id.cartlist);
        itemCount=findViewById(R.id.itemcount);
        netAmount=findViewById(R.id.netamount);
        tax=findViewById(R.id.tax);
        roundoff=findViewById(R.id.roundoff);
        grossAmount=findViewById(R.id.grossamount);
        unHoldBill=findViewById(R.id.unholdbill);
        billOnHold=findViewById(R.id.billonhold);
        dishComment=findViewById(R.id.dishcomment);
        logoff=findViewById(R.id.logoff);
        done=findViewById(R.id.done);
        setup=findViewById(R.id.setup);
        tender=findViewById(R.id.tender);
        tender.setOnClickListener(this);
        billOnHold.setOnClickListener(this);
        unHoldBill.setOnClickListener(this);
        dishComment.setOnClickListener(this);
        logoff.setOnClickListener(this);
        done.setOnClickListener(this);
        setup.setOnClickListener(this);
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
                GetCartItem();
                netAmount.setText("");
                tax.setText("");
                grossAmount.setText("");
            }
        });
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
    private boolean createReceiptData(){
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
            textData.append(companyAddress1).append("\n").append(companyAddress2).append("\n").append(companyAddress3).append("\n");
            textData.append(companyGstNo).append("\n");
            if(!companyHeader1.isEmpty())
                textData.append(companyHeader1).append("\n");
            if(!companyHeader2.isEmpty())
                textData.append(companyHeader2).append("\n");
            textData.append("----------------------------------------\n");
            textData.append(rightpad("Date : ", 7)).append(rightpad(returnBillDate, 11)).append(leftpad("TIME :", 11)).append(rightpad(returnBillTime, 11)).append("\n");
            textData.append(rightpad("USER : ", 7)).append(rightpad(waiterCode, 11)).append(leftpad("BILLNO :", 11)).append(rightpad(billNumber, 11)).append("\n");
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
            textData.append("Items  ").append(leftpad(String.valueOf(totalItem), 3)).append(leftpad("SUBTOTAL:      ", 23)).append(leftpad(String.format("%.2f", Float.parseFloat(totalPrice)), 7)).append("\n");
            if(!totalTax.equals("0.0")) {
                textData.append(leftpad("* SGST @ 2.50%:      ", 33)).append(leftpad(String.format("%.2f", Float.parseFloat(totalTax) / 2), 7)).append("\n");
                textData.append(leftpad("* CGST @ 2.50%:      ", 33)).append(leftpad(String.format("%.2f", Float.parseFloat(totalTax) / 2), 7)).append("\n");
            }
            switch (know) {
                case "a":
                    textData.append(leftpad("R. off:       ", 33)).append(leftpad(String.format("%.2f", roundOffValue), 7)).append("\n");
                    textData.append(leftpad("TOTAL:      ", 33)).append(leftpad(grandTotal, 7)).append("\n");
                    break;
                case "b":
                    textData.append(leftpad("R. off:       ", 33)).append(leftpad("+" + String.format("%.2f", roundOffValue), 7)).append("\n");
                    textData.append(leftpad("TOTAL:      ", 33)).append(leftpad(grandTotal, 7)).append("\n");
                    break;
                case "d":
                    textData.append(leftpad("TOTAL:      ", 33)).append(leftpad(grandTotal, 7)).append("\n");
                    break;
            }
            Double db=Double.parseDouble(grandTotal);
            int value = db.intValue();
            textData.append(leftpad(convert(value), 40)).append("\n");
            textData.append("\n");
            if(!comapnyFooter1.isEmpty())
                textData.append(comapnyFooter1).append("\n");
            if(!comapnyFooter2.isEmpty())
                textData.append(comapnyFooter2).append("\n");
            if(!comapnyFooter3.isEmpty())
                textData.append(comapnyFooter3).append("\n");
            if(!comapnyFooter4.isEmpty())
                textData.append(comapnyFooter4).append("\n");
            if(!comapnyFooter5.isEmpty())
                textData.append(comapnyFooter5).append("\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addFeedLine";
            mPrinter.addFeedLine(2);
            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            return false;
        }
        return true;
    }
    public void getBillDetail(final String itemdetail){
        Double d=Double.parseDouble(grandTotal);
        final int val=d.intValue();
        StringRequest myReq = new StringRequest(Request.Method.POST,Constent.HTTP+Constent.IP+Constent.SAVEBILL,
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("custName","");
                    jsonObject.put("custPhone","");
                    jsonObject.put("roundOff",0);
                    jsonObject.put("grandTotal",val);
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
                Toast.makeText(MenuDetail.this, "success", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONObject jsonObject1=jsonObject.getJSONObject("output");
                    billNumber=jsonObject1.getString("ReturnBill_No");
                    returnBillTime =jsonObject1.getString("ReturnBillTime");
                    returnBillDate =jsonObject1.getString("ReturnBillDate");
                  //  Display();
                    if(!targetIp.isEmpty())
                        runPrintReceiptSequence();
                    else
                        Toast.makeText(MenuDetail.this,"Please setup your Printer setup",Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    hideProgressDialouge();
                    Toast.makeText(MenuDetail.this,"error: "+e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        };
    }
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialouge();
                Toast.makeText(MenuDetail.this,"error: "+error.toString(),Toast.LENGTH_LONG).show();
            }
        };
    }
    public void ShowDialouge(){
        dialog = new Dialog(MenuDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customerdetail);
        Button dialogButton =dialog.findViewById(R.id.submit);
        ImageView back=dialog.findViewById(R.id.back);
        final EditText cusName =dialog.findViewById(R.id.cusname);
        final EditText cusPhonee =dialog.findViewById(R.id.phoneno);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                custName=cusName.getText().toString().trim();
                custPhone=cusPhonee.getText().toString().trim();//[a-zA-Z.? ]*
                if(custName.isEmpty()) {
                    if(!Pattern.matches("[a-zA-Z.? ]*", custPhone))
                    {
                        if(custPhone.length()!=10)
                        {
                            Toast.makeText(MenuDetail.this,"Name should not ",Toast.LENGTH_LONG).show();
                        }
                        else {

                        }
                        Toast.makeText(MenuDetail.this, "Please enter Customer Name", Toast.LENGTH_LONG).show();
                    }
                }
                else if(!custPhone.isEmpty())
                {
                    if(!Pattern.matches("[a-zA-Z]+", custPhone))
                    {
                        if(custPhone.length()!=10)
                        {
                            Toast.makeText(MenuDetail.this,"Please enter Valid Phone Number",Toast.LENGTH_LONG).show();
                        }
                        else {
                            getCompanydetail();
                            for (int i = 0; i < cartItemListDetail.size(); i++) {
                                String itemDetail1 = cartItemListDetail.get(i).getCode() + "~|~" + cartItemListDetail.get(i).getQuantity() + "~|~" + cartItemListDetail.get(i).getComment() + "~|~" + cartItemListDetail.get(i).getPrice() + "~|~" + cartItemListDetail.get(i).getTaxRate() + "~|~" + cartItemListDetail.get(i).getTaxAmount();
                                itemDetail = itemDetail + (itemDetail1 + "||");
                            }
                            showProgressDialouge();
                            cusPhonee.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            getBillDetail(itemDetail);
                        }
                    }
                }
            }
        });
        dialog.show();
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
        textData.append("DATE :   ").append(rightpad(returnBillDate, 8)).append(leftpad("TIME :", 11)).append(rightpad(returnBillTime, 11)).append("\n");
        textData.append("USER :   "+rightpad(waiterCode,8)+leftpad("BILLNO :",11)+rightpad(billNumber,11)+"\n");
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
        textData.append("Items  "+leftpad(String.valueOf(totalItem),3)+leftpad("SUBTOTAL:      ",23)+leftpad(String.format("%.2f",Float.parseFloat(totalPrice)),7)+"\n");
        if(!totalTax.equals("0.0")) {
            textData.append(leftpad("* SGST @ 2.50%:      ", 33) + leftpad(String.format("%.2f", Float.parseFloat(totalTax) / 2), 7) + "\n");
            textData.append(leftpad("* CGST @ 2.50%:      ", 33) + leftpad(String.format("%.2f", Float.parseFloat(totalTax) / 2), 7) + "\n");
        }
        if(know.equals("a"))
        {
            textData.append(leftpad("R. off:       ",33)+leftpad(String.format("%.2f",roundOffValue),7)+"\n");
            textData.append(leftpad("TOTAL:      ",33)+leftpad(grandTotal,7)+"\n");
        }
        else if(know.equals("b"))
        {
            textData.append(leftpad("R. off:       ",33)+leftpad("+"+String.format("%.2f",roundOffValue),7)+"\n");
            textData.append(leftpad("TOTAL:      ",33)+leftpad(grandTotal,7)+"\n");
        }
        else if(know.equals("d"))
            textData.append(leftpad("TOTAL:      ",33)+leftpad(grandTotal,7)+"\n");
        Double db=Double.parseDouble(grandTotal);
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

        final AlertDialog build = new AlertDialog.Builder(context)
                .setMessage(textData.toString())
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .create();

        build.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) build).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                       build.dismiss();
                    }
                });
            }
        });
        build.show();
    }
}
