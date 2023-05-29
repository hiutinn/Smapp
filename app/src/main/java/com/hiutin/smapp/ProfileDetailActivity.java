package com.hiutin.smapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hiutin.smapp.data.model.UserModel;

import com.hiutin.smapp.databinding.ActivityProfileDetailBinding;
import com.hiutin.smapp.viewModel.ProfileFragmentViewModel;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;

public class ProfileDetailActivity extends AppCompatActivity {
    private ActivityProfileDetailBinding binding;
    private static final int REQUEST_PICK_IMAGE = 1;
    private Uri imageUri = null;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        binding = ActivityProfileDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Loading")
                .setMessage("Loading...")
                .create();
        FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    UserModel user = task.getResult().toObject(UserModel.class);
                    Glide.with(getApplication())
                            .load(user.getAvatar())
                            .into(binding.profileImage);
                    binding.edtName.setText(user.getName());
                });


        binding.frameImg.setOnClickListener(view -> {
            Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(imageIntent, REQUEST_PICK_IMAGE);
        });

        binding.btnSave.setOnClickListener(view -> {
            alertDialog.show();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            if (imageUri != null) {
                Log.e("link",imageUri+"");
                StorageReference storageReference = storage.getReference().child("PostImages/" + System.currentTimeMillis());
                storageReference
                        .putFile(imageUri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("avatar", uri);
                                            updates.put("name", binding.edtName.getText().toString());
                                            FirebaseFirestore.getInstance().collection("users")
                                                    .document(FirebaseAuth.getInstance().getUid())
                                                    .update(updates)
                                                    .addOnCompleteListener(task1 -> {
                                                        alertDialog.dismiss();
                                                        Intent intent = new Intent(this, MainActivity.class);
                                                        startActivity(intent);
                                                    });
                                        });
                            }
                        });
            }else {
                FirebaseFirestore.getInstance().collection("users")
                        .document(FirebaseAuth.getInstance().getUid())
                        .update("name",binding.edtName.getText().toString())
                        .addOnCompleteListener(task1 -> {
                            alertDialog.dismiss();
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        });
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                // Xử lý đường dẫn hình ảnh được chọn ở đây
                binding.profileImage.setImageURI(selectedImageUri);
                imageUri = selectedImageUri;
            }
        }
    }
}