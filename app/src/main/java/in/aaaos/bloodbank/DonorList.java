package in.aaaos.bloodbank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DonorList extends AppCompatActivity {
    String city,blood;
    private ProgressDialog dialog;
    AlertDialog.Builder builder;
    ArrayList<Values> imgs;
    Toolbar toolbar;
    private RecyclerView.Adapter mAdapter;
    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_list);
        imgs=new ArrayList<>();
        builder = new AlertDialog.Builder(DonorList.this);
        city = getIntent().getStringExtra("city");
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
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        dialog = new ProgressDialog(DonorList.this);
        blood = getIntent().getStringExtra("blood");
        if (isNetworkAvailable()) {
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.show();
            String url = "http://spa.aaaos.in/Api/GetData.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (response.equals("norecord")) {
                        builder.setMessage("No record found");
                        DisplayAlert("No");

                    } else {
                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            for (int i=0;i<jsonArray.length();i++){
                                Values values=new Values();
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                values.setName(jsonObject.getString("Name"));
                                values.setPhone(jsonObject.getString("Phone"));
                                values.setCity(jsonObject.getString("City"));
                                values.setBlood(jsonObject.getString("Blood"));
                                imgs.add(values);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapter = new MyRecyclerViewAdapter(DonorList.this, imgs);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data
                            JSONObject obj = new JSONObject(res);
                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        } catch (JSONException e2) {
                            // returned data is not JSONObject?
                            e2.printStackTrace();
                        }
                    }
                    Snackbar.make(findViewById(android.R.id.content), "Please try after some time",
                            Snackbar.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("blood", blood);
                    params.put("city", city);
                    return params;
                }

            };
            AppController.getInstance().addToRequestQueue(stringRequest);
        }
        else{
            builder.setMessage("No Internet");
            DisplayAlert("No Network");
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) DonorList.this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                if (code.equals("No")) {
                    dialogInterface.dismiss();
                    finish();
                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }
}
