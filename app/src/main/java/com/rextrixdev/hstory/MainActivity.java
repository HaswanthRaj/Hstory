package com.rextrixdev.hstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rextrixdev.hstory.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SelectListener {

    private FirebaseAuth mAuth;
    private Button create;

    ActivityMainBinding binding;

    RecyclerView recyclerView;
    DatabaseReference db;
    viewadadapter adapter;
    ArrayList<Story> storie;
    ArrayList<String> storyid;
    private TextView welcome,welcomeuser;
    Calendar calendar = Calendar.getInstance();
    private RelativeLayout rootLayout;
    private SearchView searchView;
    private AnimationDrawable animDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        animDrawable = (AnimationDrawable) rootLayout.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();
        searchView =findViewById(R.id.searchbar);

        //Welcoming code

        welcome = (TextView) findViewById(R.id.welcometext);
        welcomeuser = (TextView) findViewById(R.id.welcomeuser);

        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
                 welcome.setText("Good Morning,");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
                 welcome.setText("Good Afternoon,");
        }else if(timeOfDay >= 16 && timeOfDay < 21){
                welcome.setText("Good Evening,");
        }else if(timeOfDay >= 21 && timeOfDay < 24){
                welcome.setText("Good Night,");
        }


        //Bottom navigation code

        binding.bottomnav.setOnNavigationItemSelectedListener(item -> {

                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.profile:
                        Intent intent1 = new Intent(MainActivity.this, profilea.class);
                        startActivity(intent1);
                        break;
                    case R.id.settings:
                        Intent intent2 = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent2);
                        break;
                }
                return true;

        });



        create = (Button) findViewById(R.id.createbutton);
        create.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        db = FirebaseDatabase.getInstance().getReference("stories");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        storie = new ArrayList<>();
        storyid = new ArrayList<String>();
        adapter = new viewadadapter(this, storie,storyid,this);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storie.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Story story = ds.getValue(Story.class);
                    storie.add(story);
                    storyid.add(ds.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void filter(String newText) {
        ArrayList<Story> filteredList = new ArrayList<>();
        for (Story story : storie) {
            if (story.title.toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(story);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createbutton:
                startActivity(new Intent(this, com.rextrixdev.hstory.createstory.class));
                break;
        }

    }

    @Override
    public void onItemClicked(Story story) {
        Intent intent = new Intent(this, com.rextrixdev.hstory.storyread.class);
        intent.putExtra("storyid", storyid);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, com.rextrixdev.hstory.login.class));
            finish();
        }else{
            getusernam(FirebaseAuth.getInstance().getCurrentUser().getUid(),welcomeuser);
        }
    }
    public String getusernam(String id, TextView username) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
        final String[] name = new String[1];
        final String[] nam = {""};
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (ds.getKey().equals(id)) {
                        nam[0] = user.fullname;
                        username.setText(nam[0]);

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return name[0];
    }

}