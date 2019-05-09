package dhbw.familymanager.familymanager.photoGallery;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.adapter.GalleryViewAdapter;
import dhbw.familymanager.familymanager.model.Photo;

public class FolderScreenActivity extends AppCompatActivity {

    private String family;
    private String folderName;
    private ArrayList<String> photos;
    private static boolean refresh = false;
    private final int REQUEST_WRITE_STORAGE = 1;
    private final int REQUEST_CAMERA = 2;
    private List<Uri> filePaths = new ArrayList<Uri>();
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private String photoName;
    private FirebaseFirestore db;
    private String photoPath;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        family = MainActivity.getFamily();
        Intent i = getIntent();
        folderName = i.getStringExtra("albumName");
        setContentView(R.layout.image_gallery);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        getPhotos();
    }

    public static void update(){
        refresh = true;
    }

    @Override
    protected void onPostResume(){
        super.onPostResume();
        if (refresh)
        {
            refresh = false;
            photos.clear();
            getPhotos();
        }
    }

    private void getPhotos() {
        DocumentReference docRef = db.collection("folders").document(family + folderName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        photos = (ArrayList<String>) document.get("photos");
                        setAdapter();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    private void setAdapter() {
        GridView gridView = (GridView)findViewById(R.id.gridview);
        ArrayList<String> photosPath = new ArrayList<>();
        for(String photo: photos)
        {
            photosPath.add("albumPhotos/"+family + "/" + folderName+ "/" +photo);
        }
        String[] photoArray = new String[photosPath.size()];
        GalleryViewAdapter adapter = new GalleryViewAdapter(getApplicationContext(), photosPath.toArray(photoArray), this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String photo = photos.get(position);
                Intent intent = new Intent(FolderScreenActivity.this, ShowPictureActivity.class);
                intent.putExtra("photoObject", photo);
                intent.putExtra("albumName", family+folderName);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photoalbum_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_photo:
                showFileChooser();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    createFolder();
                } else
                {
                    Toast.makeText(this, "Die App hat keine Erlaubnis auf deine Dateien zuzugreifen. Willst du dieses Recht erlauben? ", Toast.LENGTH_LONG).show();
                }
            }
            case REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {}
                else
                {
                    Toast.makeText(this, "Die App hat keine Erlaubnis auf deine Kamera zuzugreifen. Willst du dieses Recht erlauben? ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void createFolder() {
        final File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FamilyManager");
        if (!imageStorageDir.exists()) {
            Toast.makeText(this, "Neuer Ordner wird erstellt...", Toast.LENGTH_SHORT).show();
            boolean rv = imageStorageDir.mkdir();
            Toast.makeText(this, "Ordner" + ( rv ? "wurde erstellt" : "konnte nicht erstellt werden"), Toast.LENGTH_SHORT).show();
        }
    }

    private void showFileChooser() {
        final File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "FamilyManager");
        try {

            boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

            boolean hasCameraPermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);

            if (!hasPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
                return;
            }
            if (!hasCameraPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
                return;
            }
            createFolder();
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");

            filePath = Uri.fromFile(file);
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);

            Intent albumIntent = new Intent(this, ImagePickActivity.class);
           // albumIntent.setType("images/*");
            //albumIntent.putExtra(ImagePickActivity.IS_NEED_CAMERA, true);
            albumIntent.putExtra(Constant.MAX_NUMBER, 9);

            Intent chooserIntent = Intent.createChooser(albumIntent, "Wähle ein Bild zum Hochladen aus");

            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[] { captureIntent });
            startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
        }
        catch(Exception e){
            Toast.makeText(getBaseContext(), "Exception:"+e,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null)
        {
            ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
            if (list != null)
            {
                for (ImageFile file: list)
                {
                    filePaths.add(Uri.parse("file://" + file.getPath()));
                }
                addPhotos();
            }
           // filePath = data.getData();
        }
        if(filePath != null)
        {
            addPhoto();
        }
    }

    private void addPhotos() {
        for (Uri fileUri: filePaths)
        {
            filePath = fileUri;
            addPhoto();
        }
        filePath = null;
    }

    private void addPhoto() {
        photoName = UUID.randomUUID().toString();
        addPictureToFolderDB();
        photoPath = "albumPhotos/"+family + "/" + folderName+ "/" + photoName;
        uploadImage();
        addPictureToPhotosDB();
    }

    private void addPictureToPhotosDB() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Photo photo = new Photo(photoPath, auth.getCurrentUser().getUid(), new Date(), photoName);
        db.collection("photos").document(family + folderName + photoName).set(photo);
    }

    private void addPictureToFolderDB() {
        photos.add(photoName);
        db.collection("folders").document(family+folderName).update("photos", photos);
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        StorageReference ref = storageReference.child(photoPath);
        ref.putFile(filePath)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(FolderScreenActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    photos.clear();
                    getPhotos();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(FolderScreenActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });
    }
}
