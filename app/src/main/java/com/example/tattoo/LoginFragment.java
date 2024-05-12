package com.example.tattoo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.tattoo.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FragmentLoginBinding binding; // Update the binding variable name to match the new layout file

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false); // Update the binding initialization
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.editTextEmailLogin.getText().toString().trim();
            String password = binding.editTextPasswordLogin.getText().toString().trim();

            // Perform login using Firebase Authentication
            loginUser(email, password);
        });

        binding.textViewSignUp.setOnClickListener(v -> {
            // Navigate to the sign-up fragment when the text view is clicked
            NavHostFragment.findNavController(LoginFragment.this)
                  .navigate(R.id.action_LoginFragment_to_signUpFragment);
        });
    }

    private void loginUser(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Login success", Toast.LENGTH_SHORT).show();
                    } else {
                        // Login failed, display an error message or handle the error accordingly
                        Toast.makeText(requireContext(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
