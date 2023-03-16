package com.example.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialnetwork.Modal.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText nameE,emailE,passwordE,repeatP;
    Button registerBtn;
    TextView toLogin;
    DatabaseReference db;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Register");

        init();

        toLogin.setOnClickListener(v -> {

            Intent i  = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        });

        registerBtn.setOnClickListener(v -> {
             String username = nameE.getText().toString();
             String email = emailE.getText().toString();
             String password = passwordE.getText().toString();
             String passwordR = repeatP.getText().toString();

             if(username.isEmpty() || email.isEmpty() || password.isEmpty() || !password.equals(passwordR)){
                 Toast.makeText(this, "Enter a value in the fields or your passwords do "
                         + "not match ", Toast.LENGTH_SHORT).show();
                return;
             }

             registerNow(username,email,password);

        });
    }

    private void registerNow(final String username, String email, String password) {

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Failed,Error", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String id = auth.getUid();

                    db = FirebaseDatabase.getInstance().getReference("myUsers").child(id)     ;

                    User user1 = new User(username,id,"default",email,"online");

                    db.setValue(user1).addOnCompleteListener(task1 -> {
                        if(!task1.isSuccessful())
                            return;
                        Toast.makeText(this, "Success Register", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);

                    }).addOnFailureListener(e -> Toast.makeText(this, "Failed Register", Toast.LENGTH_SHORT).show());

                });


    }

    private void init() {
        nameE = findViewById(R.id.Rname);
        emailE = findViewById(R.id.Remail);
        passwordE = findViewById(R.id.Rpassword);
        registerBtn = findViewById(R.id.registerB);
        toLogin = findViewById(R.id.toLogin);
        repeatP = findViewById(R.id.rpassword);
    }
}