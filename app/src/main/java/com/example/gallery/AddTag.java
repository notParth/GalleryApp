package com.example.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class AddTag extends AppCompatActivity {

    public static final String TAG_NAME = "name";
    public static final String TAG_TYPE = "type";
    private EditText tagName;
    private Spinner tagType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tag);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tagName = findViewById(R.id.tag_name);
        tagType = findViewById(R.id.tag_spinner);
        ArrayAdapter<CharSequence> adapter
                = ArrayAdapter.createFromResource(this, R.array.tags_type, android.R.layout.simple_spinner_item);
        tagType.setAdapter(adapter);
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
        String name = tagName.getText().toString();
        String tag = tagType.getSelectedItem().toString();

        if (name == null || name.length() == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Name required");
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(TAG_NAME, name);
        bundle.putString(TAG_TYPE, tag);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
