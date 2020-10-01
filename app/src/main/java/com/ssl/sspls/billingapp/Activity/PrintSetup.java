package com.ssl.sspls.billingapp.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.epos2.Log;
import com.epson.epos2.printer.Printer;
import com.ssl.sspls.billingapp.Model.SpnModelsItem;
import com.ssl.sspls.billingapp.R;
import com.ssl.sspls.billingapp.ShowMsg;

import java.util.Objects;
public class PrintSetup extends AppCompatActivity implements View.OnClickListener{
    ImageView back;
    private TextView mEditTarget = null,skip;
    private Spinner mSpnSeries = null;
    private Spinner mSpnLang = null;
    int modelNo,language;
    String targetIp;
    RadioButton pdisplay,psetup;
    LinearLayout linearLayout;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_setup);
        getSupportActionBar().hide();
        settings =getSharedPreferences("MyPref", MODE_PRIVATE);
        //waiterCode=settings.getString("waiterCode","");
        modelNo=settings.getInt("modelNo",0);
        language=settings.getInt("language",0);//modeType
        targetIp=settings.getString("targetIp","");
        pdisplay=findViewById(R.id.pdisplay);
        psetup=findViewById(R.id.psetup);
        linearLayout=findViewById(R.id.linearLayout);
        linearLayout.setVisibility(View.GONE);
        back=findViewById(R.id.back);
        pdisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                linearLayout.setVisibility(View.GONE);
                SharedPreferences sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editorSP = sharedPreferences.edit();
                editorSP.putString("print","display");
                editorSP.apply();
                Intent i=new Intent(PrintSetup.this,MenuDetail.class);
                startActivity(i);
                finish();
            }
        });
        psetup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Context mContext = this;
        int[] target = {
                R.id.btnDiscovery,
                R.id.btnSampleReceipt
        };
        for (int aTarget : target) {
            Button button = findViewById(aTarget);
            button.setOnClickListener(this);
        }
        mSpnSeries =findViewById(R.id.spnModel);
        ArrayAdapter<SpnModelsItem> seriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m10), Printer.TM_M10));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m30), Printer.TM_M30));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p20), Printer.TM_P20));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60), Printer.TM_P60));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60ii), Printer.TM_P60II));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p80), Printer.TM_P80));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t20), Printer.TM_T20));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t60), Printer.TM_T60));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t70), Printer.TM_T70));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t81), Printer.TM_T81));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t82), Printer.TM_T82));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t83), Printer.TM_T83));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t88), Printer.TM_T88));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90), Printer.TM_T90));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90kp), Printer.TM_T90KP));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u220), Printer.TM_U220));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u330), Printer.TM_U330));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_l90), Printer.TM_L90));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_h6000), Printer.TM_H6000));
        mSpnSeries.setAdapter(seriesAdapter);
        mSpnSeries.setSelection(0);

        mSpnLang =findViewById(R.id.spnLang);
        ArrayAdapter<SpnModelsItem> langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_southasia), Printer.MODEL_SOUTHASIA));
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_ank), Printer.MODEL_ANK));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_japanese), Printer.MODEL_JAPANESE));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_chinese), Printer.MODEL_CHINESE));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_taiwan), Printer.MODEL_TAIWAN));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_korean), Printer.MODEL_KOREAN));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_thai), Printer.MODEL_THAI));

        mSpnLang.setAdapter(langAdapter);
        mSpnLang.setSelection(0);

        try {
//            Log.setLogSettings(mContext, Log.PERIOD_TEMPORARY, Log.OUTPUT_STORAGE, null, 0, 1,
                   // Log.LOGLEVEL_LOW);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "setLogSettings", mContext);
        }
        mEditTarget = findViewById(R.id.edtTarget);
        if(!targetIp.isEmpty())
            mEditTarget.setText(targetIp);
    }
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        if (data != null && resultCode == RESULT_OK) {
            String target = data.getStringExtra(getString(R.string.title_target));
            if (target != null) {
                TextView mEdtTarget =findViewById(R.id.edtTarget);
                mEdtTarget.setText(target);
            }
        }
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnDiscovery:
                intent = new Intent(this, DiscoveryActivity.class);
                startActivityForResult(intent, 0);
                break;

            case R.id.btnSampleReceipt:
                updateButtonState();
                int modelNo=((SpnModelsItem) mSpnSeries.getSelectedItem()).getModelConstant();
                int language=((SpnModelsItem) mSpnLang.getSelectedItem()).getModelConstant();
                String targetIp=mEditTarget.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editorSP = sharedPreferences.edit();
                editorSP.putInt("modelNo",modelNo);
                editorSP.putInt("language",language);
                editorSP.putString("targetIp",targetIp);
                editorSP.putString("print","print");
                editorSP.apply();
                Toast.makeText(PrintSetup.this,"Printer setup Successfull....",Toast.LENGTH_LONG).show();
                Intent i=new Intent(PrintSetup.this,MenuDetail.class);
                startActivity(i);
                finish();
                break;
            default:
                break;
        }
    }
    private void updateButtonState() {
        Button btnReceipt =findViewById(R.id.btnSampleReceipt);
        btnReceipt.setEnabled(false);
    }
}
