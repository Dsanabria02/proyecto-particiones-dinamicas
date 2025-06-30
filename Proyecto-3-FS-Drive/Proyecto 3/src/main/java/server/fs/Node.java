package server.fs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    /* Clase base de un archivo o directorio */

    // Atributos
    protected String name;
    protected LocalDateTime created;
    protected LocalDateTime modified;
    protected int size;
    protected Node parent; // Agregar referencia al padre

    // Constructor
    public Node(String name) {
        this.name = name;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.size = 0;
        this.parent = null;
    }

    // Métodos
    public abstract boolean isDirectory();

    // ------ GETTERS AND SETTERS ----------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public Node getParent() {
        return parent;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    protected void setSize(int size) {
        this.size = size;
    }

    public String getPath() {
        StringBuilder pathBuilder = new StringBuilder();
        List<String> pathSegments = new ArrayList<>();

        Node current = this; // Empezamos desde el nodo actual

        // Recorremos hacia arriba hasta que no haya más padres
        while (current != null) {
            pathSegments.add(0, current.getName()); // Agregamos el nombre al principio de la lista
            current = current.getParent(); // Subimos al padre
        }

        // Si el primer segmento es el nombre de la raíz (e.g., "root"), y quieres que
        // sea "/",
        // puedes manejarlo aquí. Para un sistema tipo Drive, "root" o "Mi Unidad" es
        // común.
        if (!pathSegments.isEmpty()) {
            // Asumiendo que el primer elemento es el nombre de la raíz.
            // Si quieres que la ruta inicie con "/" para la raíz, podrías hacer algo así:
            if (pathSegments.get(0).equals("root")) { // O el nombre que le des a tu directorio raíz
                pathBuilder.append("/");
                for (int i = 1; i < pathSegments.size(); i++) {
                    pathBuilder.append(pathSegments.get(i));
                    if (i < pathSegments.size() - 1) {
                        pathBuilder.append("/");
                    }
                }
            } else { // Para subdirectorios o si la raíz no es especial
                for (int i = 0; i < pathSegments.size(); i++) {
                    pathBuilder.append(pathSegments.get(i));
                    if (i < pathSegments.size() - 1) {
                        pathBuilder.append("/");
                    }
                }
            }
        }

        // Manejar el caso de un solo nodo raíz que debería ser "/"
        if (pathSegments.size() == 1 && pathSegments.get(0).equals("root")) {
            return "/";
        }

        return pathBuilder.toString();
    }

    public String getProperties() {
        return "Nombre: " + name + "\n" +
                "Tamaño: " + size + " bytes\n" +
                "Creado: " + created + "\n" +
                "Modificado: " + modified + "\n" +
                "Ruta: " + getPath();
    }

}
