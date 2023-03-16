package com.example.socialnetwork.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialnetwork.MessageActivity;
import com.example.socialnetwork.Modal.User;
import com.example.socialnetwork.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    ArrayList<User> users;
    Context context;

    public ChatAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }


    @NonNull
    @Override
    public ChatAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_user_list,parent,false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(users.get(position).getUsername());

        if(users.get(position).getImgUrl().equals("default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(context)
                    .load(users.get(position).getImgUrl())
                    .into(holder.imageView);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent i  = new Intent(context, MessageActivity.class);
            i.putExtra("id",users.get(position).getId());
            context.startActivity(i);
        });

        if (users.get(position).getStatus().equals("online")){
            holder.statusOff.setVisibility(View.GONE);
            holder.statusOn.setVisibility(View.VISIBLE);
        }else{
            holder.statusOff.setVisibility(View.VISIBLE);
            holder.statusOn.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {
        ImageView imageView,statusOff,statusOn;
        TextView name;
        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profileImg);
            name = itemView.findViewById(R.id.profileName);
            statusOn = itemView.findViewById(R.id.status);
            statusOff = itemView.findViewById(R.id.statusOff);
        }
    }
}

