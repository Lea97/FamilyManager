package dhbw.familymanager.familymanager.model;


import java.util.Date;
import java.util.List;

public class Event {
    private long id;
    private String title;
    private String familyId;
    private Date start;
    private Date end;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;



    public Event() {
    }

    public void setId(long id) {
        this.id = id;
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

    public long getId() {
        return id;
    }

}
