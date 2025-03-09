package ru.ylab.server.models;

public class User {

    private String name;
    private String email;
    private String password;
    private boolean isBlocked;
    private String role;

    public User() {
    }

    public User(String name, String email, String password, boolean isBlocked, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isBlocked = isBlocked;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isBlocked=" + isBlocked +
                ", role='" + role + '\'' +
                '}';
    }

}
