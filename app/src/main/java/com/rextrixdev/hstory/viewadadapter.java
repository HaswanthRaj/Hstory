package com.rextrixdev.hstory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class viewadadapter extends RecyclerView.Adapter<viewadadapter.ViewHolder> {


    private static int pos;
    Context context;
    public int a=0;


    ArrayList<Story> storie;
    ArrayList<String> storyid;
    private StorageReference storageReference;

    private SelectListener selectListener;




    public viewadadapter(Context context, ArrayList<Story> storie, ArrayList<String> storyid, SelectListener selectListener) {
        this.context = context;
        this.storie = storie;
        this.storyid = storyid;
        this.selectListener = selectListener;


    }

    public String getusernam(String id, ViewHolder holder){

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
                        holder.author.setText("by " +nam[0]);

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return name[0];
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Story story = storie.get(position);
        holder.title.setText(story.getTitle());
        holder.date.setText("On: "+story.date);
        holder.views.setText(": "+story.views);
        holder.language.setText(story.language);
        getusernam(story.userid, holder);
        getstoryidbyuser(story.title,story.userid, holder);
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.onItemClicked(storie.get(position));
                pos = position;
            }
        });

    }


    @Override
    public int getItemCount() {
        return storie.size();
    }

    public void filterList(ArrayList<Story> filteredList) {
        storie = filteredList;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CardView cardview;
        TextView author;
        ImageView imageView;
        TextView date;
        TextView views,language;
        public ViewHolder(@NonNull View v) {
            super(v);

            title= (TextView) v.findViewById(R.id.stitle);
            cardview = (CardView) v.findViewById(R.id.maincont);
            author = (TextView) v.findViewById(R.id.sauthor);
            imageView = (ImageView) v.findViewById(R.id.storycover);
            date = (TextView) v.findViewById(R.id.date);
            views = (TextView) v.findViewById(R.id.views);
            language = (TextView) v.findViewById(R.id.language);

        }
    }
    public static int getstoryid(){
        return pos;
    }

    public void getstoryidbyuser(String name,String id, ViewHolder holder){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("stories");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Story story = ds.getValue(Story.class);
                    if (story.title.equals(name) && story.userid.equals(id)) {
                        storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference profileRef = storageReference.child("storycoverpics/"+ ds.getKey() );
                        a=a+1;
                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(holder.imageView);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return ;
    }


}
