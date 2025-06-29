package server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.users.User;
import server.fs.JsonPersistence;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JsonPersistence jsonPersistence;

    private static final String ARCHIVO_JSON_SIMPLE = "data/usuarios.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String password = userData.get("password");

        Map<String, User> usuarios = jsonPersistence.cargarTodosLosUsuarios();
        if (usuarios.containsKey(username) && usuarios.get(username).getPassword().equals(password)) {
            return "Login exitoso";
        } else {
            return "Credenciales incorrectas";
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String password = userData.get("password");

        Map<String, User> usuarios = jsonPersistence.cargarTodosLosUsuarios();
        if (usuarios.containsKey(username)) {
            return "Usuario ya existe";
        }

        User nuevo = new User(username, password);
        jsonPersistence.saveUser(nuevo);
        System.out.println("Usuario registrado: " + username);
        return "Registro exitoso";
    }

    @PostMapping("/register-simple")
    public String registerSimple(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String password = user.get("password");

        Map<String, String> usuarios = cargarUsuariosSimples();
        if (usuarios.containsKey(username)) {
            return "Usuario ya existe";
        }

        usuarios.put(username, password);
        guardarUsuariosSimples(usuarios);
        return "Registro simple exitoso";
    }

    @GetMapping("/usuarios-simples")
    public Map<String, String> getUsuariosSimples() {
        return cargarUsuariosSimples();
    }

    private Map<String, String> cargarUsuariosSimples() {
        try {
            File archivo = new File(ARCHIVO_JSON_SIMPLE);
            if (!archivo.exists()) {
                return new HashMap<>();
            }
            return mapper.readValue(archivo, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void guardarUsuariosSimples(Map<String, String> usuarios) {
        try {
            mapper.writeValue(new File(ARCHIVO_JSON_SIMPLE), usuarios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

