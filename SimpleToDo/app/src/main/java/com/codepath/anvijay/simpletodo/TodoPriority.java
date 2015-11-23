package com.codepath.anvijay.simpletodo;

/**
 * Created by anvijay on 11/19/15.
 */
public enum TodoPriority {
    LOW(0),
    MEDIUM(1),
    HIGH(2);

    private int todoPriority;

    private TodoPriority(int value) {
        this.todoPriority = value;
    }

    public int getTodoPriority() {
        return todoPriority;
    }
}
