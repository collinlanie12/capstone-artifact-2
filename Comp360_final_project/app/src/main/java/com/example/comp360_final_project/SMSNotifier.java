package com.example.comp360_final_project;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

/**
 * SMSNotifier class to handle sending SMS notifications for event-related actions.
 */
public class SMSNotifier {

    private static final int SMS_PERMISSION_CODE = 100;
    private final Context context;

    /**
     * Constructor for SMSNotifier.
     *
     * @param context Application context
     */
    public SMSNotifier(Context context) {
        this.context = context;
    }

    /**
     * Check if SMS permission is granted.
     *
     * @return True if permission is granted, false otherwise.
     */
    public boolean isSMSPermissionGranted() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Send SMS notification if permission is granted.
     *
     * @param phoneNumber The phone number to send the message to.
     * @param message     The message content.
     */
    public void sendSMSNotification(String phoneNumber, String message) {
        if (isSMSPermissionGranted()) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(context, "SMS notification sent!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Failed to send SMS notification.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "SMS permission not granted.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Generate an event notification message.
     *
     * @param event The event for which the notification is being generated.
     * @return The message to send via SMS.
     */
    public String createEventNotificationMessage(Event event) {
        return "Event: " + event.getName() +
                " on " + event.getYear() + "-" + event.getMonth() + "-" + event.getDay() +
                " at " + String.format("%02d:%02d", event.getHour(), event.getMinute());
    }
}