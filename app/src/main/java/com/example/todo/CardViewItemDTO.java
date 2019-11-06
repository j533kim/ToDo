package com.example.todo;

public class CardViewItemDTO {
    public String task;
    public float importance;
    public String details;
    public String dateAdded;
    public String dateEnd;
    public boolean done;

    public CardViewItemDTO(String task, float importance, String details, String dateAdded, String dateEnd, boolean done) {
        this.task = task;
        this.importance = importance;
        this.details = details;
        this.dateAdded = dateAdded;
        this.dateEnd = dateEnd;
        this.done = done;
    }
}
