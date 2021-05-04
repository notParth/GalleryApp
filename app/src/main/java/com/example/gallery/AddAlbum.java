package com.example.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;


public class AddAlbum extends AppCompatActivity {

    private EditText albumName;
    private int albumIndex;
    public static final String ALBUM_INDEX = "albumIndex";
    public static final String ALBUM_NAME = "albumName";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumName = findViewById(R.id.album_name);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please use the provided back button", Toast.LENGTH_LONG).show();
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void save(View view) {
        String name = albumName.getText().toString();

        if (name == null || name.length() ==0) {
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Name required");
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putInt(ALBUM_INDEX, albumIndex);
        bundle.putString(ALBUM_NAME, name);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
