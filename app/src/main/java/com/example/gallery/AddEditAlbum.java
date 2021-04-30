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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;



import java.util.ArrayList;


public class AddEditAlbum extends AppCompatActivity {

    public static final String ALBUM_INDEX = "albumIndex";
    public static final String ALBUM_NAME = "albumName";
    public static final String ALBUM_PHOTOS = "albumPhotos";
    public static final String ALBUM_STATE = "state";
    public static final int PICK_IMAGE = 100;
    private static final String TAG = "FragmentActivity";

    private int albumIndex;
    private ListView listViewPhotos;
    private EditText albumName;
    private ImageView show_image;
    private ArrayList<Photo> photos;
    private Uri imageUri;
    private static int image_pos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // disabled the back button because:
        // would result in the array resetting to the default state
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        albumName = findViewById(R.id.album_name);
        show_image = findViewById(R.id.show_image);
        Bundle bundle = getIntent().getExtras();
        albumIndex = bundle.getInt(ALBUM_INDEX);
        photos = bundle.getParcelableArrayList(ALBUM_PHOTOS);
        if (photos != null && photos.size() > 0) {
            show_image.setImageURI(Uri.parse(photos.get(0).getPath()));
            image_pos = 0;
        }
        albumName.setText(bundle.getString(ALBUM_NAME));
        listViewPhotos = findViewById(R.id.image_list);
        myAdapter adapter= new myAdapter(this, photos);
        listViewPhotos.setAdapter(adapter);

        listViewPhotos.setOnItemClickListener((p, V, pos, id) ->
                showImage(pos));
    }

    public void showImage(int pos) {
        show_image.setImageURI(Uri.parse(photos.get(pos).getPath()));
        image_pos = pos;
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
            next_image(view);
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
    }
}
