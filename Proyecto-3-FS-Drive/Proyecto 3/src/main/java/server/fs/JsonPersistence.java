package server.fs;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;
import server.users.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class JsonPersistence {
    private static final String DATA_PATH = "data/usuarios.json";

    private final Gson gson;

    public JsonPersistence() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime
                                .parse(json.getAsString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(Node.class, new NodeAdapter())
                .create();
    }

    public Map<String, User> cargarTodosLosUsuarios() {
        try {
            Path path = Path.of(DATA_PATH);
            if (!Files.exists(path))
                return new HashMap<>();

            String json = Files.readString(path).trim();
            if (json.isEmpty())
                return new HashMap<>();

            Type tipo = new TypeToken<Map<String, User>>() {
            }.getType();
            return gson.fromJson(json, tipo);
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException("Error al cargar usuarios.json", e);
        }
    }

    public void saveUser(User user) {
        try {
            Map<String, User> usuarios = cargarTodosLosUsuarios();
            usuarios.put(user.getUsername(), user);

            Files.createDirectories(Path.of("data"));
            String json = gson.toJson(usuarios);
            Files.writeString(Path.of(DATA_PATH), json);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar usuario", e);
        }
    }

    public User loadUser(String username) {
        Map<String, User> usuarios = cargarTodosLosUsuarios();
        User user = usuarios.get(username);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado: " + username);
        }
        return user;
    }
}
