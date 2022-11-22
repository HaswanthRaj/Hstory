package com.rextrixdev.hstory;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rextrixdev.hstory.databinding.ActivityStoryreadBinding;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class storyread extends AppCompatActivity {

    private ActivityStoryreadBinding binding;
    private TextView title;
    private TextView content;
    private int pos;
    private boolean isviewed=false;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    ArrayList<String> storyid;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        binding = ActivityStoryreadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        title= (TextView) findViewById(R.id.conttitle);
        content= (TextView) findViewById(R.id.content);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        pos = viewadadapter.getstoryid();
        storyid = getIntent().getStringArrayListExtra("storyid");

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("stories");


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Story story;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    story = ds.getValue(Story.class);
                    if (ds.getKey().equals(storyid.get(pos))) {
                        title.setText(story.title);
                        content.setText(story.story);
                        if (isviewed==false){
                            db.child(ds.getKey()).child("views").setValue(story.views + 1);
                            isviewed=true;
                        }
                        ;
                        getstorycover();
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(storyread.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });




        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Coming Soon", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    private void getstorycover() {
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("storycoverpics/"+ storyid.get(pos) );
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(new Target() {

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        collapsingToolbarLayout.setBackground(new BitmapDrawable(bitmap));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(storyread.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}