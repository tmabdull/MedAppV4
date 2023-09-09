package com.example.medappv4;

import java.util.Arrays;
import java.util.List;

public class Medicine {
    // List to represent the days of the week.
    // [true, false, ..., true] => [Sunday, Monday, ..., Saturday]
    private List<Boolean> daysOfWeek;
    private String name;

    // Separate attributes for hour and minute to represent the time.
    private int hourOfDay;  // 0-23
    private int minute;     // 0-59

    private String id;


//    // Default constructor initializing the attributes.
//    public Medicine() {
//        // Initialize the daysOfWeek list with all false values (no notifications by default).
//        this.daysOfWeek = Arrays.asList(false, false, false, false, false, false, false);
//
//        // Setting a default time, here, 12:00 (noon). You can adjust as needed.
//        this.hourOfDay = 12;
//        this.minute = 0;
//
//        this.name = "";
//    }

    // Firestore needs an empty constructor -- it will use the setter methods
    public Medicine() {}

    // Parameterized constructor to initialize with provided values.
    public Medicine(List<Boolean> daysOfWeek, int hourOfDay, int minute, String name) {
        this.daysOfWeek = daysOfWeek;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.name = name;
    }

    // Getters and Setters
    public List<Boolean> getDaysOfWeek() {
        return daysOfWeek;
    }
    public void setDaysOfWeek(List<Boolean> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }
    public void setHourOfDay(int hourOfDay) {
        if (hourOfDay >= 0 && hourOfDay < 24) {  // Validation
            this.hourOfDay = hourOfDay;
        }
    }

    public int getMinute() {
        return minute;
    }
    public void setMinute(int minute) {
        if (minute >= 0 && minute < 60) {  // Validation
            this.minute = minute;
        }
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}

