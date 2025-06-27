package server.fs;

import java.util.*;

public class DirectoryNode extends Node {
    /* Clase que representa un directorio en un file system */

    // Atributos
    private List<Node> children = new ArrayList<>();

    // Contructor
    public DirectoryNode(String name) {
        super(name);
    }

    // Métodos
    
    public List<Node> getChildren() { 
        // Obtener todos los archivos del directorio
        return children; 
    }

    public Node getChild(String name) {
        // Obtener un archivo del directorio
        return children.stream().filter(n -> n.getName().equals(name)).findFirst().orElse(null);
    }

    public void addChild(Node node) {
        // Agregar archivo al directorio
        children.add(node);
    }

    public void removeChild(String name) {
        //Eliminar archivo al directorio
        children.removeIf(n -> n.getName().equals(name));
    }

    @Override
    public boolean isDirectory() { 
        // Indica que sí es un directorio
        return true; 
    }
}
