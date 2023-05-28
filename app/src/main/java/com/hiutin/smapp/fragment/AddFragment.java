package com.hiutin.smapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hiutin.smapp.R;
import com.hiutin.smapp.adapter.GalleryAdapter;
import com.hiutin.smapp.data.model.NotificationModel;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.data.repository.NotificationRepository;
import com.hiutin.smapp.data.repository.UserRepository;
import com.hiutin.smapp.databinding.FragmentAddBinding;
import com.hiutin.smapp.data.model.CommentModel;
import com.hiutin.smapp.dialog.LoadingDialog;
import com.hiutin.smapp.viewModel.AddFragmentViewModel;
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

public class AddFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private static FragmentAddBinding binding;
    private Uri imageUri = null;
    private Uri videoUri = null;
    private AddFragmentViewModel viewModel;
    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_PICK_VIDEO = 2;
    private LoadingDialog loadingDialog;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(requireContext());
        viewModel = new ViewModelProvider(requireActivity()).get(AddFragmentViewModel.class);
        viewModel.getCurrentUserLiveData().observe(getViewLifecycleOwner(), user -> {
            Glide.with(requireContext())
                    .load(user.getAvatar())
                    .placeholder(R.drawable.user)
                    .into(binding.imgUserAvatar);
            binding.tvUserName.setText(user.getName());
        });
        MediaController mediaController = new MediaController(requireContext());
        mediaController.setAnchorView(binding.vd);
        binding.vd.setMediaController(mediaController);
        binding.edtCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!binding.edtCaption.getText().toString().isEmpty()) {
                    binding.btnPost.setEnabled(true);
                    binding.btnPost.setBackgroundColor(Color.parseColor("#000000"));
                } else {
                    binding.btnPost.setEnabled(false);
                    binding.btnPost.setBackgroundColor(Color.parseColor("#cccccc"));
                }
                if (binding.edtCaption.getLineCount() > 3) {
                    binding.edtCaption.setTextSize(20);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnChoose.setOnClickListener(this::showPopUpMenu);

        binding.btnPost.setOnClickListener(v -> {
            loadingDialog.show();
            createPost();
        });
    }

    private void createPost() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if (imageUri != null) {
            StorageReference storageReference = storage.getReference().child("PostImages/" + System.currentTimeMillis());
            storageReference
                    .putFile(imageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(uri -> uploadData(uri.toString()));
                        }
                    });
            return;
        }

        if (videoUri != null) {
            StorageReference storageReference = storage.getReference().child("VideoImages/" + System.currentTimeMillis());
            storageReference
                    .putFile(videoUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(uri -> uploadData(uri.toString()));
                        }
                    });
            return;
        }
         uploadData(null);

    }

    private void uploadData(String url) {
        String id = FirebaseFirestore.getInstance()
                .collection("posts").document().getId();
        ArrayList<String> likes = new ArrayList<>();

        PostModel newPost = new PostModel();
        newPost.setId(id);
        newPost.setCaption(binding.edtCaption.getText().toString());
        newPost.setLikes(likes);
        if (imageUri != null)
            newPost.setPostImage(url);

        if (videoUri != null)
            newPost.setPostVideo(url);
        Timestamp timestamp = Timestamp.now();
        newPost.setTimestamp(timestamp.toDate());
        newPost.setUid(FirebaseAuth.getInstance().getUid());
        viewModel.addPost(newPost);

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("UserRepository", "Error fetching users" + e.getMessage());
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        List<String> followers = (ArrayList<String>) snapshot.get("followers");
                        NotificationRepository notificationRepository = new NotificationRepository();
                        NotificationModel model = new NotificationModel(FirebaseAuth.getInstance().getUid(),"đăng bài viết mới: "+newPost.getCaption(),id,followers,timestamp);
                        notificationRepository.addNotification(getContext(),model,"đăng bài viết mới: "+newPost.getCaption());
                        //notificationRepository.sendMultipleNotification(getContext());
                    }
                });
        resetInput();
        loadingDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                // Xử lý đường dẫn hình ảnh được chọn ở đây
                binding.vdLayout.setVisibility(View.GONE);
                binding.img.setImageURI(selectedImageUri);
                binding.img.setVisibility(View.VISIBLE);
                imageUri = selectedImageUri;
            } else if (requestCode == REQUEST_PICK_VIDEO && data != null) {
                Uri selectedVideoUri = data.getData();
                // Xử lý đường dẫn video được chọn ở đây
                binding.img.setVisibility(View.GONE);
                binding.vd.setVideoURI(selectedVideoUri);
                binding.vdLayout.setVisibility(View.VISIBLE);
                videoUri = selectedVideoUri;
//                binding.vd.start();
            }
        }
    }

    public static void resetInput() {
        binding.edtCaption.setText("");
        binding.img.setImageURI(null);
        binding.img.setVisibility(View.GONE);
        binding.vd.setVideoURI(null);
        binding.vdLayout.setVisibility(View.GONE);
    }

    private void showPopUpMenu(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.add_choose_image_or_video);
        popup.show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.choose_image:
                Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imageIntent, REQUEST_PICK_IMAGE);
                return true;
            case R.id.choose_video:
                Intent videoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(videoIntent, REQUEST_PICK_VIDEO);
                return true;
            default:
                return false;
        }
    }
}