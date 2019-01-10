package dhbw.familymanager.familymanager.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class User {

    private String name;
    private Date birthday;
    private String email;
    private String phonennumber;


    public User() {
    }

    public User(String name, Date birthday, String email, String phonennumber) {
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.phonennumber = phonennumber;
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

    public String getPhonennumber() {
        return phonennumber;
    }

    public void setPhonennumber(String phonennumber) {
        this.phonennumber = phonennumber;
    }

}
