package com.example.tattoo;

import java.util.Date;

public class Appointment {
    private String name;
    private String design;
    private String date;
    private String email;

    public Appointment() {
        // Empty constructor required for Firestore
    }

    public Appointment(String name, String design, String date, String email) {
        this.name = name;
        this.design = design;
        this.date = date;
        this.email = email;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

