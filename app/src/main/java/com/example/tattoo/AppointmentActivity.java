package com.example.tattoo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tattoo.databinding.ActivityNewappointmentBinding;
import com.example.tattoo.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AppointmentActivity extends Activity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityNewappointmentBinding binding; // Update the binding variable name to match the new layout file
    private Button buttonPickDate;
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewappointmentBinding.inflate(getLayoutInflater()); // Update the binding initialization
        setContentView(binding.getRoot());
        buttonPickDate = findViewById(R.id.button_pick_date);
        calendar = Calendar.getInstance();

        buttonPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize date picker dialog and set listener
                DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Update the calendar instance with the selected date
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                // Update the button text to the selected date
                                updateButtonText();
                            }
                        },
                        // Set the initial date to the current date
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                // Show the date picker dialog
                datePickerDialog.show();
            }
        });

        // Set click listener for the submit buttonc
        binding.buttonSubmit.setOnClickListener(v -> {
            String name = binding.editTextName.getText().toString().trim();
            String design = binding.editTextDesign.getText().toString().trim();
            String date = binding.buttonPickDate.getText().toString().trim();

            // Perform appointment submission
            submitAppointment(name, design, date);
        });
    }
    private void updateButtonText() {
        buttonPickDate.setText(android.text.format.DateFormat.format("MM/dd/yyyy", calendar.getTime()));
    }
    private void submitAppointment(String name, String design, String date) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!= null) {
        Appointment appointment = new Appointment(name, design, date, currentUser.getEmail());

        // Add the appointment to Firestore
        db.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Appointment submitted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        } else {
            Toast.makeText(this, "Please log in first ", Toast.LENGTH_SHORT).show();

        }

    }
}
