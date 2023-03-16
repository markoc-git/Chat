package com.example.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextView toRegister;
    TextInputEditText emailE,passwordE;
    Button loginBtn;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();


        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In");

        auth = FirebaseAuth.getInstance();

        byId();

        toRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(v -> {
            String email = emailE.getText().toString();
            String password = passwordE.getText().toString();

            if(email.isEmpty() || password.isEmpty())
                return;

            loginNow(email,password);
        });
    }

    private void loginNow(String email, String password) {

        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful())
                        return;
                    Toast.makeText(this, "Success LogIn", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }).addOnFailureListener(e -> Toast.makeText(this, "Failed Log In", Toast.LENGTH_SHORT).show());
    }



    private void byId() {
        toRegister = findViewById(R.id.toR);
        emailE = findViewById(R.id.emailL);
        passwordE = findViewById(R.id.passwordL);
        loginBtn = findViewById(R.id.login);

    }
}






