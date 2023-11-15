package com.junit.service;

import com.junit.dto.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class UserService {
    private final List<User> users = new ArrayList<>();
    public List<User> getAll() {
        return users;
    }
    public boolean add(User user){
        return users.add(user);
    }
    public void add(User...users){
        this.users.addAll(Arrays.asList(users));
    }
    public Optional<User> login(String userName, String password) {
        return users.stream()
                .filter(user -> user.getUserName().equals(userName))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream()
                .collect(toMap(User::getId, Function.identity()));
    }
}
