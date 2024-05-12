package com.example.tattoo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tattoo.databinding.ActivityAppointmentsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AppointmentsActivity extends Activity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static int notificationId = 1;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private static final String channelId = "my_channel_id";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityAppointmentsBinding binding; // Update the binding variable name to match the new layout file

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentsBinding.inflate(getLayoutInflater()); // Update the binding initialization
        setContentView(binding.getRoot());

        // Call method to display user appointments
        displayUserAppointments();
    }

    private void displayUserAppointments() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String todayDateString = dateFormat.format(calendar.getTime());

            // Query appointments for the current user's email
            db.collection("appointments")
                    .whereEqualTo("email", currentUser.getEmail())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> userAppointments = new ArrayList<>();
                        List<String> appointmentIds = new ArrayList<>();
                        List<String> appointmentDates = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Get appointment details and add to the list
                            String name = documentSnapshot.getString("name");
                            String design = documentSnapshot.getString("design");
                            String date = documentSnapshot.getString("date");
                            String appointmentId = documentSnapshot.getId();
                            Appointment app = new Appointment(name, design, date, currentUser.getEmail());

                            userAppointments.add("Name: " + app.getName() + ", Design: " + app.getDesign() + ", Date: " + app.getDate());

                            appointmentIds.add(appointmentId);
                            appointmentDates.add(date);

                            if (date!=null && date.equals(todayDateString)) {
                                // Appointment is today, trigger a notification
                                sendNotification("Appointment Today", "Your appointment is scheduled for today");
                            }
                        }
                        StringBuilder appointmentsText = new StringBuilder();
                        for (int i = 0; i < userAppointments.size(); i++) {
                            String appointment = userAppointments.get(i);
                            String appointmentId = appointmentIds.get(i);
                            appointmentsText.append(appointment).append("\n");

                            // Create a button for deleting each appointment
                            TextView text  = new TextView(this);
                            text.setText(appointment);
                            Button deleteButton = new Button(this);
                            deleteButton.setText("Delete appointment");
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            deleteButton.setLayoutParams(params);
                            binding.layoutAppointmentDetails.addView(text);
                            binding.layoutAppointmentDetails.addView(deleteButton);

                            int finalI = i;
                            // Set click listener for each delete button
                            deleteButton.setOnClickListener(v -> {
                                // Show a confirmation dialog before deleting the appointment
                                new AlertDialog.Builder(this)
                                        .setTitle("Delete Appointment")
                                        .setMessage("Are you sure you want to delete this appointment?")
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                            // User confirmed deletion, delete the appointment from Firestore
                                            db.collection("appointments").document(appointmentId)
                                                    .delete()
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Appointment deleted successfully, update UI
                                                        userAppointments.remove(finalI);
                                                        appointmentIds.remove(finalI);
                                                        appointmentDates.remove(finalI);
                                                        binding.layoutAppointmentDetails.removeAllViews(); // Clear the layout
                                                        displayUserAppointments(); // Refresh UI

                                                        Toast.makeText(this, "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(this, "Failed to delete appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                            });
                        }

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to retrieve appointments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String title, String message) {
        notificationId++;
        // Create an explicit intent for the notification
        Intent intent = new Intent(this, AppointmentsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = getNotificationChannel();
        if (notificationChannel  != null &&  Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }


        // Create notification builder
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_stat_calendar_today)
                .setContentTitle(title)
                .setContentText(message)
                .setChannelId(channelId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Get notification manager
        NotificationManagerCompat notificationManager2 = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            return;
        }
        notificationManager2.notify(notificationId, notificationBuilder.build());

    }

    @Nullable
    private static NotificationChannel getNotificationChannel() {
        CharSequence channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(channelId,             channelName, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel.enableLights(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel.setLightColor(Color.RED);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel.enableVibration(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel.setVibrationPattern(new long[]{1000, 2000});
        }
        return notificationChannel;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // Check if permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send the notification
                sendNotification("Appointment Today", "Your appointment is scheduled for today");
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
