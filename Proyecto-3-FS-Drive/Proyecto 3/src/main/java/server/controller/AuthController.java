package server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final String ARCHIVO_JSON = "data/usuarios.json";  // ✅ Ruta relativa a la raíz del proyecto
    private static final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String password = user.get("password");

        Map<String, String> usuarios = cargarUsuarios();
        if (usuarios.containsKey(username) && usuarios.get(username).equals(password)) {
            return "Login exitoso";
        } else {
            return "Credenciales incorrectas";
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String password = user.get("password");

        Map<String, String> usuarios = cargarUsuarios();
        if (usuarios.containsKey(username)) {
            return "Usuario ya existe";
        }

        usuarios.put(username, password);
        guardarUsuarios(usuarios);
        System.out.println("Usuario registrado: " + username);
        return "Registro exitoso";
    }

    private Map<String, String> cargarUsuarios() {
        try {
            File archivo = new File(ARCHIVO_JSON);
            if (!archivo.exists()) {
                return new HashMap<>();
            }
            return mapper.readValue(archivo, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void guardarUsuarios(Map<String, String> usuarios) {
        try {
            mapper.writeValue(new File(ARCHIVO_JSON), usuarios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
