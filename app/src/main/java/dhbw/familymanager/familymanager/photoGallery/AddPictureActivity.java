package dhbw.familymanager.familymanager.photoGallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import dhbw.familymanager.familymanager.FileChooser;
import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;

public class AddPictureActivity extends AppCompatActivity implements View.OnClickListener {

    private String family;
    private String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        family = MainActivity.getFamily();
        Intent i = getIntent();
        folderName = i.getStringExtra("albumName");
        setContentView(R.layout.add_photo);
        findViewById(R.id.addPhoto).setOnClickListener(this);
        findViewById(R.id.cancleAddPhoto).setOnClickListener(this);
        findViewById(R.id.choosePicture).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addPhoto:
                if (validate())
                {
                    addPhoto();
                    this.finish();
                }
                break;
            case R.id.cancleAddPhoto:
                this.finish();
                break;
            case R.id.choosePicture:
                startFileChooser();
                break;
        }
    }

    private void startFileChooser() {
        ImageView imageView = findViewById(R.id.choosedPicture);
        FileChooser fileChooser  = new FileChooser("albumPhotos/"+family + "/" + folderName+ "/",getApplicationContext(), this, imageView);
    }

    private Boolean validate() {
        EditText editText = findViewById(R.id.newPhotoName);
        String photoName = editText.getText().toString();
        if (photoName.isEmpty())
        {
            return false;
        }
        return true;
    }

    private void addPhoto() {

    }
}
