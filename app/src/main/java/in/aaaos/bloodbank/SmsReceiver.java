package in.aaaos.bloodbank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by RACHIT GOYAL on 6/9/2018.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;
    SmsMessage smsMessage;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        assert pdus != null;
        for (Object pdu : pdus) {
            if (Build.VERSION.SDK_INT <= 22) {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            } else {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdu, data.getString("format"));
            }

            String sender = smsMessage.getDisplayOriginatingAddress();
            if (sender.contains("BLDDNR")) {

                String messageBody = smsMessage.getMessageBody();
                messageBody = messageBody.substring(0, messageBody.indexOf(' '));
                ;

                //Pass on the text to our listener.
                mListener.messageReceived(messageBody);
            }
            //You must check here if the sender is your provider and not another one with same text.

        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
