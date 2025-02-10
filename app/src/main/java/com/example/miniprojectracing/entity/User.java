package com.example.miniprojectracing.entity;

public class User {
    private String username;
    private String password;
    private String name;
    private double money;

    public User(String username, String password, String name, double money) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.money = money;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public double getMoney() {
        return money;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
