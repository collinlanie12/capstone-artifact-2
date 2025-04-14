package com.example.trackit_enhanced_artifact;

/*
 * SMSNotificationReceiver.java
 *
 * BroadcastReceiver that handles sending SMS notifications for event reminders.
 * It listens for broadcast Intents and triggers SMS delivery using SmsManager.
 *
 * Author: Collin Lanier
 * Date: 2025-03-27
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SMSNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSNotificationReceiver"; // Tag for logging

    /**
     * Triggered when a broadcast matching this receiver's intent filter is received.
     * It tries to send an SMS message with the event reminder content.
     *
     * @param context the application context
     * @param intent  the received intent containing SMS data
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String message = intent.getStringExtra("message");

        if (phoneNumber != null && message != null) {
            sendSms(context, phoneNumber, message);
        } else {
            Log.w(TAG, "Received null phone number or message content.");
            Toast.makeText(context, "Invalid phone number or message content.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sends an SMS message using the Android SmsManager.
     *
     * @param context     the application context
     * @param phoneNumber the destination phone number
     * @param message     the message content
     */
    private void sendSms(Context context, String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(context, "Event reminder sent via SMS!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "SMS sent successfully to " + phoneNumber);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS to " + phoneNumber, e);
            Toast.makeText(context, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
        }
    }
}