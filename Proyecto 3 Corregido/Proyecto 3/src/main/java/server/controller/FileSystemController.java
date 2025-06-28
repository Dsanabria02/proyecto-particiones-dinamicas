package server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.fs.FileManager;
import server.users.User;
import server.users.UserManager;

@RestController
@RequestMapping("/api/fs")
public class FileSystemController {

    private final FileManager fs;
    private final UserManager users;

    public FileSystemController(FileManager fs, UserManager users) {
        this.fs = fs;
        this.users = users;
    }

    @PostMapping("/create-file")
    public String createFile(@RequestBody CreateFileRequest req) {
        User user = users.login(req.username());
        fs.createFile(user, req.name(), req.extension(), req.content());
        users.save(user);
        return "Archivo creado";
    }

    @PostMapping("/create-directory")
    public String createDirectory(@RequestBody CreateDirectoryRequest req) {
        User user = users.login(req.username());
        fs.createDirectory(user, req.name());
        users.save(user);
        return "Directorio creado";
    }

    @PostMapping("/change-directory")
    public String changeDirectory(@RequestBody ChangeDirectoryRequest req) {
        User user = users.login(req.username());
        fs.changeDirectory(user, req.name());
        users.save(user);
        return "Directorio cambiado";
    }

    @GetMapping("/list")
    public String list(@RequestParam String username) {
        User user = users.login(username);
        return fs.listDirectory(user);
    }

    @GetMapping("/view-file")
    public String viewFile(@RequestParam String username, @RequestParam String name) {
        User user = users.login(username);
        return fs.viewFile(user, name);
    }

    @PutMapping("/modify-file")
    public String modifyFile(@RequestBody ModifyFileRequest req) {
        User user = users.login(req.username());
        fs.modifyFile(user, req.name(), req.content());
        users.save(user);
        return "Archivo modificado";
    }

    @GetMapping("/view-properties")
    public ResponseEntity<String> fileProperties(@RequestParam String username, @RequestParam String name) {
    try {
        User user = users.login(username);
        String props = fs.fileProperties(user, name);
        return ResponseEntity.ok(props);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Archivo o directorio no encontrado: " + e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error inesperado: " + e.getMessage());
    }
}


    @PostMapping("/delete")
    public String delete(@RequestBody DeleteRequest req) {
        User user = users.login(req.username);
        fs.delete(user, req.name);
        users.save(user);
        return "Eliminado";
    }

    @PostMapping("/copy")
    public String copy(@RequestBody MoveCopyRequest req) {
        User user = users.login(req.username());
        fs.copy(user, req.name(), req.targetDir());
        users.save(user);
        return "Copiado";
    }

    @PostMapping("/move")
    public String move(@RequestBody MoveCopyRequest req) {
        User user = users.login(req.username());
        fs.move(user, req.name(), req.targetDir());
        users.save(user);
        return "Movido";
    }

    @PostMapping("/share")
    public String share(@RequestBody ShareRequest req) {
        User from = users.login(req.fromUser());
        User to = users.login(req.toUser());
        fs.share(from, req.name(), to);
        users.save(to);
        return "Compartido";
    }

    // Clase interna o externa si lo preferís (para mapear el body del delete)
    public static class DeleteRequest {
        public String username;
        public String name;
    }
}
