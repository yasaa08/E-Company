package com.mycompany.e.company.model;

import javafx.beans.property.*;

public class Payroll {
    private final IntegerProperty id;
    private final StringProperty employeeName;
    private final StringProperty bulan; // Format: "Januari 2026"
    private final DoubleProperty gajiPokok;
    private final DoubleProperty potongan; // Pajak + BPJS
    private final DoubleProperty totalGaji;
    private final StringProperty status; // "Lunas" / "Pending"

    public Payroll(int id, String nama, String bulan, double gapok, double pot, double total, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.employeeName = new SimpleStringProperty(nama);
        this.bulan = new SimpleStringProperty(bulan);
        this.gajiPokok = new SimpleDoubleProperty(gapok);
        this.potongan = new SimpleDoubleProperty(pot);
        this.totalGaji = new SimpleDoubleProperty(total);
        this.status = new SimpleStringProperty(status);
    }

    // Getters for TableView
    public IntegerProperty idProperty() { return id; }
    public StringProperty employeeNameProperty() { return employeeName; }
    public StringProperty bulanProperty() { return bulan; }
    public DoubleProperty gajiPokokProperty() { return gajiPokok; }
    public DoubleProperty potonganProperty() { return potongan; }
    public DoubleProperty totalGajiProperty() { return totalGaji; }
    public StringProperty statusProperty() { return status; }
}