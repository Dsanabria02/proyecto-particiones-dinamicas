package server.fs;

import com.google.gson.*;
import org.springframework.stereotype.Service;
import server.users.User;

import java.io.*;
import java.nio.file.*;

@Service
public class JsonPersistence {
    private static final String DATA_PATH = "data/";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public User loadUser(String username) {
        try {
            Path path = Path.of(DATA_PATH + username + ".json");
            if (!Files.exists(path)) return new User(username, 10240);
            String json = Files.readString(path);
            return gson.fromJson(json, User.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUser(User user) {
        try {
            Files.createDirectories(Path.of(DATA_PATH));
            String json = gson.toJson(user);
            Files.writeString(Path.of(DATA_PATH + user.getUsername() + ".json"), json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}