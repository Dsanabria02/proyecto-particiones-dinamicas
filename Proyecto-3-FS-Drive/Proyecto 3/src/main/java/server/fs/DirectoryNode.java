package server.fs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DirectoryNode extends Node {
    /* Clase que representa un directorio en un file system */

    private List<Node> children;

    // Constructor vacío para que Gson pueda deserializar
    public DirectoryNode() {
        super("");
        this.children = new ArrayList<>();
    }

    // Constructor normal
    public DirectoryNode(String name) {
        super(name);
        this.children = new ArrayList<>();
    }

    // Métodos
    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public Node getChild(String name) {
        return children.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void addChild(Node child) {
        if (child != null) {
            children.add(child);
            child.setParent(this); // Establecer la referencia al padre
            updateSize(); // Actualizar el tamaño del directorio
        }
    }

    public void removeChild(String name) {
        Node childToRemove = getChild(name);
        if (childToRemove != null) {
            children.removeIf(c -> c.getName().equals(name));
            childToRemove.setParent(null);
            updateSize();
        }
    }

    // Actualizar el tamaño del directorio basado en sus hijos
    private void updateSize() {
        int totalSize = children.stream()
                .mapToInt(Node::getSize)
                .sum();
        setSize(totalSize);
        setModified(LocalDateTime.now());
    }

    // Método para obtener el directorio padre de este nodo
    public DirectoryNode getParent() {
        Node parent = super.getParent();
        if (parent instanceof DirectoryNode) {
            return (DirectoryNode) parent;
        }
        return null;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    // Alias para que en JSON aparezca como "createdAt"
    public LocalDateTime getCreatedAt() {
        return this.created;
    }

    public void setCreatedAt(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModifiedAt() {
        return this.modified;
    }

    public void setModifiedAt(LocalDateTime modified) {
        this.modified = modified;
    }

    // Método para verificar si el directorio está vacío
    public boolean isEmpty() {
        return children.isEmpty();
    }

    // Método para obtener la cantidad de elementos en el directorio
    public int getChildCount() {
        return children.size();
    }
}

