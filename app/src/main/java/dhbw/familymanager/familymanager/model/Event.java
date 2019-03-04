package dhbw.familymanager.familymanager.model;


import java.util.Calendar;
import java.util.Date;

public class Event {
    private String uid;
    private long id;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String title;
    private String familyId;
    private Date start;
   // private Calendar start;
    private Date end;



    public Event() {
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getTitle() {
        return title;
    }
    public String getFamilyId(){
        return familyId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFamilyId(String familyId){
        this.familyId=familyId;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }




    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getUid() {
        return uid;
    }

}
