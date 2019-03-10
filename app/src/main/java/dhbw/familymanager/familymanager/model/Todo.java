package dhbw.familymanager.familymanager.model;

import java.util.List;

public class Todo {

    private String listName;
    private List<String> tasks;

    public Todo(){

    }

    public Todo(String listName, List<String> tasks){
        this.listName=listName;
        this.tasks=tasks;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
}
