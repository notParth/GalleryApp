package com.example.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class moveImage extends AppCompatActivity {

    private ListView listViewAlbumNames;
    private ArrayList<Album> album_names;
    private int index;
    private Photo photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_image);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listViewAlbumNames = findViewById(R.id.album_names_list);
        Bundle bundle = getIntent().getExtras();
        album_names = bundle.getParcelableArrayList(AddEditAlbum.ALBUM_NAMES);
        index = bundle.getInt(AddEditAlbum.ALBUM_INDEX);
        photo = bundle.getParcelable(AddEditAlbum.PHOTO);
        listViewAlbumNames.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, album_names)
        );

        listViewAlbumNames.setOnItemClickListener((p, V, pos, id) -> album_selected(pos));
    }

    public void cancel_move(View view) {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    public void album_selected(int pos) {
        if (pos == index) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
        album_names.get(pos).getPhotos().add(photo);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(AddEditAlbum.ALBUM_NAMES, album_names);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
