package com.hiutin.smapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hiutin.smapp.FragmentReplaceActivity;
import com.hiutin.smapp.MainActivity;
import com.hiutin.smapp.R;
import com.hiutin.smapp.databinding.FragmentLoginBinding;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private AlertDialog alertDialog;
    public static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle("Loading")
                .setMessage("Loading...")
                .create();

        binding.tvForgotPassword.setOnClickListener(v -> {
            ((FragmentReplaceActivity)requireActivity()).setFragment(new ForgotPasswordFragment());
        });
        binding.tvSignUp.setOnClickListener(v -> {
            ((FragmentReplaceActivity) getActivity()).setFragment(new CreateAccountFragment());
        });
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString();
            String password = binding.edtPassword.getText().toString();
            if (email.isEmpty()) {
                binding.edtEmail.setError("Please enter your email!");
                return;
            }

            if (!binding.edtEmail.getText().toString().matches(EMAIL_REGEX)) {
                binding.edtEmail.setError("Please enter a valid email!");
                return;
            }

            if (password.isEmpty()) {
                binding.edtPassword.setError("Please enter your password!");
                return;
            }

            if (password.length() < 6) {
                binding.edtPassword.setError("Password must have at least 6 characters!");
                return;
            }
            alertDialog.show();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    alertDialog.dismiss();
                    if (!auth.getCurrentUser().isEmailVerified()) {
                        Snackbar.make(binding.getRoot(), "Please verify your email", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Snackbar.make(binding.getRoot(), "Login success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                } else {
                    alertDialog.dismiss();
                    Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnGoogleLogin.setOnClickListener(v -> {
            signInWithGoogle();
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        alertDialog.show();
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
                Log.e("TAG", "onActivityResult: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(account.getId());
                docRef.get().addOnCompleteListener(task12 -> {
                    if (task12.isSuccessful()) {
                        DocumentSnapshot document = task12.getResult();
                        if (document.exists()) {
                            alertDialog.dismiss();
                            Snackbar.make(binding.getRoot(), "Log in success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        } else {
                            Map<String, Object> user = new HashMap<>();
                            user.put("uid", auth.getUid());
                            user.put("name", account.getDisplayName());
                            user.put("email", account.getEmail());
                            user.put("avatar", String.valueOf(account.getPhotoUrl()));
                            FirebaseFirestore.getInstance().collection("users").document(auth.getUid())
                                    .set(user)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            alertDialog.dismiss();
                                            Snackbar.make(binding.getRoot(), "Log in success", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getContext(), MainActivity.class));
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getContext(), "Error2: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task12.getException());
                    }
                });

            }
        });
    }
}






















