package server.fs;

import org.springframework.stereotype.Service;
import server.users.User;

import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.ArrayList;

@Service
public class FileManager {

    // ------------- CREAR ARCHIVOS ---------------------------

    public void createFile(User user, String name, String extension, String content) {
        // Crear archivo dentro del actual directorio
        DirectoryNode current = user.getCurrentDirectory();
        if (current.getChild(name) != null)
            throw new RuntimeException("Archivo ya existe");
        current.addChild(new FileNode(name, extension, content));
    }

    // Crear archivos con sobreescritura
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

    public void createDirectory(User user, String name) {
        // Crear un directorio dentro del actual directorio
        DirectoryNode current = user.getCurrentDirectory();
        if (current.getChild(name) != null)
            throw new RuntimeException("Directorio ya existe");
        current.addChild(new DirectoryNode(name));
    }

    // Crear directorios con sobreescritura
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

    public void changeDirectory(User user, String name) {
        System.out.println("Intentando cambiar de directorio a: " + name);
    
        if ("..".equals(name)) {
            DirectoryNode current = user.getCurrentDirectory();
            DirectoryNode parent = current.getParent();
            if (parent != null) {
                user.setCurrentDirectory(parent);
                System.out.println("Cambiado al directorio padre: " + parent.getName());
            } else {
                System.out.println("Ya estás en el directorio raíz, no se puede subir más.");
            }
        } else if ("root".equals(name)) {
            user.setCurrentDirectory(user.getRootDirectory());
            System.out.println("Cambiado al directorio raíz.");
        } else {
            Node next = user.getCurrentDirectory().getChild(name);
            if (next != null && next.isDirectory()) {
                user.setCurrentDirectory((DirectoryNode) next);
                System.out.println("Cambiado al directorio: " + next.getName());
            } else {
                System.out.println("Directorio no encontrado: " + name);
                throw new RuntimeException("Directorio no encontrado");
            }
        }

        System.out.println("Directorio actual: " + user.getCurrentDirectory().getName());
    }


    // Cambio de directorio con retorno de path (versión extendida)
    public String changeDirectoryWithPath(User user, String name) {
        Node next = user.getCurrentDirectory().getChild(name);
        if (next != null && next.isDirectory()) {
            user.setCurrentDirectory((DirectoryNode) next);
            return user.getCurrentDirectory().getPath(); // aquí usamos getPath()
        } else {
            throw new RuntimeException("Directorio no encontrado");
        }
    }

    public String changeToSharedDirectory(User user, String name) {
        Node next = user.getShared().getChild(name);
        if (next != null && next.isDirectory()) {
            user.setCurrentDirectory((DirectoryNode) next);
            return user.getCurrentDirectory().getPath();
        } else {
            throw new RuntimeException("Directorio no encontrado en compartidos");
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
        Node n = user.getCurrentDirectory().getChild(name);
        if (n instanceof FileNode file) {
            file.setContent(newContent);

            // ACTUALIZAR también el directorio contenedor
            DirectoryNode parent = user.getCurrentDirectory();
            parent.setModified(java.time.LocalDateTime.now());
        } else {
            throw new RuntimeException("No es un archivo");
        }
    }

    // --------------- VER PROPIEDADES -----------------------------------

    private String formatSize(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        double size = bytes / Math.pow(1024, exp);
        return String.format("%.1f %sB", size, unit);
    }

    public String fileProperties(User user, String name) {
        // Mostrar propiedades del archivo o directorio
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
                    file.getExtension());
        } else {
            return String.format(
                    "Nombre: %s\nTamaño: %s\nCreado: %s\nModificado: %s",
                    n.getName(),
                    formatSize(n.getSize()),
                    n.getCreated().format(formatter),
                    n.getModified().format(formatter));
        }
    }

    public void delete(User user, String name) {
        DirectoryNode currentDir = user.getCurrentDirectory();
        Node nodeToDelete = currentDir.getChild(name);

        if (nodeToDelete == null) {
            throw new IllegalArgumentException("Archivo o directorio no encontrado: " + name);
        }

        if (nodeToDelete.isDirectory()) {
            // Es un DirectoryNode - eliminar en cascada
            DirectoryNode dirToDelete = (DirectoryNode) nodeToDelete;
            deleteDirectoryRecursively(dirToDelete);
            user.getCurrentDirectory().removeChild(name);
        } else {
            // Es un FileNode - eliminación simple
            user.getCurrentDirectory().removeChild(name);
        }
    }

    /**
     * Método auxiliar para eliminar recursivamente el contenido de un directorio
     */
    private void deleteDirectoryRecursively(DirectoryNode directory) {
        // Crear copia para evitar ConcurrentModificationException
        List<Node> childrenCopy = new ArrayList<>(directory.getChildren());

        for (Node child : childrenCopy) {
            if (child.isDirectory()) {
                // Es un subdirectorio - llamada recursiva
                deleteDirectoryRecursively((DirectoryNode) child);
                directory.removeChild(child.getName());
            } else {
                // Es un archivo - eliminar directamente
                directory.removeChild(child.getName());
            }
        }
    }

    public void copy(User user, String name, String targetDirName) {
        Node original = user.getCurrentDirectory().getChild(name);
        if (original == null)
            throw new RuntimeException("Archivo a copiar no encontrado");

        DirectoryNode destination = findDirectoryByPath(user.getRootDirectory(), targetDirName);
        if (destination == null || !destination.isDirectory()) {
            throw new RuntimeException("El destino no es un directorio válido");
        }

        if (original instanceof FileNode file) {
            destination.addChild(new FileNode(file.getName(), file.getExtension(), file.getContent()));
        } else if (original instanceof DirectoryNode dir) {
            destination.addChild(cloneDirectory(dir));
        } else {
            throw new RuntimeException("Tipo de nodo no soportado");
        }
    }

    public void move(User user, String name, String targetDirName) {
        Node node = user.getCurrentDirectory().getChild(name);
        if (node == null)
            throw new RuntimeException("Archivo o directorio a mover no encontrado");

        DirectoryNode destination = findDirectoryByPath(user.getRootDirectory(), targetDirName);
        if (destination == null || !destination.isDirectory()) {
            throw new RuntimeException("El destino no es un directorio válido");
        }

        user.getCurrentDirectory().removeChild(name);
        destination.addChild(node);
    }

    public void share(User fromUser, String name, User toUser) {
        System.out.println("Intentando compartir archivo: " + name);
        System.out.println("Usuario origen: " + fromUser.getUsername());
        System.out.println("Directorio actual: " + fromUser.getCurrentDirectory().getName());

        Node n = fromUser.getCurrentDirectory().getChild(name);
        if (n == null) {
            System.out.println("No se encontró el archivo '" + name + "' en el directorio actual.");
            throw new RuntimeException("Archivo no encontrado.");
        }

        if (n == null) {
            System.out.println("Error: El archivo o directorio \"" + name
                    + "\" no existe en el directorio actual del usuario " + fromUser.getUsername());
            return;
        }

        if (n instanceof FileNode file) {
            toUser.getShared().addChild(new FileNode(file.getName(), file.getExtension(), file.getContent()));
            System.out.println(
                    "Archivo \"" + name + "\" compartido de " + fromUser.getUsername() + " a " + toUser.getUsername());
        } else if (n instanceof DirectoryNode dir) {
            toUser.getShared().addChild(cloneDirectory(dir));
            System.out.println("Directorio \"" + name + "\" compartido de " + fromUser.getUsername() + " a "
                    + toUser.getUsername());
        } else {
            System.out.println("Error: El nodo \"" + name + "\" no es un archivo ni un directorio válido.");
        }
    }

    public String listShared(User user) {
        DirectoryNode shared = user.getShared();
        StringBuilder sb = new StringBuilder();
        for (Node n : shared.getChildren()) {
            sb.append(n.isDirectory() ? "[DIR] " : "[FILE] ").append(n.getName()).append("\n");
        }
        return sb.toString();
    }

    public String viewSharedFile(User user, String name) {
        Node n = user.getShared().getChild(name);
        if (n instanceof FileNode file)
            return file.getContent();
        throw new RuntimeException("Archivo compartido no encontrado o inválido");
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

    public List<String> listFolders(User user) {
        List<String> folders = new ArrayList<>();
        collectFolderPaths(user.getRootDirectory(), "", folders);
        return folders;
    }

    private void collectFolderPaths(DirectoryNode node, String path, List<String> folders) {
        String currentPath = path.isEmpty() ? node.getName() : path + "/" + node.getName();
        folders.add(currentPath);
        for (Node child : node.getChildren()) {
            if (child instanceof DirectoryNode) {
                collectFolderPaths((DirectoryNode) child, currentPath, folders);
            }
        }
    }

    public DirectoryNode findDirectoryByPath(DirectoryNode root, String path) {
        if (path == null || path.isEmpty() || path.equals("root")) {
            return root;
        }

        String[] parts = path.split("/");
        DirectoryNode current = root;

        for (String part : parts) {
            if (part.equals("root") || part.isBlank())
                continue;

            Node nextNode = current.getChild(part);
            if (nextNode == null || !nextNode.isDirectory()) {
                return null; // No existe o no es directorio
            }
            current = (DirectoryNode) nextNode;
        }

        return current;
    }

    private Node cloneNode(Node original) {
        if (original instanceof DirectoryNode) {
            DirectoryNode clonedDir = new DirectoryNode(original.getName());
            for (Node child : ((DirectoryNode) original).getChildren()) {
                clonedDir.addChild(cloneNode(child));
            }
            return clonedDir;
        } else if (original instanceof FileNode file) {
            return new FileNode(file.getName(), file.getExtension(), file.getContent());
        }
        return null;
    }

    private void collectFolders(DirectoryNode node, List<String> folders) {
        folders.add(node.getName());
        for (Node child : node.getChildren()) {
            if (child instanceof DirectoryNode) {
                collectFolders((DirectoryNode) child, folders);
            }
        }
    }

    private String generateUniqueName(DirectoryNode dir, String baseName) {
        String name = baseName;
        int counter = 1;
        while (dir.getChild(name) != null) {
            name = baseName + " (" + counter + ")";
            counter++;
        }
        return name;
    }


    public String viewFileFromShared(User user, String name) {
        Node n = user.getSharedDirectory().getChild(name);
        if (n instanceof FileNode f)
            return f.getContent();
        throw new RuntimeException("Archivo compartido no encontrado");
    }

    public String filePropertiesFromShared(User user, String name) {
        Node n = user.getSharedDirectory().getChild(name);
        if (n == null) throw new RuntimeException("No encontrado");
        return n.getProperties();
    }

    public void deleteFromShared(User user, String name) {
        user.getSharedDirectory().removeChild(name);
    }

    public void copyFromShared(User user, String name, String targetFolder) {
        Node n = user.getSharedDirectory().getChild(name);
        if (n == null) throw new RuntimeException("No encontrado");
        copyNodeTo(n, user, targetFolder);
    }

    public void moveFromShared(User user, String name, String targetFolder) {
        Node n = user.getSharedDirectory().getChild(name);
        if (n == null) throw new RuntimeException("No encontrado");
        copyNodeTo(n, user, targetFolder);
        deleteFromShared(user, name);
    }

    private void copyNodeTo(Node n, User user, String targetFolder) {
        DirectoryNode target = findDirectoryByPath(user.getRootDirectory(), targetFolder);
        if (target == null)
            throw new RuntimeException("Directorio destino no encontrado");

        Node copy = cloneNode(n);
        target.addChild(copy);
    }


}
