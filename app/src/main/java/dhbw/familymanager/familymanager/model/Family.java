package dhbw.familymanager.familymanager.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

//@IgnoreExtraProperties
public class Family {

    private String familyName;
    private List<String> members;
    private String owner;


    public Family() {
    }

    public Family(String familyName, List<String> members, String owner) {
        this.familyName = familyName;
        this.members = members;
        this.owner = owner;
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

    public String getOwner() {return owner;}

    public void setOwner(String owner) {this.owner = owner;}
}
