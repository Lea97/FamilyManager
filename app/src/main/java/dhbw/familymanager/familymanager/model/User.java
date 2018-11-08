package dhbw.familymanager.familymanager.model;

import java.util.Date;

public class User {

    private String name;
    private Date bithday;
    private String email;
    private String phonennumber;

    public User() {}

    public User(String name, Date bithday, String email, String phonennumber) {
        this.name = name;
        this.bithday = bithday;
        this.email = email;
        this.phonennumber = phonennumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBithday() {
        return bithday;
    }

    public void setBithday(Date bithday) {
        this.bithday = bithday;
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
