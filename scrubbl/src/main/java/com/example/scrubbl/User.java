package com.example.scrubbl;

public class User {
    private final int id;
    private final String name;
    private Roles role;


    private int currentPoints = 0;

    // This class holds the user info

    public User(int id, String name, Roles role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Roles getRole() {
        return role;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }

    public void addPoint() {
        currentPoints++;
    }
}
