package in.aaaos.bloodbank;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

public class selection extends AppCompatActivity {
    LinearLayout call, msg, share;
    String phone, name, blood, city;
    private static final int REQUEST_PHONE_CALL = 1;
    private static final int PERMISSION_REQUEST = 100;
    String SENT = "SMS_SENT";

    private ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_selection);
        call = (LinearLayout) findViewById(R.id.linearcall);
        msg = (LinearLayout) findViewById(R.id.linearmsg);
        share = (LinearLayout) findViewById(R.id.linearshare);
        phone = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
        blood = getIntent().getStringExtra("blood");
        city = getIntent().getStringExtra("city");
       
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(selection.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                            Snackbar.make(findViewById(android.R.id.content), "You need to grant CALL PHONE permission to make call",
                                    Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                                    }
                                }
                            }).show();
                        } else {
                            ActivityCompat.requestPermissions(selection.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                        }
                    } else {
                        startActivity(intent);
                    }
                } else {
                    startActivity(intent);
                }

            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.SEND_SMS)) {
                            Snackbar.make(findViewById(android.R.id.content), "You need to grant SEND SMS permission to send sms",
                                    Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST);
                                    }
                                }
                            }).show();
                        } else {
                            requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, PERMISSION_REQUEST);
                        }
                    } else {
                        sendSMS();
                    }
                } else {
                    sendSMS();
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Name:" + name + "\n" + "Phone no.:" + phone + "\n" + "Blood Group:" + blood + "\n" + "City:" + city;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

            }
        });

    }

    private void sendSMS() {
        pdialog = new ProgressDialog(selection.this);
        pdialog.setMessage("Please Wait...");
        pdialog.setCancelable(false);
        pdialog.show();
        PendingIntent sentPI = PendingIntent.getBroadcast(selection.this, 0, new Intent(SENT), 0);
        String SENT = "SMS_SENT";
        selection.this.registerReceiver(
                new BroadcastReceiver()
                {
                    @Override
                    public void onReceive(Context arg0, Intent arg1)

                    {
                        switch(getResultCode())
                        {

                            case Activity.RESULT_OK:
                                Toast.makeText(selection.this,"Message Sent",Toast.LENGTH_SHORT).show();
                                pdialog.dismiss();
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Toast.makeText(selection.this,"Message not sent",Toast.LENGTH_SHORT).show();
                                pdialog.dismiss();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Toast.makeText(selection.this,"Message not sent",Toast.LENGTH_SHORT).show();
                                pdialog.dismiss();

                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Toast.makeText(selection.this,"Message not sent",Toast.LENGTH_SHORT).show();
                                pdialog.dismiss();

                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Toast.makeText(selection.this,"Message not sent",Toast.LENGTH_SHORT).show();
                                pdialog.dismiss();

                                break;
                        }
                    }
                }, new IntentFilter(SENT));
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null,"Could you please help me.I urgently need blood."+"\n"+"My phone no. is"+phone+"\n"+"I need"+blood+"blood",sentPI, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(findViewById(android.R.id.content), "Permission Granted",
                            Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                }
                else
                {
                    Snackbar.make(findViewById(android.R.id.content), "Permission denied",
                            Snackbar.LENGTH_LONG).show();
                }
                return;
            }
            case PERMISSION_REQUEST:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Snackbar.make(findViewById(android.R.id.content), "Permission Granted",
                            Snackbar.LENGTH_LONG).show();
                    sendSMS();

                } else {

                    Snackbar.make(findViewById(android.R.id.content), "Permission denied",
                            Snackbar.LENGTH_LONG).show();

                }
            }
        }
    }
}
