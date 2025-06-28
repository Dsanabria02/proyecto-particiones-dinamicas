package server.fs;

import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import server.users.User;

@Service
public class FileManager {
    
    // ------------- CREAR ARCHIVOS ---------------------------

    // Crear archivos
    public void createFile(User user, String name, String extension, String content, boolean overwrite) {
        DirectoryNode current = user.getCurrentDirectory();
        Node existing = current.getChild(name);
        if (existing != null) {
            if (!overwrite) {
                throw new RuntimeException("Archivo ya existe");
            } else {
                current.removeChild(name); // Elimina el archivo anterior
            }
        }
        current.addChild(new FileNode(name, extension, content));
    }

    // Crear directorios
    public void createDirectory(User user, String name, boolean overwrite) {
        DirectoryNode current = user.getCurrentDirectory();
        Node existing = current.getChild(name);
        if (existing != null) {
            if (!overwrite) {
                throw new RuntimeException("Directorio ya existe");
            } else {
                current.removeChild(name); // Elimina el directorio anterior (¡CUIDADO con el contenido!)
            }
        }
        current.addChild(new DirectoryNode(name));
    }


    // -----------------------------------------------------------------------------


    // --------- NAVEGAR DIRECTORIOS ---------------------------------

    // Cambio de directorio
    public String changeDirectory(User user, String name) {
        Node next = user.getCurrentDirectory().getChild(name);
        if (next != null && next.isDirectory()) {
            user.setCurrentDirectory((DirectoryNode) next);
            return user.getCurrentDirectory().getPath(); // ✅ aquí usamos getPath()
        } else {
            throw new RuntimeException("Directorio no encontrado");
        }
    }

    // Obtener la ruta actual
    public String getCurrentPath(User user) {
        return user.getCurrentDirectory().getPath();
    }

    // --------------------------------------------------------------------------------


    public String listDirectory(User user) {
        // Mostrar lista de directorios
        StringBuilder sb = new StringBuilder();
        for (Node n : user.getCurrentDirectory().getChildren()) {
            sb.append(n.isDirectory() ? "[DIR] " : "[FILE] ").append(n.getName()).append("\n");
        }
        return sb.toString();
    }

    public String viewFile(User user, String name) {
        // Mostrar contenido de un archivo en particular
        Node n = user.getCurrentDirectory().getChild(name);
        if (n instanceof FileNode file)
            return file.getContent();
        throw new RuntimeException("Archivo no encontrado o inválido");
    }

    public void modifyFile(User user, String name, String newContent) {
        // Modificar archivo
        Node n = user.getCurrentDirectory().getChild(name);
        if (n instanceof FileNode file)
            file.setContent(newContent);
        else
            throw new RuntimeException("No es un archivo");
    }

    // --------------- VER PROPIEDADES -----------------------------------
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        double size = bytes / Math.pow(1024, exp);
        return String.format("%.1f %sB", size, unit);
    }

    public String fileProperties(User user, String name) {
        Node n = user.getCurrentDirectory().getChild(name);
        if (n == null)
            throw new RuntimeException("Archivo o directorio no encontrado");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        if (n instanceof FileNode file) {
            return String.format(
                "Nombre: %s\nTamaño: %s\nCreado: %s\nModificado: %s\nExtensión: %s",
                file.getName(),
                formatSize(file.getSize()),
                file.getCreated().format(formatter),
                file.getModified().format(formatter),
                file.getExtension()
            );
        } else {
            return String.format(
                "Nombre: %s\nTamaño: %s\nCreado: %s\nModificado: %s",
                n.getName(),
                formatSize(n.getSize()),
                n.getCreated().format(formatter),
                n.getModified().format(formatter)
            );
        }
    }

    public void delete(User user, String name) {
        // Eliminar archivo del actual directorio
        user.getCurrentDirectory().removeChild(name);
    }

    public void copy(User user, String name, String targetDirName) {
        // Copiar archivo
        Node original = user.getCurrentDirectory().getChild(name);
        DirectoryNode target = (DirectoryNode) user.getCurrentDirectory().getChild(targetDirName);
        if (original != null && target != null && target.isDirectory()) {
            if (original instanceof FileNode file) {
                target.addChild(new FileNode(file.getName(), file.getExtension(), file.getContent()));
            }
        } else {
            throw new RuntimeException("Archivo o directorio destino inválido");
        }
    }

    public void move(User user, String name, String targetDirName) {
        // Mover archivo
        Node node = user.getCurrentDirectory().getChild(name);
        DirectoryNode target = (DirectoryNode) user.getCurrentDirectory().getChild(targetDirName);
        if (node != null && target != null && target.isDirectory()) {
            user.getCurrentDirectory().removeChild(name);
            target.addChild(node);
        } else {
            throw new RuntimeException("Movimiento inválido");
        }
    }

    public void share(User fromUser, String name, User toUser) {
        // Compartir archivo o directorio
        Node n = fromUser.getCurrentDirectory().getChild(name);
        if (n instanceof FileNode file) {
            toUser.getShared().addChild(new FileNode(file.getName(), file.getExtension(), file.getContent()));
        } else if (n instanceof DirectoryNode dir) {
            toUser.getShared().addChild(cloneDirectory(dir));
        } else {
            throw new RuntimeException("Archivo o directorio no encontrado");
        }
    }

    private DirectoryNode cloneDirectory(DirectoryNode dir) {
        DirectoryNode copy = new DirectoryNode(dir.getName());
        for (Node child : dir.getChildren()) {
            if (child instanceof FileNode file) {
                copy.addChild(new FileNode(file.getName(), file.getExtension(), file.getContent()));
            } else if (child instanceof DirectoryNode subDir) {
                copy.addChild(cloneDirectory(subDir));
            }
        }
        return copy;
    }
}