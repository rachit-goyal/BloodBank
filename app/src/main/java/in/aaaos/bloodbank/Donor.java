package in.aaaos.bloodbank;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Donor extends AppCompatActivity {
    EditText name, phone, age, edt;
    RadioGroup radioGroup;
    RadioButton radiosexbutton;
    Toolbar toolbar;
    CheckBox terms;
    Dialog dialog;
    private ProgressDialog pdialog;
    AlertDialog.Builder builder;
    TextView submit, blood, city,delete,condition;
    String sex,val;
    ListView bloodgroup, citylist;
    BottomSheetDialog bottomSheetDialog;
    private static final int PERMISSION_REQUEST = 100;
    ArrayAdapter list_adapter, listadaptercity;
    String[] bloodlist = new String[]{"A+",
            "A-",
            "B+",
            "B-",
            "AB+",
            "AB-",
            "O+",
            "O-",
    };
    String[] cities = new String[]{"Agra", "Aligarh", "Hatras", "Firozabad", "Mathura",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);
        name = (EditText) findViewById(R.id.editTextname);
        phone = (EditText) findViewById(R.id.editTextphone);
        age = (EditText) findViewById(R.id.editTextage);
        pdialog = new ProgressDialog(Donor.this);
        terms=(CheckBox)findViewById(R.id.terms);
        blood = (TextView) findViewById(R.id.bloodgrouptext);
        city = (TextView) findViewById(R.id.citytext);
        delete=(TextView)findViewById(R.id.delete);
        condition=(TextView)findViewById(R.id.condition);
        builder = new AlertDialog.Builder(Donor.this);
        submit = (TextView) findViewById(R.id.submit);
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

        radioGroup = (RadioGroup) findViewById(R.id.radiosex);
        list_adapter = new ArrayAdapter(Donor.this, android.R.layout.simple_list_item_1, android.R.id.text1, bloodlist) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        };
        listadaptercity = new ArrayAdapter(Donor.this, android.R.layout.simple_list_item_1, android.R.id.text1, cities) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        };
        condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Donor.this,Terms.class);
                startActivity(i);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("Main", MODE_PRIVATE);
                final String restoredText = prefs.getString("pho", null);
                if (restoredText != null) {
                    if(isNetworkAvailable()){
                        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(Donor.this);
                        dialog1.setCancelable(true);
                        dialog1.setTitle("Delete Donor");
                        dialog1.setMessage("Delete my number "+restoredText+" as donor");
                        dialog1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogwe, int id) {
                                pdialog.setMessage("Please Wait...");
                                pdialog.setCancelable(false);
                                pdialog.show();
                                String urr = "http://spa.aaaos.in/Api/delete.php";
                                StringRequest sss = new StringRequest(Request.Method.POST, urr, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        dialogwe.dismiss();
                                        if (response.contains("deleted")) {
                                            name.setText(null);
                                            phone.setText(null);
                                            age.setText(null);
                                            blood.setText(null);
                                            city.setText(null);
                                            radioGroup.clearCheck();
                                            Snackbar.make(findViewById(android.R.id.content), "Donor deleted",
                                                    Snackbar.LENGTH_SHORT).show();
                                            SharedPreferences.Editor editor = getSharedPreferences("Main", MODE_PRIVATE).edit();
                                            editor.putString("pho", null);
                                            editor.apply();
                                            pdialog.dismiss();
                                        } else if (response.contains("notdeleted")) {
                                            pdialog.dismiss();
                                            Snackbar.make(findViewById(android.R.id.content), "Please try after some time",
                                                    Snackbar.LENGTH_SHORT).show();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        pdialog.dismiss();
                                        dialogwe.dismiss();
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
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("phone", restoredText);
                                        return params;

                                    }

                                };
                                AppController.getInstance().addToRequestQueue(sss);


                                //Action for "Delete".
                            }
                        });


                        final AlertDialog alert = dialog1.create();
                        alert.show();



                    }
                }
                else{
                    builder.setMessage("You have not register any number from this device.");
                    DisplayAlert("No Network");
                }
            }
        });
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
                if (TextUtils.isEmpty(name.getText().toString())) {
                    name.setError("Field can not be blank");
                    Log.d("abcdefghij", name.getText().toString());
                } else if (TextUtils.isEmpty(phone.getText().toString())) {
                    phone.setError("Field can not be blank");
                    Log.d("abcdefghij", phone.getText().toString());
                } else if (phone.length() < 10) {
                    phone.setError("Enter valid no.");
                    Log.d("abcdefghij", age.getText().toString());
                } else if (TextUtils.isEmpty(age.getText().toString())) {
                    age.setError("Enter your age");
                } else if (age.toString().trim().length() > 0) {
                    String ag = age.getText().toString().trim();
                    if (Integer.parseInt(ag) < 18) {
                        age.setError("Minors are not allowed");
                    } else if (Integer.parseInt(ag) > 60) {
                        age.setError("You are not allowed");
                    }
                }
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(Donor.this, "Select your gender", Toast.LENGTH_SHORT).show();

                }
                else if(radioGroup.getCheckedRadioButtonId() > -1){
                    int selectedId=radioGroup.getCheckedRadioButtonId();
                    radiosexbutton=(RadioButton)findViewById(selectedId);
                    sex=radiosexbutton.getText().toString();
                    Log.d("value",sex);
                }
                 if (TextUtils.isEmpty(blood.getText().toString())) {
                    blood.setError("Select your blood group");
                } else if (TextUtils.isEmpty(city.getText().toString())) {
                    city.setError("Select your blood group");
                }
                else if(!terms.isChecked()){
                     Toast.makeText(Donor.this,"Please click terms and condition to proceed",Toast.LENGTH_SHORT).show();
                 }
                else {
                     SharedPreferences prefs = getSharedPreferences("Main", MODE_PRIVATE);
                     final String restoredText = prefs.getString("pho", null);
                     if(restoredText==null) {

                             if (isNetworkAvailable()) {

                                 pdialog.setMessage("Please Wait...");
                                 pdialog.setCancelable(false);
                                 pdialog.show();

                                  val = "" + ((int) (Math.random() * 9000) + 1000);
                                 Log.d("random", val);
                                 final String url = "http://control.msg91.com/api/sendotp.php?otp_length=4&authkey=219703Am0FgWtL5b1bd563&message=" + val + " is your verification code for donor.&sender=BLDDNR&mobile=91" + phone.getText().toString() + "&otp=" + val;
                                 StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                     @Override
                                     public void onResponse(String response) {
                                         Log.d("random", response);
                                         try {
                                             JSONObject jsonObject = new JSONObject(response);
                                             if (jsonObject.getString("type").equals("success")) {
                                                 pdialog.dismiss();
                                                 SharedPreferences.Editor editor = getSharedPreferences("Main", MODE_PRIVATE).edit();
                                                 editor.putString("pho", phone.getText().toString().trim());
                                                 editor.apply();
                                                 dialog = new Dialog(Donor.this);
                                                 dialog.setContentView(R.layout.dia);
                                                 dialog.setCancelable(false);
                                                 edt = dialog.findViewById(R.id.code);
                                                 final TextView sub = dialog.findViewById(R.id.diasubmit);
                                                 final TextView resend = dialog.findViewById(R.id.resend);
                                                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                     if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                                                         if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                                                             Snackbar.make(findViewById(android.R.id.content), "You need to grant READ SMS permission to auto read sms",
                                                                     Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View v) {
                                                                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                         requestPermissions(new String[]{android.Manifest.permission.READ_SMS}, PERMISSION_REQUEST);
                                                                     }
                                                                 }
                                                             }).show();
                                                         } else {
                                                             requestPermissions(new String[]{android.Manifest.permission.READ_SMS}, PERMISSION_REQUEST);
                                                         }
                                                     } else {
                                                         SmsReceiver.bindListener(new SmsListener() {
                                                             @Override
                                                             public void messageReceived(String messageText) {
                                                                 Log.d("Text", messageText);
                                                                 edt.setText(messageText);
                                                                 if (edt.getText().toString().trim().equals(val)) {
                                                                     dialog.dismiss();
                                                                     pdialog.setMessage("Please Wait...");
                                                                     pdialog.setCancelable(false);
                                                                     pdialog.show();
                                                                     String url = "http://spa.aaaos.in/Api/Data.php";
                                                                     StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                                         @Override
                                                                         public void onResponse(String response) {
                                                                             if (dialog != null)
                                                                                 dialog.dismiss();
                                                                             if (response.equals("inserted")) {
                                                                                 name.setText(null);
                                                                                 phone.setText(null);
                                                                                 age.setText(null);
                                                                                 blood.setText(null);
                                                                                 city.setText(null);
                                                                                 radioGroup.clearCheck();
                                                                                 Toast.makeText(Donor.this, "Thanks you", Toast.LENGTH_SHORT).show();
                                                                                 pdialog.dismiss();
                                                                                 finish();
                                                                             } else if (response.equals("updated")) {
                                                                                 name.setText(null);
                                                                                 phone.setText(null);
                                                                                 age.setText(null);
                                                                                 blood.setText(null);
                                                                                 city.setText(null);
                                                                                 radioGroup.clearCheck();
                                                                                 Toast.makeText(Donor.this, "Donor details updated", Toast.LENGTH_SHORT).show();
                                                                                 pdialog.dismiss();
                                                                                 finish();
                                                                             } else {
                                                                                 pdialog.dismiss();
                                                                                 Snackbar.make(findViewById(android.R.id.content), "Please try after some time",
                                                                                         Snackbar.LENGTH_SHORT).show();
                                                                             }
                                                                         }
                                                                     }, new Response.ErrorListener() {
                                                                         @Override
                                                                         public void onErrorResponse(VolleyError error) {
                                                                             pdialog.dismiss();
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
                                                                     }) {
                                                                         @Override
                                                                         protected Map<String, String> getParams() throws AuthFailureError {
                                                                             Map<String, String> params = new HashMap<String, String>();
                                                                             params.put("name", name.getText().toString().trim());
                                                                             params.put("phone", phone.getText().toString().trim());
                                                                             params.put("age", age.getText().toString().trim());
                                                                             params.put("sex", sex);
                                                                             params.put("blood", blood.getText().toString());
                                                                             params.put("city", city.getText().toString());
                                                                             return params;
                                                                         }


                                                                     };
                                                                     AppController.getInstance().addToRequestQueue(str);
                                                                 }
                                                             }

                                                         });
                                                     }
                                                 } else {
                                                     SmsReceiver.bindListener(new SmsListener() {
                                                         @Override
                                                         public void messageReceived(String messageText) {
                                                             Log.d("Text", messageText);
                                                             edt.setText(messageText);
                                                             if (edt.getText().toString().trim().equals(val)) {
                                                                 dialog.dismiss();
                                                                 pdialog.setMessage("Please Wait...");
                                                                 pdialog.setCancelable(false);
                                                                 pdialog.show();
                                                                 String url = "http://spa.aaaos.in/Api/Data.php";
                                                                 StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                                     @Override
                                                                     public void onResponse(String response) {
                                                                         if (dialog != null)
                                                                             dialog.dismiss();
                                                                         if (response.equals("inserted")) {
                                                                             name.setText(null);
                                                                             phone.setText(null);
                                                                             age.setText(null);
                                                                             blood.setText(null);
                                                                             city.setText(null);
                                                                             radioGroup.clearCheck();
                                                                             Toast.makeText(Donor.this, "Thanks you", Toast.LENGTH_SHORT).show();
                                                                             pdialog.dismiss();
                                                                             finish();
                                                                         } else if (response.equals("updated")) {
                                                                             name.setText(null);
                                                                             phone.setText(null);
                                                                             age.setText(null);
                                                                             blood.setText(null);
                                                                             city.setText(null);
                                                                             radioGroup.clearCheck();
                                                                             Toast.makeText(Donor.this, "Donor details updated", Toast.LENGTH_SHORT).show();
                                                                             pdialog.dismiss();
                                                                             finish();
                                                                         } else {
                                                                             pdialog.dismiss();
                                                                             Snackbar.make(findViewById(android.R.id.content), "Please try after some time",
                                                                                     Snackbar.LENGTH_SHORT).show();
                                                                         }
                                                                     }
                                                                 }, new Response.ErrorListener() {
                                                                     @Override
                                                                     public void onErrorResponse(VolleyError error) {
                                                                         pdialog.dismiss();
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
                                                                 }) {
                                                                     @Override
                                                                     protected Map<String, String> getParams() throws AuthFailureError {
                                                                         Map<String, String> params = new HashMap<String, String>();
                                                                         params.put("name", name.getText().toString().trim());
                                                                         params.put("phone", phone.getText().toString().trim());
                                                                         params.put("age", age.getText().toString().trim());
                                                                         params.put("sex", sex);
                                                                         params.put("blood", blood.getText().toString());
                                                                         params.put("city", city.getText().toString());
                                                                         return params;
                                                                     }


                                                                 };
                                                                 AppController.getInstance().addToRequestQueue(str);
                                                             }
                                                         }

                                                     });
                                                 }

                                                 sub.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View view) {
                                                         if (TextUtils.isEmpty(edt.getText().toString())) {
                                                             edt.setError("Field can not be blank");
                                                         }
                                                         else if(!edt.getText().toString().trim().equals(val)){
                                                             edt.setText("Code not valid.");
                                                         }
                                                         else if(edt.getText().toString().trim().equals(val)){
                                                             dialog.dismiss();
                                                             pdialog.setMessage("Please Wait...");
                                                             pdialog.setCancelable(false);
                                                             pdialog.show();
                                                             String url = "http://spa.aaaos.in/Api/Data.php";
                                                             StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                                 @Override
                                                                 public void onResponse(String response) {
                                                                     if (dialog != null)
                                                                         dialog.dismiss();
                                                                     if (response.equals("inserted")) {
                                                                         name.setText(null);
                                                                         phone.setText(null);
                                                                         age.setText(null);
                                                                         blood.setText(null);
                                                                         city.setText(null);
                                                                         radioGroup.clearCheck();
                                                                         Toast.makeText(Donor.this, "Thanks you", Toast.LENGTH_SHORT).show();
                                                                         pdialog.dismiss();
                                                                         finish();
                                                                     } else if (response.equals("updated")) {
                                                                         name.setText(null);
                                                                         phone.setText(null);
                                                                         age.setText(null);
                                                                         blood.setText(null);
                                                                         city.setText(null);
                                                                         radioGroup.clearCheck();
                                                                         Toast.makeText(Donor.this, "Donor details updated", Toast.LENGTH_SHORT).show();
                                                                         pdialog.dismiss();
                                                                         finish();
                                                                     } else {
                                                                         pdialog.dismiss();
                                                                         Snackbar.make(findViewById(android.R.id.content), "Please try after some time",
                                                                                 Snackbar.LENGTH_SHORT).show();
                                                                     }
                                                                 }
                                                             }, new Response.ErrorListener() {
                                                                 @Override
                                                                 public void onErrorResponse(VolleyError error) {
                                                                     pdialog.dismiss();
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
                                                             }) {
                                                                 @Override
                                                                 protected Map<String, String> getParams() throws AuthFailureError {
                                                                     Map<String, String> params = new HashMap<String, String>();
                                                                     params.put("name", name.getText().toString().trim());
                                                                     params.put("phone", phone.getText().toString().trim());
                                                                     params.put("age", age.getText().toString().trim());
                                                                     params.put("sex", sex);
                                                                     params.put("blood", blood.getText().toString());
                                                                     params.put("city", city.getText().toString());
                                                                     return params;

                                                                 }


                                                             };
                                                             AppController.getInstance().addToRequestQueue(str);
                                                         }

                                                     }
                                                 });
                                                 resend.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View view) {
                                                         resend.setVisibility(View.GONE);
                                                         pdialog.setMessage("Please Wait...");
                                                         pdialog.setCancelable(false);
                                                         pdialog.show();
                                                         StringRequest st = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                             @Override
                                                             public void onResponse(String response) {
                                                                 try {
                                                                     JSONObject jsonObject = new JSONObject(response);
                                                                     if (jsonObject.getString("type").equals("success")) {
                                                                         pdialog.dismiss();
                                                                         SmsReceiver.bindListener(new SmsListener() {
                                                                             @Override
                                                                             public void messageReceived(String messageText) {
                                                                                 Log.d("Text", messageText);
                                                                                 edt.setText(messageText);
                                                                                 if (edt.getText().toString().trim().equals(val)) {
                                                                                     dialog.dismiss();
                                                                                     pdialog.setMessage("Please Wait...");
                                                                                     pdialog.setCancelable(false);
                                                                                     pdialog.show();
                                                                                     String url = "http://spa.aaaos.in/Api/Data.php";
                                                                                     StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                                                         @Override
                                                                                         public void onResponse(String response) {
                                                                                             if (dialog != null)
                                                                                                 dialog.dismiss();
                                                                                             if (response.equals("inserted")) {
                                                                                                 name.setText(null);
                                                                                                 phone.setText(null);
                                                                                                 age.setText(null);
                                                                                                 blood.setText(null);
                                                                                                 city.setText(null);
                                                                                                 radioGroup.clearCheck();
                                                                                                 Toast.makeText(Donor.this, "Thanks you", Toast.LENGTH_SHORT).show();
                                                                                                 pdialog.dismiss();
                                                                                                 finish();
                                                                                             } else if (response.equals("updated")) {
                                                                                                 name.setText(null);
                                                                                                 phone.setText(null);
                                                                                                 age.setText(null);
                                                                                                 blood.setText(null);
                                                                                                 city.setText(null);
                                                                                                 radioGroup.clearCheck();
                                                                                                 Toast.makeText(Donor.this, "Donor details updated", Toast.LENGTH_SHORT).show();
                                                                                                 pdialog.dismiss();
                                                                                                 finish();
                                                                                             } else {
                                                                                                 pdialog.dismiss();
                                                                                                 Snackbar.make(findViewById(android.R.id.content), "Please try after some time",
                                                                                                         Snackbar.LENGTH_SHORT).show();
                                                                                             }
                                                                                         }
                                                                                     }, new Response.ErrorListener() {
                                                                                         @Override
                                                                                         public void onErrorResponse(VolleyError error) {
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
                                                                                     }) {
                                                                                         @Override
                                                                                         protected Map<String, String> getParams() throws AuthFailureError {
                                                                                             Map<String, String> params = new HashMap<String, String>();
                                                                                             params.put("name", name.getText().toString().trim());
                                                                                             params.put("phone", phone.getText().toString().trim());
                                                                                             params.put("age", age.getText().toString().trim());
                                                                                             params.put("sex", sex);
                                                                                             params.put("blood", blood.getText().toString());
                                                                                             params.put("city", city.getText().toString());
                                                                                             return params;

                                                                                         }


                                                                                     };
                                                                                     AppController.getInstance().addToRequestQueue(str);
                                                                                 }
                                                                             }

                                                                         });
                                                                         sub.setOnClickListener(new View.OnClickListener() {
                                                                             @Override
                                                                             public void onClick(View view) {
                                                                                 if (edt.getText().toString().trim().equals(val)) {
                                                                                     dialog.dismiss();
                                                                                     pdialog.setMessage("Please Wait...");
                                                                                     pdialog.setCancelable(false);
                                                                                     pdialog.show();
                                                                                     String url = "http://spa.aaaos.in/Api/Data.php";
                                                                                     StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                                                         @Override
                                                                                         public void onResponse(String response) {
                                                                                             if (dialog != null)
                                                                                                 dialog.dismiss();
                                                                                             if (response.equals("inserted")) {
                                                                                                 name.setText(null);
                                                                                                 phone.setText(null);
                                                                                                 age.setText(null);
                                                                                                 blood.setText(null);
                                                                                                 city.setText(null);
                                                                                                 radioGroup.clearCheck();
                                                                                                 Toast.makeText(Donor.this, "Thanks you", Toast.LENGTH_SHORT).show();
                                                                                                 pdialog.dismiss();
                                                                                                 finish();
                                                                                             } else if (response.equals("updated")) {
                                                                                                 name.setText(null);
                                                                                                 phone.setText(null);
                                                                                                 age.setText(null);
                                                                                                 blood.setText(null);
                                                                                                 city.setText(null);
                                                                                                 radioGroup.clearCheck();
                                                                                                 Toast.makeText(Donor.this, "Donor details updated", Toast.LENGTH_SHORT).show();
                                                                                                 pdialog.dismiss();
                                                                                                 finish();
                                                                                             } else {
                                                                                                 pdialog.dismiss();
                                                                                                 Snackbar.make(findViewById(android.R.id.content), "Please try after some time",
                                                                                                         Snackbar.LENGTH_SHORT).show();
                                                                                             }
                                                                                         }
                                                                                     }, new Response.ErrorListener() {
                                                                                         @Override
                                                                                         public void onErrorResponse(VolleyError error) {
                                                                                             pdialog.dismiss();
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
                                                                                     }) {
                                                                                         @Override
                                                                                         protected Map<String, String> getParams() throws AuthFailureError {
                                                                                             Map<String, String> params = new HashMap<String, String>();
                                                                                             params.put("name", name.getText().toString().trim());
                                                                                             params.put("phone", phone.getText().toString().trim());
                                                                                             params.put("age", age.getText().toString().trim());
                                                                                             params.put("sex", sex);
                                                                                             params.put("blood", blood.getText().toString());
                                                                                             params.put("city", city.getText().toString());
                                                                                             return params;

                                                                                         }


                                                                                     };
                                                                                     AppController.getInstance().addToRequestQueue(str);
                                                                                 }
                                                                             }
                                                                         });
                                                                     }


                                                                 } catch (JSONException e) {
                                                                     e.printStackTrace();
                                                                 }
                                                             }
                                                         }, new Response.ErrorListener() {
                                                             @Override
                                                             public void onErrorResponse(VolleyError error) {

                                                             }
                                                         });
                                                         AppController.getInstance().addToRequestQueue(st);
                                                     }
                                                 });

                                                 dialog.show();

                                             }
                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }


                                     }
                                 }, new Response.ErrorListener() {
                                     @Override
                                     public void onErrorResponse(VolleyError error) {

                                     }
                                 });
                                 AppController.getInstance().addToRequestQueue(str);
                             }
                             else{
                                 builder.setMessage("No Network");
                                 DisplayAlert("No Network");
                             }
                         }
                         else if(restoredText!=null && restoredText.equals(phone.getText().toString().trim())){
                         if(isNetworkAvailable()){
                             pdialog.setMessage("Please Wait...");
                             pdialog.setCancelable(false);
                             pdialog.show();
                             String url = "http://spa.aaaos.in/Api/Data.php";
                             StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                 @Override
                                 public void onResponse(String response) {
                                     if (dialog != null)
                                         dialog.dismiss();
                                     if (response.equals("inserted")) {
                                         name.setText(null);
                                         phone.setText(null);
                                         age.setText(null);
                                         blood.setText(null);
                                         city.setText(null);
                                         radioGroup.clearCheck();
                                         Toast.makeText(Donor.this, "Thanks you", Toast.LENGTH_SHORT).show();
                                         pdialog.dismiss();
                                         finish();
                                     } else if (response.equals("updated")) {
                                         name.setText(null);
                                         phone.setText(null);
                                         age.setText(null);
                                         blood.setText(null);
                                         city.setText(null);
                                         radioGroup.clearCheck();
                                         Toast.makeText(Donor.this, "Donor details updated", Toast.LENGTH_SHORT).show();
                                         pdialog.dismiss();
                                         finish();
                                     } else {
                                         pdialog.dismiss();
                                         Snackbar.make(findViewById(android.R.id.content), "Please try after some time",
                                                 Snackbar.LENGTH_SHORT).show();
                                     }
                                 }
                             }, new Response.ErrorListener() {
                                 @Override
                                 public void onErrorResponse(VolleyError error) {
                                     pdialog.dismiss();
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
                             }) {
                                 @Override
                                 protected Map<String, String> getParams() throws AuthFailureError {
                                     Map<String, String> params = new HashMap<String, String>();
                                     params.put("name", name.getText().toString().trim());
                                     params.put("phone", phone.getText().toString().trim());
                                     params.put("age", age.getText().toString().trim());
                                     params.put("sex", sex);
                                     params.put("blood", blood.getText().toString());
                                     params.put("city", city.getText().toString());
                                     return params;

                                 }


                             };
                             AppController.getInstance().addToRequestQueue(str);
                         }
                         else {
                             builder.setMessage("No Network");
                             DisplayAlert("No Network");
                         }

                        }
                            else{
                         builder.setMessage("You can not register any other number from this device.");
                         DisplayAlert("No Network");
                     }


                 }


            }
        });

    }




    private void ShowBottom() {
        View view = Donor.this.getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        citylist = (ListView) view.findViewById(R.id.lv_languages);
        citylist.setAdapter(listadaptercity);
        bottomSheetDialog = new BottomSheetDialog(Donor.this);
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
        View view = Donor.this.getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        bloodgroup = (ListView) view.findViewById(R.id.lv_languages);
        bloodgroup.setAdapter(list_adapter);
        bottomSheetDialog = new BottomSheetDialog(Donor.this);
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
                = (ConnectivityManager) Donor.this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Snackbar.make(findViewById(android.R.id.content), "Permission Granted",
                            Snackbar.LENGTH_LONG).show();
                    SmsReceiver.bindListener(new SmsListener() {
                        @Override
                        public void messageReceived(String messageText) {
                            Log.d("Text", messageText);
                            edt.setText(messageText);
                            if (edt.getText().toString().trim().equals(val)) {
                                dialog.dismiss();
                                pdialog.setMessage("Please Wait...");
                                pdialog.setCancelable(false);
                                pdialog.show();
                                String url = "http://spa.aaaos.in/Api/Data.php";
                                StringRequest str = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (dialog != null)
                                            dialog.dismiss();
                                        if (response.equals("inserted")) {
                                            name.setText(null);
                                            phone.setText(null);
                                            age.setText(null);
                                            blood.setText(null);
                                            city.setText(null);
                                            radioGroup.clearCheck();
                                            Toast.makeText(Donor.this, "Thanks you", Toast.LENGTH_SHORT).show();
                                            pdialog.dismiss();
                                            finish();
                                        } else if (response.equals("updated")) {
                                            name.setText(null);
                                            phone.setText(null);
                                            age.setText(null);
                                            blood.setText(null);
                                            city.setText(null);
                                            radioGroup.clearCheck();
                                            Toast.makeText(Donor.this, "Donor details updated", Toast.LENGTH_SHORT).show();
                                            pdialog.dismiss();
                                            finish();
                                        } else {
                                            pdialog.dismiss();
                                            Snackbar.make(findViewById(android.R.id.content), "Please try after some time",
                                                    Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        pdialog.dismiss();
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
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("name", name.getText().toString().trim());
                                        params.put("phone", phone.getText().toString().trim());
                                        params.put("age", age.getText().toString().trim());
                                        params.put("sex", sex);
                                        params.put("blood", blood.getText().toString());
                                        params.put("city", city.getText().toString());
                                        return params;
                                    }


                                };
                                AppController.getInstance().addToRequestQueue(str);
                            }
                        }

                    });

                } else {

                    Snackbar.make(findViewById(android.R.id.content), "Permission denied",
                            Snackbar.LENGTH_LONG).show();

                }
            }
        }
    }
}
