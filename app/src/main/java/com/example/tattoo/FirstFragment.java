package com.example.tattoo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tattoo.databinding.FragmentFirstBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.Toast;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
    public void handleLogout() {
        FirebaseAuth.getInstance().signOut(); // Sign out the user
        // Go to login
        Toast.makeText(requireContext(), "Logout successful ", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).navigate(R.id.action_FirstFragment_to_LoginFragment);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is logged in, change text and visibility
            binding.textviewHeader.setText("Welcome back, " + currentUser.getEmail());
            binding.textviewAboveButtons.setText("You're already logged in.");
            binding.buttonLogin.setVisibility(View.GONE); // Hide login button
            binding.buttonSignup.setVisibility(View.GONE); // Hide signup button
            binding.buttonNewAppointment.setVisibility(View.VISIBLE);
            binding.buttonAppointments.setVisibility(View.VISIBLE);
            binding.buttonLogout.setVisibility(View.VISIBLE);
            binding.buttonNewAppointment.setOnClickListener(v ->
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_appointmentActivity)
            );
            binding.buttonAppointments.setOnClickListener(v ->
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_appointmentsActivity)
            );
            binding.buttonLogout.setOnClickListener(v ->
                    handleLogout()
            );
        } else {
            // User is not logged in, set default text and visibility
            binding.textviewHeader.setText("Welcome to Tattoo app");
            binding.textviewAboveButtons.setText("Log in first to make appointments");
            binding.buttonLogin.setVisibility(View.VISIBLE); // Show login button
            binding.buttonSignup.setVisibility(View.VISIBLE); // Show signup button
            binding.buttonNewAppointment.setVisibility(View.GONE);
            binding.buttonAppointments.setVisibility(View.GONE);
            binding.buttonLogout.setVisibility(View.GONE);
            binding.buttonLogin.setOnClickListener(v ->
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_LoginFragment)
            );

            binding.buttonSignup.setOnClickListener(v ->
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_signUpFragment)
            );
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}