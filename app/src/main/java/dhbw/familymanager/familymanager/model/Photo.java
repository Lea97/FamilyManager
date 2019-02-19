package dhbw.familymanager.familymanager.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Photo {

    private String path;
    private String owner;
    private Date added;
    private String name;

    public Photo() {}

    public Photo(String path, String owner, Date added, String name) {
        this.path = path;
        this.owner = owner;
        this.added = added;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
