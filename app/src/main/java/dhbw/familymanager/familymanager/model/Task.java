package dhbw.familymanager.familymanager.model;

public class Task {

    String taskContent;
    boolean isDone;

    public Task(String taskContent, boolean isDone){
        this.taskContent = taskContent;
        this.isDone = isDone;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }
}
