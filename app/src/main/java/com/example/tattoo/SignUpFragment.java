package com.example.tattoo;

import androidx.fragment.app.Fragment;

import com.example.tattoo.databinding.FragmentSignupBinding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpFragment extends Fragment {

    private FragmentSignupBinding binding; // Update the binding variable name to match the new layout file

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false); // Update the binding initialization
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set click listener for the sign-up button
        binding.buttonSignUp.setOnClickListener(v -> {
            // Get user input (email and password)
            String email = binding.editTextEmailSignUp.getText().toString().trim();
            String password = binding.editTextPasswordSignUp.getText().toString().trim();

            // Perform sign-up using Firebase Authentication
            signUpUser(email, password);
        });
    }

    private void signUpUser(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Registration success", Toast.LENGTH_SHORT).show();
                    } else {
                        // Sign-up failed, display an error message or handle the error accordingly
                        Toast.makeText(requireContext(), "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
