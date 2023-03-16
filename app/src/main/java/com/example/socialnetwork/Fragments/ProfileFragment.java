package com.example.socialnetwork.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialnetwork.Modal.User;
import com.example.socialnetwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    TextView name;
    ImageView image;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("myUsers")
            .child(fuser.getUid());
    StorageReference storage;
    static final int IMAGE_REQUEST = 1;
    Uri imageUrl;
    StorageTask<UploadTask.TaskSnapshot> upload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        image = view.findViewById(R.id.imgProfile);
        name = view.findViewById(R.id.nameProfile);


        storage = FirebaseStorage.getInstance().getReference("uploads");

        assert fuser != null;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                assert user != null;
                name.setText(user.getUsername());

                if(user.getImgUrl().equals("default")){
                    image.setImageResource(R.drawable.baseline_add_a_photo_24);
                }else{

                    Picasso.get()
                            .load(user.getImgUrl())
                            .into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        image.setOnClickListener(v -> SelectImage());

        return view;
    }


   private void SelectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,IMAGE_REQUEST);

    }

   private String getFileExtention(Uri uri){
        ContentResolver resolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

            imageUrl = data.getData();
            if(upload != null && upload.isInProgress())
                Toast.makeText(getContext(), "Upload is progress...", Toast.LENGTH_SHORT).show();
                UploadImage();

    }



    void UploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        if(imageUrl != null){
            final StorageReference fileReference = storage.child(System.currentTimeMillis()
            +  "." + getFileExtention(imageUrl));
            upload = fileReference.putFile(imageUrl);
            upload.continueWithTask(task -> {

                if(!task.isSuccessful()){
                    throw task.getException();
                }

                return fileReference.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if(!task.isSuccessful())
                    return;

                Uri downloadUri = task.getResult();
                String mUri = downloadUri.toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;

                reference = FirebaseDatabase.getInstance().getReference("myUsers")
                        .child(user.getUid());


                HashMap<String,Object> map = new HashMap<>();
                map.put("imgUrl",mUri);
                reference.updateChildren(map);
                progressDialog.dismiss();

            });
        }
    }
}



