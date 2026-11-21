/**
 * Task 06 – Event Entity Model
 * Course: Programming in Java (CSA09)
 * Student: Ranjith Kumar.R | Register Number: 1924L260003
 * Java Version: 27 | IDE: Antigravity
 */
public class Event {
    private final int eventId;
    private final String eventName;
    private final String category;
    private final String eventDate;
    private final String venue;
    private final int capacity;

    public Event(int eventId, String eventName, String category, String eventDate, String venue, int capacity) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.category = category;
        this.eventDate = eventDate;
        this.venue = venue;
        this.capacity = capacity;
    }

    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public String getCategory() { return category; }
    public String getEventDate() { return eventDate; }
    public String getVenue() { return venue; }
    public int getCapacity() { return capacity; }

    @Override
    public String toString() {
        return eventName;
    }
}
