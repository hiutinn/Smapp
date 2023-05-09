package com.hiutin.smapp.fragment;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hiutin.smapp.adapter.GalleryAdapter;
import com.hiutin.smapp.databinding.FragmentAddBinding;
import com.hiutin.smapp.data.model.CommentModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFragment extends Fragment {
    private FragmentAddBinding binding;
    private GalleryAdapter adapter;
    private List<Uri> images;
    private Uri mImageUri;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setHasFixedSize(true);
        images = new ArrayList<>();
        adapter = new GalleryAdapter(requireContext(), images);
        binding.recyclerView.setAdapter(adapter);
        getImages();
        clickListener();
    }


    private void getImages() {
        getActivity().runOnUiThread(() -> {
            Dexter.withContext(requireContext())
                    .withPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                File directory = new File(Environment.getExternalStorageDirectory().toString() + "/DCIM");
                                if (directory.exists()) {
                                    File[] subDirectories = directory.listFiles();

                                    assert subDirectories != null;
                                    for (File dereFile : subDirectories) {
                                        File[] files = dereFile.listFiles();
                                        assert files != null;
                                        for (File file : files) {
                                            if (file.getAbsolutePath().endsWith(".jpg") || file.getAbsolutePath().endsWith(".png")) {
                                                images.add(Uri.fromFile(file));
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                                File pictureDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures");
                                if (pictureDirectory.exists()) {
                                    File[] pics = pictureDirectory.listFiles();

                                    assert pics != null;
                                    for (File file : pics) {
                                        if (file.getAbsolutePath().endsWith(".jpg") || file.getAbsolutePath().endsWith(".png")) {
                                            images.add(Uri.fromFile(file));
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                    }).check();
        });
    }

    private void clickListener() {
        adapter.sendImage(imageUri -> {
            mImageUri = imageUri;
            Glide.with(requireContext())
                    .load(mImageUri)
                    .into(binding.ivPostImage);
            binding.ivPostImage.setVisibility(View.VISIBLE);
            binding.btnNext.setVisibility(View.VISIBLE);
        });

        binding.btnNext.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnNext.setVisibility(View.GONE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("PostImages/" + System.currentTimeMillis());

            storageReference.putFile(mImageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> uploadData(uri.toString()));
                }
            });
        });
    }

    private void uploadData(String imageURL) {
        CollectionReference collectionReference
                = FirebaseFirestore.getInstance()
                .collection("posts");
        String id = collectionReference.document().getId();
        ArrayList<String> likes = new ArrayList<>();
        ArrayList<CommentModel> comments = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("uid", FirebaseAuth.getInstance().getUid());
        map.put("postImage", imageURL);
        map.put("caption", binding.edtDescription.getText().toString());
        map.put("timestamp", FieldValue.serverTimestamp());
        map.put("likes", likes);
        map.put("comments", comments);

        collectionReference.document(id).set(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Upload post successfully", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPostImage.setVisibility(View.GONE);
                        binding.edtDescription.setText("");
                        binding.btnNext.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}