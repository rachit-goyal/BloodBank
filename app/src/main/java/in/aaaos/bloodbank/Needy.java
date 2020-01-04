package in.aaaos.bloodbank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Needy extends AppCompatActivity {
    TextView submit,blood,city;
    ListView bloodgroup,citylist;
    BottomSheetDialog bottomSheetDialog;
    Toolbar toolbar;
    AlertDialog.Builder builder;
    ArrayAdapter list_adapter,listadaptercity;
    String[] bloodlist = new String[] { "A+",
            "A-",
            "B+",
            "B-",
            "AB+",
            "AB-",
            "O+",
            "O-",
    };
    String[] cities = new String[] { "Agra","Aligarh","Hatras","Firozabad","Mathura",
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy);
        blood=(TextView) findViewById(R.id.bloodgrouptext);
        city=(TextView) findViewById(R.id.citytext);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        builder = new AlertDialog.Builder(Needy.this);
        submit=(TextView)findViewById(R.id.submit);
        list_adapter = new ArrayAdapter(Needy.this,android.R.layout.simple_list_item_1, android.R.id.text1,bloodlist){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        };
        listadaptercity = new ArrayAdapter(Needy.this,android.R.layout.simple_list_item_1, android.R.id.text1,cities){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        };
        blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowBottomSheet();
            }
        });
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowBottom();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(blood.getText().toString())){
                    blood.setError("Select your blood group");
                }
                else  if(TextUtils.isEmpty(city.getText().toString())){
                    city.setError("Select city");
                }
                else{
                    if(isNetworkAvailable()) {
                        Intent i = new Intent(Needy.this, DonorList.class);
                        i.putExtra("blood", blood.getText().toString().trim());
                        i.putExtra("city", city.getText().toString());
                        startActivity(i);
                    }
                    else{
                        builder.setMessage("No Internet");
                        DisplayAlert("No Network");
                    }
                }
            }
        });

    }

    private void ShowBottom() {
        View view = Needy.this.getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        citylist = (ListView) view.findViewById(R.id.lv_languages);
        citylist.setAdapter(listadaptercity);
        bottomSheetDialog = new BottomSheetDialog(Needy.this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        citylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                city.setText(citylist.getItemAtPosition(i).toString());
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void ShowBottomSheet() {
        View view = Needy.this.getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        bloodgroup = (ListView) view.findViewById(R.id.lv_languages);
        bloodgroup.setAdapter(list_adapter);
        bottomSheetDialog = new BottomSheetDialog(Needy.this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        bloodgroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                blood.setText(bloodgroup.getItemAtPosition(i).toString());
                bottomSheetDialog.dismiss();
            }
        });

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Needy.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void DisplayAlert(final String code) {
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (code.equals("No Network")) {
                    dialogInterface.dismiss();
                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

}
