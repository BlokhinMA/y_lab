package ru.ylab.server.services;

import ru.ylab.server.models.User;

import java.util.List;
import java.util.Objects;

public class UserService {

    private final List<User> users;

    public UserService(List<User> users) {
        this.users = users;
    }

    public boolean create(User newUser) {
        for (User user : users)
            if (Objects.equals(newUser.getEmail(), user.getEmail()))
                return false;
        newUser.setBlocked(false);
        newUser.setRole("user");
        users.add(newUser);
        return true;
    }

    public boolean login(User loginUser) {
        for (User user : users)
            if (Objects.equals(loginUser.getEmail(), user.getEmail()) &&
                    Objects.equals(loginUser.getPassword(), user.getPassword()))
                return true;
        return false;
    }

    public void deleteByEmail(String email) {
        for (int i = 0; i < users.size(); i++)
            if (Objects.equals(email, users.get(i).getEmail())) {
                users.remove(i);
                return;
            }
    }

    public boolean blockByIndex(int index) {
        if (index < 0)
            return false;
        for (User user : users)
            if (users.get(index) == user) {
                user.setBlocked(true);
                return true;
            }
        return false;
    }

}
