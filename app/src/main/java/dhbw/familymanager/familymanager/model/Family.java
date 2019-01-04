package dhbw.familymanager.familymanager.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

//@IgnoreExtraProperties
public class Family {

    private String familyName;
    private List<String> members;


    public Family() {
    }

    public Family(String familyName, List<String> members) {
        this.familyName = familyName;
        this.members = members;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> users) {
        this.members = members;
    }
}
