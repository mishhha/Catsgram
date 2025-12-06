package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@Service
public class UserService {
    Map<Long, User> users = new HashMap<>();

    public Optional<User> findUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public User getUserById(long idUser) {
        User user = users.get(idUser);
        if(user == null) {
            throw new ConditionsNotMetException("Пользователь с id: " + idUser + " не найден");
        }
        return user;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User addUser(User newUser) {
        if(newUser.getEmail() == null) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        String email = newUser.getEmail();
        Optional<User> searchUser = users.values().stream()
            .filter(user -> user.getEmail().equals(email))
            .findFirst();
        if(searchUser.isPresent()) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        User savedUser = new User();
        savedUser.setEmail(newUser.getEmail());
        savedUser.setPassword(newUser.getPassword());
        savedUser.setUsername(newUser.getUsername());
        savedUser.setId(getNextId());
        savedUser.setRegistrationDate(Instant.now());

        users.put(savedUser.getId(), savedUser);
        return savedUser;
    }

    public User updateUser(User updateUser) {
        if(updateUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User oldUser = users.get(updateUser.getId());
        User newUser = new User();

        if(updateUser.getEmail() == null && updateUser.getUsername() == null && updateUser.getPassword() == null) {
            newUser.setId(updateUser.getId());
            newUser.setUsername(oldUser.getUsername());
            newUser.setEmail(oldUser.getEmail());
            newUser.setPassword(oldUser.getPassword());
            newUser.setRegistrationDate(oldUser.getRegistrationDate());

            users.put(newUser.getId(),newUser);
            return newUser;
        }

        if(updateUser.getEmail() != null) {
            if (!updateUser.getEmail().equals(oldUser.getEmail())) {
                Optional<User> searchUser = users.values().stream()
                    .filter(user -> user.getEmail().equals(updateUser.getEmail()))
                    .findFirst();
                if (searchUser.isPresent()) {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                }
            }
        }

        newUser.setId(updateUser.getId());

        if(updateUser.getUsername() == null) {
            newUser.setUsername(oldUser.getUsername());
        }
        if(updateUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }
        if(updateUser.getPassword() == null) {
            newUser.setPassword(oldUser.getPassword());
        }

        newUser.setRegistrationDate(oldUser.getRegistrationDate());

        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public Long getNextId() {
        long currentMaxId = users.keySet().stream()
            .mapToLong(id -> id)
            .max()
            .orElse(0);
        return ++currentMaxId;
    }
}
