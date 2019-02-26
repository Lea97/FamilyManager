package dhbw.familymanager.familymanager.family;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import dhbw.familymanager.familymanager.MainActivity;
import dhbw.familymanager.familymanager.R;
import dhbw.familymanager.familymanager.model.Family;

public class AddFamilyActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_family);

        findViewById(R.id.createFamilyButton).setOnClickListener(this);
        findViewById(R.id.cancleAddFamilyButton).setOnClickListener(this);

    }

    private void createFamily()
    {
        List<String> members = new ArrayList<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        members.add(mAuth.getCurrentUser().getEmail());

        final TextView familyNameTextfield =  findViewById(R.id.familyNameTextfield);
        final TextView familyMembers =  findViewById(R.id.familyMemberTextfield);

        String[] allMembers = familyMembers.getText().toString().split(",");
        for (String member: allMembers) {
            members.add(member.trim());
        }

        Family family = new Family(familyNameTextfield.getText().toString(), members);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("families").document().set(family);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancleAddFamilyButton:
                this.finish();
                break;
            case R.id.createFamilyButton:
                createFamily();
                MainActivity.updateFamiles();
                this.finish();
                break;

        }
    }
}
