package in.aaaos.bloodbank;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Terms extends AppCompatActivity {
    Toolbar toolbar;
    TextView ter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        ter=(TextView)findViewById(R.id.ter);
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
        ter.setText("1.Information given to this app is publicly available to each and every person using this app.\n" +
                "Hence,you don't have any issue if any person will call you any time.\n\n" +
                "2.Before donating your blood kindly consult doctor or registered blood bank.\n\n" +
                "3.AAA ONLINE SERVICE is not investigating any donor of this app.So donor will do full investigation before donating your blood.AAA ONLINE SERVICES is not responsible for any damage.\n");
    }
}
