package com.hiutin.smapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.hiutin.smapp.FragmentReplaceActivity;
import com.hiutin.smapp.R;
import com.hiutin.smapp.databinding.FragmentForgotPasswordBinding;
import com.hiutin.smapp.databinding.FragmentLoginBinding;


public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    public static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private FirebaseAuth auth;
    private AlertDialog dialog;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        dialog = new AlertDialog.Builder(requireActivity())
                .setTitle("Loading")
                .setMessage("Loading...")
                .create();
        binding.btnRecover.setOnClickListener(v -> {
            dialog.show();
            if (!binding.edtEmail.getText().toString().matches(EMAIL_REGEX)) {
                binding.edtEmail.setError("Please enter a valid email!");
                return;
            }
            auth.sendPasswordResetEmail(binding.edtEmail.getText().toString())
                    .addOnCompleteListener(task -> {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Password reset email send successfully", Toast.LENGTH_SHORT).show();
                            binding.edtEmail.setText("");
                        } else {
                            Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        binding.tvLogin.setOnClickListener(v -> {
            ((FragmentReplaceActivity) getActivity()).setFragment(new LoginFragment());
        });
    }
}