package server.fs;
import java.time.LocalDateTime;

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
   
    // Setters
    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }
    
    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    protected void setSize(int size) {
        this.size = size;
    }
}