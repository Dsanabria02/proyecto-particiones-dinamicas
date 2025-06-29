package server.users;

import org.springframework.stereotype.Service;
import server.fs.DirectoryNode;
import server.fs.JsonPersistence;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserManager {
    private final Map<String, User> users = new HashMap<>();
    private final JsonPersistence persistence;

    public UserManager(JsonPersistence persistence) {
        this.persistence = persistence;
        cargarUsuariosDesdeJSON();
    }

    private void cargarUsuariosDesdeJSON() {
        Map<String, User> datos = persistence.cargarTodosLosUsuarios();

        if (datos == null || datos.isEmpty()) {
            System.out.println("No se encontraron usuarios en el archivo JSON.");
            return;
        }

        for (Map.Entry<String, User> entry : datos.entrySet()) {
            String username = entry.getKey();
            User u = entry.getValue();

            if (u == null || u.getPassword() == null || u.getRootDirectory() == null) {
                System.out.println("Datos incompletos para el usuario: " + username);
                continue;
            }

            u.setCurrentDirectory(u.getRootDirectory());
            users.put(username, u);
        }
    }

    public User login(String username) {
        // Versión combinada: intenta usar el usuario en memoria, si no, lo carga
        return users.computeIfAbsent(username, name -> {
            User u = persistence.loadUser(name);
            if (u == null || u.getRootDirectory() == null) {
                throw new RuntimeException("Usuario no válido o datos incompletos: " + name);
            }
            u.setCurrentDirectory(u.getRootDirectory());
            return u;
        });
    }

    public boolean registrar(String username, String password) {
        if (users.containsKey(username))
            return false;

        User nuevo = new User(username, password);

        // Crear estructura de drive con raíz SIN carpeta "compartidos"
        DirectoryNode root = new DirectoryNode("root");

        nuevo.setRootDirectory(root);
        nuevo.setCurrentDirectory(root);

        users.put(username, nuevo);
        persistence.saveUser(nuevo);
        return true;
    }

    public void save(User user) {
        if (user != null) {
            persistence.saveUser(user);
        }
    }

    public boolean existe(String username) {
        return users.containsKey(username);
    }

    public User getUser(String username) {
        if (users.containsKey(username)) {
            return users.get(username);
        }

        User u = persistence.loadUser(username);

        if (u == null || u.getPassword() == null || u.getRootDirectory() == null) {
            throw new RuntimeException("Usuario no válido o datos incompletos: " + username);
        }

        u.setCurrentDirectory(u.getRootDirectory());
        users.put(username, u);
        return u;
    }}
