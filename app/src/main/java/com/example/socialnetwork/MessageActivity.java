package com.example.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialnetwork.Adapter.MessageAdapter;
import com.example.socialnetwork.Modal.Chat;
import com.example.socialnetwork.Modal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MessageActivity extends AppCompatActivity {
    RecyclerView messageRecycle;
    EditText messageE;
    ImageButton send;
    TextView name,status;
    ImageView image;
    Toolbar toolbar;
    FirebaseUser user;
    DatabaseReference reference;
    Intent intent;
    MessageAdapter adapter;
    List<Chat> chatList ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        name = findViewById(R.id.chatName);
        status = findViewById(R.id.status);
        image = findViewById(R.id.chatImage);
        toolbar = findViewById(R.id.toolbar);
        messageRecycle = findViewById(R.id.messageR);
        send = findViewById(R.id.sendBtn);
        messageE = findViewById(R.id.messageText);
        intent = getIntent();
        String id = intent.getStringExtra("id");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        messageRecycle.setLayoutManager(linearLayoutManager);

        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        send.setOnClickListener(v -> {
            String msg = messageE.getText().toString();
            if(msg.equals(""))
                return;

            sendMessage(user.getUid(),id,msg);

            messageE.setText("");
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("myUsers").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);
                name.setText(user1.getUsername());
                if(user1.getImgUrl().equals("default")){
                    image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext())
                            .load(user1.getImgUrl())
                            .into(image);
                }

                status.setText(user1.getStatus());

                readData(user.getUid(),id, user1.getImgUrl());

                toolbar.setNavigationOnClickListener(v -> finish());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void sendMessage(String sender,String receiver,String message) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();

        Chat  chat= new Chat(message,receiver,sender);
        reference1.child("Chats").push().setValue(chat);

    }
    private void readData(String myId, String id, String imgUrl) {
        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    if(chat.getReceiver().equals(myId) && chat.getSender().equals(id) ||
                            chat.getReceiver().equals(id) && chat.getSender().equals(myId)){
                        chatList.add(chat);
                    }

                    adapter = new MessageAdapter(MessageActivity.this,chatList,imgUrl);
                    messageRecycle.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkStatus(String status){
        reference = FirebaseDatabase.getInstance().getReference("myUsers").child(user.getUid());
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",status);
        reference.updateChildren(map);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkStatus("offline");
    }

}