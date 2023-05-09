package com.hiutin.smapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hiutin.smapp.FragmentReplaceActivity;
import com.hiutin.smapp.MainActivity;
import com.hiutin.smapp.R;
import com.hiutin.smapp.databinding.FragmentCreateAccountBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountFragment extends Fragment {
    private FragmentCreateAccountBinding binding;
    private FirebaseAuth auth;

    public static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private AlertDialog alertDialog;

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Loading")
                .setMessage("Loading...")
                .setCancelable(false)
                .create();
        binding.tvLogin.setOnClickListener(v -> {
            ((FragmentReplaceActivity) getActivity()).setFragment(new LoginFragment());
        });

        binding.btnSignUp.setOnClickListener(v -> {
            if (binding.edtName.getText().toString().isEmpty()) {
                binding.edtName.setError("Please enter your name!");
                return;
            }

            if (binding.edtEmail.getText().toString().isEmpty()) {
                binding.edtEmail.setError("Please enter your email!");
                return;
            }

            if (!binding.edtEmail.getText().toString().matches(EMAIL_REGEX)) {
                binding.edtEmail.setError("Please enter a valid email!");
                return;
            }

            if (binding.edtPassword.getText().toString().isEmpty()) {
                binding.edtPassword.setError("Please enter your password!");
                return;
            }

            if (binding.edtPassword.getText().toString().length() < 6) {
                binding.edtPassword.setError("Password must have at least 6 characters!");
                return;
            }

            if (binding.edtConfirmPassword.getText().toString().isEmpty()) {
                binding.edtConfirmPassword.setError("Please confirm your password!");
                return;
            }

            if (!binding.edtConfirmPassword.getText().toString().equals(binding.edtPassword.getText().toString())) {
                binding.edtConfirmPassword.setError("Difference!");
                return;
            }

            createAccount(binding.edtName.getText().toString(),
                    binding.edtEmail.getText().toString(),
                    binding.edtPassword.getText().toString());

        });
    }

    private void createAccount(String name, String email, String password) {
        alertDialog.show();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                currentUser.sendEmailVerification().addOnCompleteListener(task12 -> {
                    if (task12.isSuccessful()) {
                        Snackbar.make(binding.getRoot(), "Please check your email to verified your email!", Toast.LENGTH_LONG).show();
                    }
                });
                Map<String, Object> user = new HashMap<>();
                user.put("uid", auth.getUid());
                user.put("name", name);
                user.put("email", email);
                user.put("avatar", "");
                user.put("following", new ArrayList<String>());
                user.put("followers", new ArrayList<String>());
                user.put("status", "");
                db.collection("users").document(auth.getUid())
                        .set(user)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                auth.signOut();
                                alertDialog.dismiss();
                                Snackbar.make(binding.getRoot(), "Sign up success", Toast.LENGTH_SHORT).show();
                                auth.signOut();
                                ((FragmentReplaceActivity) getActivity()).setFragment(new LoginFragment());
                            } else {
                                Toast.makeText(getContext(), "Error2: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                alertDialog.dismiss();
                Toast.makeText(getContext(), "Error1: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}