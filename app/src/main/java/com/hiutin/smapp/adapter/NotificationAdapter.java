package com.hiutin.smapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hiutin.smapp.R;
import com.hiutin.smapp.data.model.NotificationModel;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.databinding.NotificationItemBinding;
import com.hiutin.smapp.utils.TimeHelper;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.notificationViewHolder> {
    private Context context;
    private List<NotificationModel> notificationModels;
    private IOnItemClick iOnItemClick;

    public NotificationAdapter(Context context, List<NotificationModel> notificationModels) {
        this.context = context;
        this.notificationModels = notificationModels;
    }

    @NonNull
    @Override
    public notificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new notificationViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull notificationViewHolder holder, int position) {
        NotificationModel notificationModel = notificationModels.get(position);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(notificationModel.getUidA())
                        .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    Glide.with(context)
                                            .load(documentSnapshot.get("avatar"))
                                            .placeholder(R.drawable.user)
                                            .into(holder.binding.imageUser);
                                    String chuoi = documentSnapshot.get("name") + " " + notificationModel.getContent();
                                    String name = documentSnapshot.get("name").toString();
                                    spanable(chuoi,holder.binding.tvContent,name.length());
                                    holder.binding.tvTime.setText(TimeHelper.getTime(notificationModel.getTimestamp().toDate()));
                                });
        holder.itemView.setOnClickListener(v ->{
            iOnItemClick.onItemClick(notificationModel.getIdPost());
        });
    }
    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public class notificationViewHolder extends RecyclerView.ViewHolder {
        private NotificationItemBinding binding;
        public notificationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NotificationItemBinding.bind(itemView);
        }
    }
    public void spanable(String chuoi, TextView textView ,int index){
        Spannable spannable = new SpannableString(chuoi);

        StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
        spannable.setSpan(boldStyle, 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan(30), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }

    public interface IOnItemClick {
        void onItemClick(String uid);
    }

    public void onItemClick(IOnItemClick iOnItemClick) {
        this.iOnItemClick = iOnItemClick;
    }

}
