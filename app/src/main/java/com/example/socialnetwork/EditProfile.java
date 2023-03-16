package com.example.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialnetwork.Modal.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    CircleImageView profileImage;
    TextView profileName;
    TextInputEditText newNameE,newEmailE,newPasswordE;
    DatabaseReference reference;
    FirebaseUser cuser;
    Button editBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        init();
        setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                profileName.setText(user.getUsername());


                if(user.getImgUrl().equals("default")){
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext())
                            .load(user.getImgUrl())
                            .into(profileImage);
                }


                newNameE.setText(user.getUsername());
                newEmailE.setText(user.getEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editBtn.setOnClickListener(v -> {
            String newName = newNameE.getText().toString();
            String newEmail = newEmailE.getText().toString();
            String newPassword = newPasswordE.getText().toString();

            if(!newName.isEmpty() && newName.length() >= 3){
                HashMap<String,Object> map = new HashMap<>();
                map.put("username",newName);
                reference.updateChildren(map);
            }else
                newNameE.setError("Name must have min 3 letters");

            if(!newEmail.isEmpty()){
                cuser.updateEmail(newEmail).addOnCompleteListener(e -> {

                        if(e.isSuccessful()){

                            HashMap<String,Object> map = new HashMap<>();
                            map.put("email",newEmail);
                            reference.updateChildren(map);

                        }
                });
            }else
                newEmailE.setError("Failed email address");

            if(newPassword.length() >= 6){
                cuser.updatePassword(newPassword).addOnCompleteListener(task ->
                        Toast.makeText(this, "Successful changed password", Toast.LENGTH_SHORT).show()).
                        addOnFailureListener(e -> Toast.makeText(this, "An error has occurred ", Toast.LENGTH_SHORT).show());
            }

            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        });


    }



    private void init(){
        profileName = findViewById(R.id.profile_name);
        profileImage = findViewById(R.id.profile_image);
        newEmailE = findViewById(R.id.et_email);
        newPasswordE = findViewById(R.id.et_password);
        newNameE = findViewById(R.id.et_name);
        cuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("myUsers").child(cuser.getUid());
        editBtn = findViewById(R.id.btn_edit);

    }
    private void checkStatus(String status){
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",status);
        reference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkStatus("offline");
    }

}
