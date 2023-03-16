package com.example.socialnetwork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.socialnetwork.Fragments.ChatFragment;
import com.example.socialnetwork.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseUser user;
    ProfileFragment ProfileFragment;
    BottomNavigationView bottomNavigationView;
    ChatFragment ChatFragment;
    FrameLayout frameLayout;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.profile:
                    Framents(ProfileFragment);
                    return true;
                case R.id.chat:
                    Framents(ChatFragment);
                    return true;
            }
            return false;
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                logOut();
                return true;
            case R.id.edit:
                startActivity(new Intent(this,EditProfile.class));
                return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    private void logOut(){
        FirebaseAuth.getInstance().signOut();

    // Clear cached user data
        SharedPreferences preferences = getSharedPreferences("myUsers", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent i = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(i);
    }
    void Framents(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }


    private void init(){
        frameLayout = findViewById(R.id.frameLayout);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ProfileFragment = new ProfileFragment();
        ChatFragment = new ChatFragment();
        bottomNavigationView = findViewById(R.id.bottomMenu);
        reference = FirebaseDatabase.getInstance().getReference("myUsers").child(user.getUid());
        Framents(ProfileFragment);

    }

    private void checkStatus(String status){
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