package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

class Album implements Parcelable, Serializable {

    private String name;
    private ArrayList<Photo> photos;

    Album(String name) {
        this.name = name;
        photos = new ArrayList<Photo>();
    }

    protected Album(Parcel in) {
        name = in.readString();
        photos = in.createTypedArrayList(Photo.CREATOR);
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(photos);
    }
}


class Photo implements Parcelable, Serializable{
    String path;
    ArrayList<Tag> tags;
    String FileName;

    Photo(String path, String FileName) {
        this.path = path;
        this.FileName = FileName;
        tags = new ArrayList<>();
    }

    protected Photo(Parcel in) {
        path = in.readString();
        FileName = in.readString();
        tags = in.createTypedArrayList(Tag.CREATOR);
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };


    public ArrayList<Tag> getTags() {
        return tags;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(FileName);
        dest.writeTypedList(tags);
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return FileName;
    }
}


class Tag implements Parcelable, Serializable {
    String type;
    String name;

    Tag(String type, String name) {
        this.type = type;
        this.name = name;
    }

    protected Tag(Parcel in) {
        type = in.readString();
        name = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    public String toString() {
        return type + ": "+ name;
    }
    // pending implementation
    public String getString() {
        return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(name);
    }
}

class personTag extends Tag {
    personTag(String name) {
        super("person", name);
    }
}

class locationTag extends Tag {
    locationTag(String name) {
        super("location", name);
    }
}

public class photosAlbum extends AppCompatActivity {

    public static final int EDIT_ALBUM_CODE = 1;
    public static final int ADD_ALBUM_CODE = 2;
    public static final int DELETE_ALBUM = 1;
    public static final int SEARCH = 3;
    private ListView listView;
    private ArrayList<Album> albums;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_view);

        try {
            FileInputStream fis = openFileInput("myData");
            ObjectInputStream ois = new ObjectInputStream(fis);
            albums = (ArrayList) ois.readObject();
            ois.close();
            fis.close();

        }
        catch (IOException | ClassNotFoundException e) {
            albums = new ArrayList<>(1);
            albums.add(new Album("sample"));
        }

        button = findViewById(R.id.button2);
        button.setOnClickListener(e -> searchMove());


        listView = findViewById(R.id.album_list);
        listView.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, albums)
        );

        listView.setOnItemClickListener((p, V, pos, id) ->
                showAlbum(pos));
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput("myData", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(albums);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addMovie();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void addMovie() {
        Intent intent = new Intent(this, AddAlbum.class);
        startActivityForResult(intent, ADD_ALBUM_CODE);
    }


    private void searchMove() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(AddEditAlbum.ALBUM_NAMES, albums);
        Intent intent = new Intent(this, searchTag.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, SEARCH);
    }

    private void showAlbum(int pos) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(AddEditAlbum.ALBUM_NAMES, albums);
        Album album = albums.get(pos);
        bundle.putInt(AddEditAlbum.ALBUM_INDEX, pos);
        bundle.putString(AddEditAlbum.ALBUM_NAME, album.toString());
        bundle.putParcelableArrayList(AddEditAlbum.ALBUM_PHOTOS, album.getPhotos());
        Intent intent = new Intent(this, AddEditAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_ALBUM_CODE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onStop();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == EDIT_ALBUM_CODE) {
            Bundle bundle = null;
            try {
                bundle = intent.getExtras();
            }
            catch(NullPointerException e) {
                return;
            }
            if (bundle == null)
                return;

            int index = bundle.getInt(AddEditAlbum.ALBUM_INDEX);
            if (resultCode == RESULT_CANCELED) {
                albums = bundle.getParcelableArrayList(AddEditAlbum.ALBUM_NAMES);
                albums.get(index).setPhotos(bundle.getParcelableArrayList(AddEditAlbum.ALBUM_PHOTOS));
                listView.setAdapter(
                        new ArrayAdapter<Album>(this, R.layout.album, albums));
                return;
            }
            if (resultCode == DELETE_ALBUM) {
                albums = bundle.getParcelableArrayList(AddEditAlbum.ALBUM_NAMES);
                albums.remove(index);
                listView.setAdapter(
                        new ArrayAdapter<Album>(this, R.layout.album, albums));
                return;
            }
            albums = bundle.getParcelableArrayList(AddEditAlbum.ALBUM_NAMES);
            String name = bundle.getString(AddEditAlbum.ALBUM_NAME);
            Album album = albums.get(index);
            albums.get(index).setPhotos(bundle.getParcelableArrayList(AddEditAlbum.ALBUM_PHOTOS));
            album.setName(name);
        }
        else {
            if (resultCode != RESULT_OK)
                return;
            Bundle bundle = intent.getExtras();
            if (bundle == null)
                return;
            String name = bundle.getString(AddEditAlbum.ALBUM_NAME);
            albums.add(new Album(name));

        }

        listView.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, albums)
        );
    }
}