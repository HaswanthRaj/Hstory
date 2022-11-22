package com.rextrixdev.hstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class createstory extends AppCompatActivity implements View.OnClickListener {

    private Button save;
    private TextInputEditText title, storytext;
    public String username;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private ImageView storycover;
    private Uri imageUri;
    private ProgressBar progressBar;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    String key;
    private String date;
    String[] languages = {"Tamil","English", "Hindi"};
    private AutoCompleteTextView language;
    ArrayAdapter<String> adapter;
    public String lang;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createstory);

        save = (Button) findViewById(R.id.savestory);
        save.setOnClickListener(this);
        storycover = (ImageView) findViewById(R.id.storycover);
        storycover.setOnClickListener(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        language = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, languages);
        language.setAdapter(adapter);
        language.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lang = parent.getItemAtPosition(position).toString();

            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        date = sdf.format(System.currentTimeMillis());

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.savestory:
                savestory();
                break;
            case R.id.storycover:
                Intent opengallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(opengallery, 1);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            //set image
            storycover.setImageURI(data.getData());
            //upload image
            imageUri = data.getData();
        }
    }

    private void savestory() {
        title = (TextInputEditText) findViewById(R.id.storytitle);
        storytext = (TextInputEditText) findViewById(R.id.storytext);

        String t = title.getText().toString();
        String s = storytext.getText().toString();





        Story story = new Story(t, s, FirebaseAuth.getInstance().getCurrentUser().getUid(), date,0,0,lang);

        if(t.isEmpty()){
            title.setError("Title is required");
            title.requestFocus();
        }else if(s.isEmpty()){
            storytext.setError("Story is required");
            storytext.requestFocus();
        }else{
            progressBar = (ProgressBar) findViewById(R.id.progressBarstory);
            progressBar.setVisibility(View.VISIBLE);
            save.setVisibility(View.INVISIBLE);

            db.getReference("stories").push().setValue(story);
            databaseReference = FirebaseDatabase.getInstance().getReference("stories");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Story story1 = ds.getValue(Story.class);
                        if (story1.title.equals(story.title)) {
                                key = ds.getKey();
                            StorageReference filef = storageReference.child("storycoverpics/"+ key );
                            filef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //get url
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) {
                                        ;
                                    }
                                    String downloadUri = uriTask.getResult().toString();
                                    if (uriTask.isSuccessful()) {
                                        //set image
                                        Toast.makeText(createstory.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                        exit();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(createstory.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(createstory.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    private void exit() {
        Intent intent = new Intent(createstory.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}