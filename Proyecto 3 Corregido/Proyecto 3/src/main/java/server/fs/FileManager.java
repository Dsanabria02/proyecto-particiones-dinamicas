package server.fs;

import org.springframework.stereotype.Service;
import server.users.User;

@Service
public class FileManager {

    public void createFile(User user, String name, String extension, String content) {
        // Crear archivo dentro del actual directorio
        DirectoryNode current = user.getCurrentDirectory();
        if (current.getChild(name) != null)
            throw new RuntimeException("Archivo ya existe");
        current.addChild(new FileNode(name, extension, content));
    }

    public void createDirectory(User user, String name) {
        // Crear un directorio dentro del actual directorio
        DirectoryNode current = user.getCurrentDirectory();
        if (current.getChild(name) != null)
            throw new RuntimeException("Directorio ya existe");
        current.addChild(new DirectoryNode(name));
    }

    public void changeDirectory(User user, String name) {
        // Cambiarse a otro directorio
        Node next = user.getCurrentDirectory().getChild(name);
        if (next != null && next.isDirectory())
            user.setCurrentDirectory((DirectoryNode) next);
        else
            throw new RuntimeException("Directorio no encontrado");
    }

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

    public String fileProperties(User user, String name) {
        // Mostrar propiedades del archivo o directorio
        Node n = user.getCurrentDirectory().getChild(name);
        if (n == null)
            throw new RuntimeException("Archivo o directorio no encontrado");
        if (n instanceof FileNode file) {
            return String.format(
                    "Nombre: %s\nTamaño: %d\nCreado: %s\nModificado: %s\nExtensión: %s",
                    file.getName(), file.getSize(), file.getCreated(), file.getModified(), file.getExtension());
        } else {
            return String.format(
                    "Nombre: %s\nTamaño: %d\nCreado: %s\nModificado: %s",
                    n.getName(), n.getSize(), n.getCreated(), n.getModified());
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