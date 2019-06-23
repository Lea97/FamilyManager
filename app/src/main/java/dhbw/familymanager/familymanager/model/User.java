package dhbw.familymanager.familymanager.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties
public class User implements Serializable {

    private String name;
    private Date birthday;
    private String email;
    private String phonenumber;
    private String picturePath;

    public User() {
    }

    public User(String name, Date birthday, String email, String phonenumber, String picturePath) {
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.phonenumber = phonenumber;
        this.picturePath = picturePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
