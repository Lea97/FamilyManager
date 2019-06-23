package dhbw.familymanager.familymanager.List;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Aufgabe {
    private String owner;
    private Date added;
    private String name;

    public Aufgabe() {
    }

    public Aufgabe(String owner, Date added, String name) {

        this.owner = owner;
        this.added = added;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }
}
