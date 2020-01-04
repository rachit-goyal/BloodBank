package in.aaaos.bloodbank;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by RACHIT GOYAL on 12/4/2017.
 */

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<Values> singleUser;

    public MyRecyclerViewAdapter(Context context, ArrayList<Values> singleUser) {

        this.context = context;
        this.singleUser = singleUser;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, blood, city;


        public ViewHolder(View v) {

            super(v);
            name = (TextView) v.findViewById(R.id.name);
            blood = (TextView) v.findViewById(R.id.blood);
            city = (TextView) v.findViewById(R.id.city);
        }
    }

    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder vh = new ViewHolder(view1);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.name.setText(singleUser.get(position).getName());
        holder.city.setText(singleUser.get(position).getCity());
        holder.blood.setText(singleUser.get(position).getBlood());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    Intent i = new Intent(context, selection.class);
                    i.putExtra("phone", singleUser.get(position).getPhone());
                    i.putExtra("name", singleUser.get(position).getName());
                    i.putExtra("blood", singleUser.get(position).getBlood());
                    i.putExtra("city", singleUser.get(position).getCity());
                    context.startActivity(i);
                }
                else {
                    Toast.makeText(context,"No Network",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public int getItemCount(){

        return singleUser.size();
    }


}
