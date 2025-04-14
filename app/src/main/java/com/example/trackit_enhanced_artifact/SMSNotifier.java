package com.example.trackit_enhanced_artifact;

/*
 * SMSNotifier.java
 *
 * Responsible for handling SMS notifications related to event reminders.
 * It checks permission status, sends SMS, and generates notification content.
 *
 * Author: Collin Lanier
 * Date: 2025-03-27
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;

public class SMSNotifier {

    private static final String TAG = "SMSNotifier"; // For logging
    private final Context context;

    public SMSNotifier(Context context) {
        this.context = context;
    }

    /**
     * Checks if SEND_SMS permission is granted.
     *
     * @return True if permission is granted, false otherwise.
     */
    public boolean isSMSPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                context, Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Sends an SMS notification with the given message to the specified phone number.
     * Logs success or failure and provides user feedback by Toast.
     *
     * @param phoneNumber The recipient's phone number.
     * @param message     The message content to send.
     */
    public void sendSMSNotification(String phoneNumber, String message) {
        if (phoneNumber == null || message == null) {
            Log.w(TAG, "Phone number or message is null. SMS not sent.");
            Toast.makeText(context, "SMS data is incomplete.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isSMSPermissionGranted()) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(context, "SMS notification sent!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SMS successfully sent to " + phoneNumber);
            } catch (Exception e) {
                Log.e(TAG, "Failed to send SMS to " + phoneNumber, e);
                Toast.makeText(context, "Failed to send SMS notification.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "SMS permission not granted. Unable to send SMS.");
            Toast.makeText(context, "SMS permission not granted.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a formatted SMS message string for the given event.
     *
     * @param event The event for which to create the message.
     * @return A formatted string containing event name, date, and time.
     */
    @SuppressLint("DefaultLocale")
    public String createEventNotificationMessage(Event event) {
        return "Event: " + event.getName() +
                " on " + event.getYear() + "-" + event.getMonth() + "-" + event.getDay() +
                " at " + String.format("%02d:%02d", event.getHour(), event.getMinute());
    }
}