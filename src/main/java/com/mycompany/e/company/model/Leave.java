package com.mycompany.e.company.model;

import javafx.beans.property.*;

public class Leave {
    private final IntegerProperty id;
    private final StringProperty employeeName;
    private final StringProperty leaveType;
    private final StringProperty startDate;
    private final StringProperty endDate;
    private final StringProperty reason;
    private final StringProperty status;

    public Leave(int id, String employeeName, String leaveType, String startDate, String endDate, String reason, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.leaveType = new SimpleStringProperty(leaveType);
        this.startDate = new SimpleStringProperty(startDate);
        this.endDate = new SimpleStringProperty(endDate);
        this.reason = new SimpleStringProperty(reason);
        this.status = new SimpleStringProperty(status);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty employeeNameProperty() { return employeeName; }
    public StringProperty leaveTypeProperty() { return leaveType; }
    public StringProperty startDateProperty() { return startDate; }
    public StringProperty endDateProperty() { return endDate; }
    public StringProperty reasonProperty() { return reason; }
    public StringProperty statusProperty() { return status; }
}