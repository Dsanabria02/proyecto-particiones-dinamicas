package server.fs;
import java.util.*;

public class DirectoryNode extends Node {
    /* Clase que representa un directorio en un file system */
    
    // Atributos
    private List<Node> children = new ArrayList<>();
    
    // Constructor
    public DirectoryNode(String name) {
        super(name);
    }
    
    // Métodos
    public List<Node> getChildren() {
        return children;
    }
    
    public Node getChild(String name) {
        return children.stream()
                .filter(n -> n.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    public void addChild(Node node) {
        if (node != null) {
            children.add(node);
            node.setParent(this); // Establecer la referencia al padre
            updateSize(); // Actualizar el tamaño del directorio
        }
    }
    
    public void removeChild(String name) {
        Node childToRemove = getChild(name);
        if (childToRemove != null) {
            children.removeIf(n -> n.getName().equals(name));
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
        setModified(java.time.LocalDateTime.now());
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
    
    // Método para verificar si el directorio está vacío
    public boolean isEmpty() {
        return children.isEmpty();
    }
    
    // Método para obtener la cantidad de elementos en el directorio
    public int getChildCount() {
        return children.size();
    }
}
