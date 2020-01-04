package in.aaaos.bloodbank;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class AboutUs extends AppCompatActivity {
    Toolbar toolbar;
    TextView ter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ter=(TextView)findViewById(R.id.abouttext);
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
        ter.setText("We have developed this android based app to establish a connection between requester and" +
                " donor anytime anywhere.The objective of this application is to provide the information about the requested blood and number of available donors around those localities" +
                " It assist the requester to see no of available donor and allow requester to make call,share details of donor with another person and message donor in case of emergency.");
    }
}
