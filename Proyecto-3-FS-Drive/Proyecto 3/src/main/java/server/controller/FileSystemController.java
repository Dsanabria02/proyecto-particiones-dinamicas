package server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.fs.FileManager;
import server.users.User;
import server.users.UserManager;
import java.util.List;
import server.fs.Node;
import java.util.Map;


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
        User user = users.getUser(req.username());
        fs.createFile(user, req.name(), req.extension(), req.content());
        users.save(user);
        return "Archivo creado";
    }

    @PostMapping("/create-directory")
    public String createDirectory(@RequestBody CreateDirectoryRequest req) {
        User user = users.getUser(req.username());
        fs.createDirectory(user, req.name());
        users.save(user);
        return "Directorio creado";
    }

    @PostMapping("/change-directory")
    public String changeDirectory(@RequestBody ChangeDirectoryRequest req) {
        User user = users.getUser(req.username());
        fs.changeDirectory(user, req.name());
        users.save(user);
        return "Directorio cambiado";
    }

    @GetMapping("/list")
    public String list(@RequestParam String username) {
        User user = users.getUser(username);
        return fs.listDirectory(user);
    }

    @GetMapping("/view-file")
    public String viewFile(@RequestParam String username, @RequestParam String name) {
        User user = users.getUser(username);
        try {
            return fs.viewFile(user, name);
        } catch (RuntimeException e) {
            return fs.viewFileFromShared(user, name);
        }
    }

    @PutMapping("/modify-file")
    public String modifyFile(@RequestBody ModifyFileRequest req) {
        User user = users.getUser(req.username());
        fs.modifyFile(user, req.name(), req.content());
        users.save(user);
        return "Archivo modificado";
    }

    @GetMapping("/properties")
    public String fileProperties(@RequestParam String username, @RequestParam String name) {
        User user = users.getUser(username);
        try {
            return fs.fileProperties(user, name);
        } catch (RuntimeException e) {
            return fs.filePropertiesFromShared(user, name);
        }
    }


    @PostMapping("/delete")
    public String delete(@RequestBody DeleteRequest req) {
        User user = users.getUser(req.username);
        try {
            fs.delete(user, req.name);
        } catch (RuntimeException e) {
            fs.deleteFromShared(user, req.name);
        }
        users.save(user);
        return "Eliminado correctamente";
    }

    @PostMapping("/copy")
    public String copy(@RequestBody MoveCopyRequest req) {
        User user = users.getUser(req.username());
        try {
            fs.copy(user, req.name(), req.targetFolder());
        } catch (RuntimeException e) {
            fs.copyFromShared(user, req.name(), req.targetFolder());
        }

        users.save(user);
        return "Copiado";
    }

    @PostMapping("/move")
    public String move(@RequestBody MoveCopyRequest req) {
        User user = users.getUser(req.username());
        try {
            fs.move(user, req.name(), req.targetFolder());
        } catch (RuntimeException e) {
            fs.moveFromShared(user, req.name(), req.targetFolder());
        }
        users.save(user);
        return "Movido";
    }


    @PostMapping("/share")
    public ResponseEntity<String> share(@RequestBody ShareRequest req) {
        User from = users.getUser(req.fromUser());
        User to = users.getUser(req.toUser());

        if (from == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario origen no existe");

        if (to == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario destino no existe");

        try {
            fs.share(from, req.name(), to);
            users.save(to);
            return ResponseEntity.ok("Compartido");
        } catch (Exception e) {
            System.out.println("Error al compartir: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo compartir el archivo.");
        }
    }

    @GetMapping("/shared")
    public String listShared(@RequestParam String username) {
        User user = users.getUser(username);
        return fs.listShared(user);
    }

    @GetMapping("/view-shared-file")
    public String viewSharedFile(@RequestParam String username, @RequestParam String name) {
        User user = users.getUser(username);
        return fs.viewSharedFile(user, name);
    }

    @PostMapping("/copy-to-folder")
    public ResponseEntity<?> copiarArchivo(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String name = body.get("name");
        String targetFolder = body.get("targetFolder");

        try {
            User user = users.getUser(username);
            try {
                fs.copy(user, name, targetFolder);
            } catch (RuntimeException e) {
                fs.copyFromShared(user, name, targetFolder);
            }
            users.save(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al copiar: " + e.getMessage());
        }
    }

    @GetMapping("/shared/changeDir")
    public ResponseEntity<String> cambiarDirectorioCompartido(@RequestParam String username, @RequestParam String name) {
        try {
            User user = users.getUser(username);
            String path = fs.changeToSharedDirectory(user, name); // Solo calcular el path sin modificar el estado del user
            return ResponseEntity.ok(path);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se pudo cambiar de directorio: " + e.getMessage());
        }
    }

    @PostMapping("/move-to-folder")
    public ResponseEntity<?> moverArchivo(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String name = body.get("name");
        String targetFolder = body.get("targetFolder");

        try {
            User user = users.getUser(username);
            try {
                fs.move(user, name, targetFolder);
            } catch (RuntimeException e) {
                fs.moveFromShared(user, name, targetFolder);
            }
            users.save(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al mover: " + e.getMessage());
        }
    }


    @GetMapping("/folders")
    public ResponseEntity<List<String>> listarCarpetasUsuario(@RequestParam String username) {
        try {
            User user = users.getUser(username);
            List<String> carpetas = fs.listFolders(user);
            return ResponseEntity.ok(carpetas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/exists")
    public boolean exists(@RequestParam String username, @RequestParam String name) {
        User user = users.getUser(username);
        return user.getCurrentDirectory().getChild(name) != null;
    }

    @GetMapping("/path")
    public String getCurrentPath(@RequestParam String username) {
        User user = users.getUser(username);
        return fs.getCurrentPath(user);
    }

    @GetMapping("/view-properties")
    public ResponseEntity<String> filePropertiesView(@RequestParam String username, @RequestParam String name) {
        try {
            User user = users.getUser(username);
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

    public static class DeleteRequest {
        public String username;
        public String name;
    }
}
