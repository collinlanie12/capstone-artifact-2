package com.example.comp360_final_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * BroadcastReceiver to handle sending SMS notifications for event reminders.
 */
public class SMSNotificationReceiver extends BroadcastReceiver {

    /**
     * Called when the BroadcastReceiver is receiving an Intent broadcast.
     * This method sends an SMS reminder for an event.
     *
     * @param context the application context
     * @param intent  the intent containing the phone number and message
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract phone number and message from the intent
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String message = intent.getStringExtra("message");

        // Send the SMS if both phone number and message are available
        if (phoneNumber != null && message != null) {
            sendSms(context, phoneNumber, message);
        } else {
            Toast.makeText(context, "Invalid phone number or message content.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method to send an SMS message.
     *
     * @param context     the application context
     * @param phoneNumber the recipient's phone number
     * @param message     the message to send
     */
    private void sendSms(Context context, String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(context, "Event reminder sent via SMS!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}