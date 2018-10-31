package dhbw.familymanager.familymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText textFieldEmail;
    private EditText textFieldPassword;
    private EditText textFieldRepeatPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        textFieldEmail = findViewById(R.id.emailField);
        textFieldPassword = findViewById(R.id.passwordField);
        textFieldRepeatPassword = findViewById(R.id.repeatPasswordField);

        findViewById(R.id.toLoginButton).setOnClickListener(this);
        findViewById(R.id.loginButton).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth==null){
            System.out.println("FIREBASE NOT WORKING");
        }
    }

    private void registerUser() {
        String email = textFieldEmail.getText().toString().trim();
        String password = textFieldPassword.getText().toString().trim();
        String repeatPassword = textFieldRepeatPassword.getText().toString().trim();



        if(email.isEmpty()){
            textFieldEmail.setError("Email is required");
            textFieldEmail.requestFocus();
            return;
        }

        if(!(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            textFieldEmail.setError("Enter a valid email");
            textFieldEmail.requestFocus();
            return;
        }

        if(password.length()<6){
            textFieldPassword.setError("Minimum length of password should be 6");
            textFieldPassword.requestFocus();
            return;
        }

        if(password.isEmpty()){
            textFieldPassword.setError("Password is required");
            textFieldRepeatPassword.setError("Password is required");
            textFieldPassword.requestFocus();
            return;
        }

        if(!(password.equals(repeatPassword))){
            textFieldPassword.setError("Password is required");
            textFieldRepeatPassword.setError("Password is required");
            textFieldPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                           // mAuth.getCurrentUser().sendEmailVerification();
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"E-mail or password is wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        //mAuth.createUserWithEmailAndPassword(email, password)
          //      .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            //        @Override
        //public void onComplete(@NonNull Task<AuthResult> task) {
        //              if (task.isSuccessful()) {
        //                  System.out.println("Success tralala");
//
        //                          Toast.makeText(getApplicationContext(), "User Registered Successful", Toast.LENGTH_SHORT).show();
        //                  FirebaseUser user=mAuth.getCurrentUser();
        //                  user.sendEmailVerification();


        //              }
        //              if(task.isCanceled()){
        //                  Toast.makeText(getApplicationContext(), "User could not Registered", Toast.LENGTH_SHORT).show();
        //              }
        //          }
        //      });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.toLoginButton:
               startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                break;
            case R.id.loginButton:
                registerUser();
                break;
        }
    }
}
