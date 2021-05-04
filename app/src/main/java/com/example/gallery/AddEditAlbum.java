package com.example.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;



import java.util.ArrayList;


public class AddEditAlbum extends AppCompatActivity {

    public static final String ALBUM_INDEX = "albumIndex";
    public static final String ALBUM_NAME = "albumName";
    public static final String ALBUM_PHOTOS = "albumPhotos";
    public static final String ALBUM_NAMES = "albumNames";
    public static final String SELECTED_ALBUM = "moveAlbumIndex";
    public static final String PHOTO = "photo";
    public static final String ALBUM_STATE = "state";
    public static final int PICK_IMAGE = 100;
    public static final int MOVE_IMAGE = 200;
    public static final int ADD_TAG_CODE = 300;
    private static final String TAG = "FragmentActivity";

    private int albumIndex;
    private ListView listViewPhotos;
    private ListView listViewTags;
    private EditText albumName;
    private ImageView show_image;
    private ArrayList<Photo> photos;
    private Uri imageUri;
    private static int image_pos;
    private ArrayList<Album> album_names;
    private ArrayList<Tag> tags;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // disabled the back button because:
        // would result in the array resetting to the default state
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tags = new ArrayList<Tag>();
        albumName = findViewById(R.id.album_name);
        show_image = findViewById(R.id.show_image);
        Bundle bundle = getIntent().getExtras();
        albumIndex = bundle.getInt(ALBUM_INDEX);
        photos = bundle.getParcelableArrayList(ALBUM_PHOTOS);
        album_names = bundle.getParcelableArrayList(ALBUM_NAMES);
        if (photos != null && photos.size() > 0) {
            show_image.setImageURI(Uri.parse(photos.get(0).getPath()));
            image_pos = 0;
            tags = photos.get(image_pos).tags;
        }
        albumName.setText(bundle.getString(ALBUM_NAME));
        listViewPhotos = findViewById(R.id.image_list);
        myAdapter adapter= new myAdapter(this, photos);
        listViewPhotos.setAdapter(adapter);
        listViewPhotos.setOnItemClickListener((p, V, pos, id) ->
                showImage(pos));

        listViewTags = findViewById(R.id.tags_list);
        listViewTags.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.album, tags)
        );

        listViewTags.setOnItemClickListener((p, V, pos, id) ->
                deleteTag(pos));
    }

    public void addTag(View view) {
        Intent intent = new Intent(this, AddTag.class);
        startActivityForResult(intent, ADD_TAG_CODE);
    }

    public void deleteTag(int pos) {
        tags.remove(pos);
        photos.get(image_pos).tags = this.tags;
        setTags();
    }

    public void setTags() {
        tags = photos.get(image_pos).tags;
        listViewTags.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.album, tags)
        );
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please use the provided back button", Toast.LENGTH_LONG).show();
    }

    public void move_image(View view) {
        if(photos.size() == 0)
            return;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ALBUM_NAMES, album_names);
        bundle.putInt(ALBUM_INDEX, albumIndex);
        bundle.putParcelable(PHOTO, photos.get(image_pos));
        Intent intent = new Intent(this, moveImage.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, MOVE_IMAGE);
    }

    public void showImage(int pos) {
        show_image.setImageURI(Uri.parse(photos.get(pos).getPath()));
        image_pos = pos;
        setTags();
    }

    public void previous_image(View view) {
        if (photos == null || photos.size() == 0)
            return;
        if (image_pos > 0)
            image_pos--;
        else
            image_pos = photos.size()-1;
        showImage(image_pos);
    }

    public void next_image(View view) {
        if (photos == null || photos.size() == 0)
            return;
        if (image_pos == photos.size()-1)
            image_pos = 0;
        else
            image_pos++;
        showImage(image_pos);
    }

    public void delete_image(View view) {
        if (photos == null || photos.size() == 0)
            return;
        photos.remove(image_pos);
        myAdapter adapter= new myAdapter(this, photos);
        listViewPhotos.setAdapter(adapter);
        if (photos.size() != 0)
            previous_image(view);
        else
            show_image.setImageResource(android.R.color.transparent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_add:
                addPhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addPhoto() {
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    public void cancel(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt(ALBUM_INDEX, albumIndex);
        bundle.putParcelableArrayList(ALBUM_NAMES, album_names);
        bundle.putParcelableArrayList(ALBUM_PHOTOS, photos);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_CANCELED, intent);
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
        bundle.putParcelableArrayList(ALBUM_NAMES, album_names);
        bundle.putParcelableArrayList(ALBUM_PHOTOS, photos);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void delete_album(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt(ALBUM_STATE, 0);
        bundle.putInt(ALBUM_INDEX, albumIndex);
        bundle.putParcelableArrayList(ALBUM_NAMES, album_names);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(photosAlbum.DELETE_ALBUM, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String fileName = "";
        String path = "";
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            getContentResolver().takePersistableUriPermission(imageUri, 0);
            if (imageUri.getScheme().equals("file")) {
                fileName = imageUri.getLastPathSegment();
            } else {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(imageUri, new String[]{
                            MediaStore.Images.ImageColumns.DISPLAY_NAME
                    }, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    }
                } finally {

                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                cursor = getContentResolver().query(imageUri,  proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path =  cursor.getString(column_index);
            } catch (Exception e) {
                Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            path = imageUri.toString();
            photos.add(new Photo(path, fileName));
            myAdapter adapter= new myAdapter(this, photos);
            listViewPhotos.setAdapter(adapter);
        }
        else if (resultCode == RESULT_OK && requestCode == MOVE_IMAGE) {
                Bundle bundle = data.getExtras();
                album_names = bundle.getParcelableArrayList(ALBUM_NAMES);
                delete_image(findViewById(R.id.root));
        }
        else {
            if (resultCode == RESULT_OK && requestCode == ADD_TAG_CODE) {
                Bundle bundle = data.getExtras();
                String name = bundle.getString(AddTag.TAG_NAME);
                String type = bundle.getString(AddTag.TAG_TYPE);
                if (type.equals("Person")) {
                    tags.add(new personTag(name));
                }
                else {
                    tags.add(new locationTag(name));
                }
                setTags();
                photos.get(image_pos).tags = this.tags;
                setTags();
            }
        }
    }
}
