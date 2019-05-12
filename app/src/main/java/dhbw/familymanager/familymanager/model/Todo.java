package dhbw.familymanager.familymanager.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.List;
@IgnoreExtraProperties
public class Todo {

    private String listName;
    private List<String> tasks;
    private String owner;
    private Date created;

    public Todo(){

    }

    public Todo(String listName, List<String> tasks){
        this.listName=listName;
        this.tasks=tasks;
    }

    public Todo(String owner, Date created, List<String> tasks){
        this.tasks=tasks;
        this.owner=owner;
        this.created=created;
    }

    /*public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }*/

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
