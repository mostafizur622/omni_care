package com.srapp.Model;

public class App_Inbox {
    String greetings,description,senderName,senderAddress,date;

    public App_Inbox(String greetings, String description, String senderName, String senderAddress, String date) {
        this.greetings = greetings;
        this.description = description;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.date = date;
    }

    public String getGreetings() {
        return greetings;
    }

    public void setGreetings(String greetings) {
        this.greetings = greetings;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
