package com.ssl.sspls.billingapp.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ssl.sspls.billingapp.Localstorage.Datalayer;
import com.ssl.sspls.billingapp.Model.MenuDetailList;
import com.ssl.sspls.billingapp.Model.WaiterDetailList;
import com.ssl.sspls.billingapp.R;
import com.ssl.sspls.billingapp.Url.Constent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
public class LoginScreen extends AppCompatActivity {
    ProgressDialog pDialog;
    RequestQueue requestQueue;
    Datalayer datalayer;
    Spinner waiterListView;
    ArrayList<WaiterDetailList> WaiterDetailList;
    ArrayList<String> waiterNameList;
    EditText pass;
    Button login;
    String code;
    int modelNo;
    MenuDetailList menuDetailList;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        getSupportActionBar().hide();
        settings =getSharedPreferences("MyPref", MODE_PRIVATE);
        modelNo=settings.getInt("modelNo",0);
        waiterListView=findViewById(R.id.wlist);
        login=findViewById(R.id.login);
        pass=findViewById(R.id.pass);
        datalayer=new Datalayer(getApplicationContext());
        requestQueue=Volley.newRequestQueue(LoginScreen.this);
        WaiterDetailList =new ArrayList<>();
        waiterNameList=new ArrayList<>();
        getWaiter();
        waiterListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                code=WaiterDetailList.get(position).getPassword();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1=pass.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editorSP = sharedPreferences.edit();
                editorSP.putString("waiterCode",pass1);
                editorSP.apply();
                if(pass1.equals(code))
                    getData();
                else
                    Toast.makeText(LoginScreen.this,"Password does not match",Toast.LENGTH_SHORT).show();//bombik
                }
           });
    }
    public void getData(){
        datalayer.DeleteMenuDetail();
        showProgressDialouge();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constent.HTTP+Constent.IP+Constent.MENU,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getCompanyDetail();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i <jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String Code = jsonObject.getString("ICode");
                        String Name = jsonObject.getString("IName");
                        String Desc = jsonObject.getString("ItDesc");
                        String Rate = jsonObject.getString("Rate");
                        String Dept = jsonObject.getString("DeptName");
                        String Heading = jsonObject.getString("Heading");
                        String Veg=jsonObject.getString("Veg");
                        String RateTax=jsonObject.getString("Tax");
                       // String imagePath=jsonObject.getString("imagePath");
                        String famount=String.valueOf((Double.parseDouble(Rate)*(Double.parseDouble(RateTax)/100)));
                        menuDetailList=new MenuDetailList(Code,Name,Desc,Rate,Dept,Heading,"0","http://"+Constent.IP+"/"+Code+".jpg","0","0",Veg,RateTax,famount);
                        datalayer.open();
                        datalayer.AddMenu(menuDetailList);
                        datalayer.close();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             System.out.println("your error: "+error.toString());
            }
        });
        requestQueue.add(stringRequest);
    }
    public void getWaiter(){
        datalayer.DeleteMenuDetail();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constent.HTTP+Constent.IP+Constent.WAITER,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i <jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String Cr_Code = jsonObject.getString("Cr_Code");
                        String Cr_Name = jsonObject.getString("Cr_Name");
                        String Password = jsonObject.getString("Password");
                        waiterNameList.add(Cr_Name);
                        WaiterDetailList.add(new WaiterDetailList(Cr_Code,Cr_Name,Password));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginScreen.this,android.R.layout.simple_spinner_item, waiterNameList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    waiterListView.setAdapter(adapter);

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // hideProgressDialouge();
                System.out.println("your error: "+error.toString());
            }
        });
        requestQueue.add(stringRequest);
    }
    public void getCompanyDetail(){
        datalayer.DeleteCompanyDetail();
        StringRequest stringRequest=new StringRequest(Request.Method.GET,Constent.HTTP+Constent.IP+Constent.COMPANY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getSettalement();
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        String CNAME=jsonObject.getString("CNAME");
                        String ADD1=jsonObject.getString("ADD1");
                        String ADD2=jsonObject.getString("ADD2");
                        String ADD3=jsonObject.getString("ADD3");
                        String PHONE=jsonObject.getString("PHONE");
                        String EMAILID=jsonObject.getString("EMAILID");
                        String STNO=jsonObject.getString("STNO");
                        String PAGE_HEADER_1=jsonObject.getString("PAGE_HEADER_1");
                        String PAGE_HEADER_2=jsonObject.getString("PAGE_HEADER_2");

                        String PAGE_FOOTER_1=jsonObject.getString("PAGE_FOOTER_1");
                        String PAGE_FOOTER_2=jsonObject.getString("PAGE_FOOTER_2");
                        String PAGE_FOOTER_3=jsonObject.getString("PAGE_FOOTER_3");
                        String PAGE_FOOTER_4=jsonObject.getString("PAGE_FOOTER_4");
                        String PAGE_FOOTER_5=jsonObject.getString("PAGE_FOOTER_5");
                        datalayer.AddCompanyDetail(CNAME,ADD1,ADD2,ADD3,PHONE,EMAILID,STNO,PAGE_HEADER_1,PAGE_HEADER_2,PAGE_FOOTER_1,PAGE_FOOTER_2,PAGE_FOOTER_3,PAGE_FOOTER_4,PAGE_FOOTER_5);
                    }
                }
                catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
    public void getSettalement(){
        datalayer.DeleteSettlementDetail();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constent.HTTP+Constent.IP+Constent.SETTLE,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    hideProgressDialouge();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String PayMode = jsonObject.getString("Payment_Mode");
                        String Mode = jsonObject.getString("Mode");
                        datalayer.AddSettlementDetail(PayMode,Mode);
                    }
                        Intent i=new Intent(LoginScreen.this,MenuDetail.class);
                        startActivity(i);
                        finish();
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(LoginScreen.this,"error: "+error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }
    protected void showProgressDialouge(){
        pDialog = new ProgressDialog(LoginScreen.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }
    protected  void hideProgressDialouge(){
        if( pDialog.isShowing())
            pDialog.dismiss();
    }
}
