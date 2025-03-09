package ru.ylab.server.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private String type;
    private double sum;
    private String category;
    private LocalDateTime dateTime;
    private String description;
    private String userEmail;

    public Transaction() {
    }

    public Transaction(String type, double sum, String category, LocalDateTime dateTime, String description,
                       String userEmail) {
        this.type = type;
        this.sum = sum;
        this.category = category;
        this.dateTime = dateTime;
        this.description = description;
        this.userEmail = userEmail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return String.format("Тип: %s\nСумма: %s\nКатегория: %s\nДата и время: %s\nОписание: %s",
                type, sum, category, dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy h:mm")), description);
    }

}
