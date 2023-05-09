package com.hiutin.smapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hiutin.smapp.R;
import com.hiutin.smapp.databinding.ImageItemBinding;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    Context context;
    List<Uri> images;
    ISendImage onSend;

    public GalleryAdapter(Context context, List<Uri> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        Uri imageUri = images.get(position);
//        holder.binding.imageView.setImageURI(image.getImageUri());
        Glide.with(context)
                .load(imageUri)
                .into(holder.binding.imageView);
        holder.itemView.setOnClickListener(v -> chooseImage(imageUri));
    }

    private void chooseImage(Uri imageUri) {
        onSend.onSend(imageUri);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        private ImageItemBinding binding;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ImageItemBinding.bind(itemView);
        }
    }

    public interface ISendImage {
        void onSend(Uri imageUri);
    }

    public void sendImage(ISendImage onSend) {
        this.onSend = onSend;
    }
}
