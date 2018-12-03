package dhbw.familymanager.familymanager.model;

import java.util.List;

public class Calendar {
    private String ownerId;
    private List<Event> events;

    public Calendar() {
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
