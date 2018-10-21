package dhbw.familymanager.familymanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_page);


    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.registrationButton:
                break;


        }
    }
}
