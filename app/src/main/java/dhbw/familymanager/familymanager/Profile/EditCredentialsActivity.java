package dhbw.familymanager.familymanager.Profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import dhbw.familymanager.familymanager.R;

public class EditCredentialsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private EditText emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.change_credentials_page);
        findViewById(R.id.saveCedentialChange).setOnClickListener(this);
        findViewById(R.id.cancleCredentialChange).setOnClickListener(this);
        fillEmailField();
    }

    private void fillEmailField() {
        emailField = findViewById(R.id.emailTextfield);
        emailField.setText(auth.getCurrentUser().getEmail());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancleCredentialChange:
                this.finish();
                break;
            case R.id.saveCedentialChange:
                if (validate()) {
                    saveChanges();
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return true;
    }

    private boolean validate() {
        EditText oldPasswordField = findViewById(R.id.oldPasswordTextfield);
        if(oldPasswordField.getText().toString().isEmpty())
        {
            oldPasswordField.setError("Bitte geben Sie ihr aktuelles Passwort ein.");
            return false;
        }
        EditText newPasswordField = findViewById(R.id.newPasswordTextfield);
        EditText repeatNewPasswordField = findViewById(R.id.repeatPasswordField);
        if(!(newPasswordField.getText().toString().isEmpty())& !(repeatNewPasswordField.getText().toString().isEmpty()))
        {
            if(!newPasswordField.getText().toString().equals(repeatNewPasswordField.getText().toString()))
            {
                repeatNewPasswordField.setError("Die Wiederholung des Passwort stimmt nicht mit dem eingegeben überein.");
                return false;
            }
        }
        if (!(newPasswordField.getText().toString().isEmpty())& (repeatNewPasswordField.getText().toString().isEmpty())){
            repeatNewPasswordField.setError("Bitte wiederholen Sie ihr neues Passwort.");
            return false;
        }
        if ((newPasswordField.getText().toString().isEmpty())& !(repeatNewPasswordField.getText().toString().isEmpty())){
            repeatNewPasswordField.setError("Bitte geben Sie ihr neues Passwort ein.");
            return false;
        }
        //TODO validieren dass eingegeben E-Mail Adresse auch dem Format einer solchen entspricht

        return true;
    }

    private void saveChanges() {
      //  AuthCredential authCredential;
      //  FirebaseUser user = auth.getCurrentUser();
      //  auth.getCurrentUser().reauthenticateAndRetrieveData(authCredential);

      //  user.updateEmail(emailField.getText().toString());

    }
}
