package com.valdemar.spook.tiktok;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdemar.spook.R;

import java.util.ArrayList;
import java.util.List;

public class TiktokMainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiktok_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("videos_cortos");

        final ViewPager2 videosViewPager = findViewById(R.id.viewPagerVideos);
        final List<VideoItem> videoItems = new ArrayList<>();


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot childSnapshot: dataSnapshot.getChildren())
                {
                    final VideoItem m = childSnapshot.getValue(VideoItem.class);
                    videoItems.add(m);
                }
                videosViewPager.setAdapter(new VideosAdapter(videoItems));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }
}