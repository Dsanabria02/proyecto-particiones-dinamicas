package server.users;

import org.springframework.stereotype.Service;
import server.fs.JsonPersistence;
import server.users.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserManager {
    private final Map<String, User> users = new HashMap<>();
    private final JsonPersistence persistence;

    public UserManager(JsonPersistence persistence) {
        this.persistence = persistence;
    }

    public User login(String username) {
        return users.computeIfAbsent(username, persistence::loadUser);
    }

    public void save(User user) {
        persistence.saveUser(user);
    }
}
