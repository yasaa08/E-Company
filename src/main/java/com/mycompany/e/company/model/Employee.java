package com.mycompany.e.company.model;

import javafx.beans.property.*;
import java.sql.Date;

public class Employee {
    private final IntegerProperty employeeId;
    private final StringProperty fullName;
    private final StringProperty position;
    private final StringProperty maritalStatus;
    private final DoubleProperty baseSalary;
    private final StringProperty region;

    // Tambahan Data HRIS
    private final StringProperty joinDate;
    private final IntegerProperty childCount;

    public Employee(int id, String name, String pos, String status, double salary, String reg, Date join, int child) {
        this.employeeId = new SimpleIntegerProperty(id);
        this.fullName = new SimpleStringProperty(name);
        this.position = new SimpleStringProperty(pos);
        this.maritalStatus = new SimpleStringProperty(status);
        this.baseSalary = new SimpleDoubleProperty(salary);
        this.region = new SimpleStringProperty(reg);

        // Konversi Date ke String biar mudah ditampilkan
        this.joinDate = new SimpleStringProperty(join != null ? join.toString() : "-");
        this.childCount = new SimpleIntegerProperty(child);
    }

    public IntegerProperty employeeIdProperty() { return employeeId; }
    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty positionProperty() { return position; } // Perbaikan nama getter
    public StringProperty maritalStatusProperty() { return maritalStatus; }
    public DoubleProperty baseSalaryProperty() { return baseSalary; }
    public StringProperty regionProperty() { return region; }
    public StringProperty joinDateProperty() { return joinDate; }
    public IntegerProperty childCountProperty() { return childCount; }
}