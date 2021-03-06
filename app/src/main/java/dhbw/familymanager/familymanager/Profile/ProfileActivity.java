package dhbw.familymanager.familymanager.Profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.User;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = null;
    private static Boolean refresh = false;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private User user;
    private ImageView mImageView;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private Boolean deleted = false;
    private AlertDialog alertDialog;
    private EditText passwordField;

    static void refreshValues() {
        refresh = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (refresh) {
            refresh = false;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(auth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            createUserFromDocument(document);
                            fillFormular();
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    private void createUserFromDocument(DocumentSnapshot document) {
        user = new User();
        user.setName((String) document.get("name"));
        user.setEmail((String) document.get("email"));
        Timestamp birth = (Timestamp) document.get("birthday");
        user.setBirthday(birth.toDate());
        user.setPhonenumber((String) document.get("phonenumber"));
        user.setPicturePath((String) document.get("picturePath"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = storage.getReference();
        Intent i = getIntent();
        user = (User) i.getSerializableExtra("userObject");
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.profile);
        setValues();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showPicture(String picturePath) {
        StorageReference storageReference = storage.getReference(picturePath);
        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this /* context */).load(storageReference).into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (user != null) {
            if (!(user.getEmail().equals(firebaseUser.getEmail()))) {
                return false;
            }
        }
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("userObject", user);
                startActivity(intent);
                setValues();
                break;
            case R.id.delete_profile:
                showAlertDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Wollen Sie wirklich Ihr Profil löschen? Dies kann nicht rückgängig gemacht werden.");
        alertDialogBuilder.setCancelable(true);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_delete_profile, null);
        alertDialogBuilder.setView(dialogView)
                .setPositiveButton(R.string.delete_button, null)

                .setNegativeButton(R.string.cancle_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        passwordField = (EditText) dialogView.findViewById(R.id.passwordForDelete);
                        String password = passwordField.getText().toString();
                        deleteProfile(password);
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void deleteProfile(String password) {
        deleted = false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
        user.reauthenticateAndRetrieveData(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        auth.getCurrentUser().delete();
                        deleted = true;
                        deleteFromFamily();
                        db.collection("users").document(auth.getCurrentUser().getUid()).delete();
                        alertDialog.cancel();
                        finish();
                        MainActivity.logoutUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        passwordField.setError("Passwort ist falsch");
                    }
                });
    }

    private void deleteFromFamily() {
        final String currentEmail = auth.getCurrentUser().getEmail();
        Query query = db.collection("families").whereArrayContains("members", currentEmail);
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            deleteMember(document, currentEmail);
                        }
                    }
                });
    }

    private void deleteMember(DocumentSnapshot document, String emailAdress) {
        ArrayList<String> members = (ArrayList<String>) document.get("members");
        members.remove(emailAdress);
        db.collection("families").document(document.getId()).update("members", members);
    }

    private void setValues() {
        if (user == null) {
            String userId = firebaseUser.getUid();
            DocumentReference docRef = db.collection("users").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            createUserFromDocument(document);
                            fillFormular();
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        } else {
            fillFormular();
        }
    }

    private void fillFormular() {
        final TextView nameTextfield = (TextView) findViewById(R.id.nameTextfield);
        nameTextfield.setText(user.getName());

        final TextView mailTextfield = (TextView) findViewById(R.id.emailTextfield);
        mailTextfield.setText(user.getEmail());

        final TextView birthTextView = (TextView) findViewById(R.id.showDate);
        if (user.getBirthday() != null) {
            final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            ((TextView) findViewById(R.id.birthTextfield)).setText(dateFormat.format(user.getBirthday()));
        }
        final TextView numberTextfield = (TextView) findViewById(R.id.numberTextfield);
        numberTextfield.setText(user.getPhonenumber());

        String picturePath = user.getPicturePath();
        showPicture(picturePath);
    }
}
