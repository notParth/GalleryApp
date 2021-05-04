package com.example.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;


public class searchTag extends AppCompatActivity {
    public static final String ALBUM_NAMES = "albumNames";

    private Spinner dropdown;
    private ListView listViewPhotos;
    private ArrayList<Photo> photos;
    private ArrayList<Album> album_names;
    private AutoCompleteTextView tagValue;
    private Button b;
    private ArrayList<String> locTags;
    private ArrayList<String> perTags;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_tag);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        album_names = bundle.getParcelableArrayList("albumNames");
        tagValue = findViewById(R.id.tagValue);

        b = findViewById(R.id.sbutton);
        b.setOnClickListener(e -> search());

        dropdown = findViewById(R.id.tagTypeSpinner);
        String[] items = new String[]{"location", "person"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(0);


        locTags = new ArrayList<String>();
        perTags = new ArrayList<String>();
        for(Album a : album_names){
            for(Photo p : a.getPhotos()){
                for(Tag t : p.getTags()){
                    if(t.getType().equals("location")){
                        locTags.add(t.name);
                    } else{
                        perTags.add(t.name);
                    }
                }
            }
        }

        ArrayAdapter<String> adapterloc = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, locTags);
       ArrayAdapter<String> adapterper = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, perTags);
        tagValue.setAdapter(adapterloc);
        tagValue.setThreshold(1);

       dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
                if (pos == 0) {
                    tagValue.setAdapter(adapterloc);
                }else{
                    tagValue.setAdapter(adapterper);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please use the provided back button", Toast.LENGTH_LONG).show();
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void search() {

        listViewPhotos = findViewById(R.id.searchedList);


        dropdown = findViewById(R.id.tagTypeSpinner);
        String tagType = dropdown.getSelectedItem().toString();
        tagValue = findViewById(R.id.tagValue);
        String tagVal = tagValue.getText().toString();
        photos = new ArrayList<Photo>();

        for(Album a : album_names){
            for(Photo p : a.getPhotos()){
                for(Tag t : p.getTags()){
                    if(tagType.equals(t.getType())){
                        if(tagVal.equalsIgnoreCase(t.getName())){
                            photos.add(p);
                        }
                    }
                }
            }
        }
        myAdapter adapter2= new myAdapter(this, photos);
        listViewPhotos.setAdapter(adapter2);

    }
}
