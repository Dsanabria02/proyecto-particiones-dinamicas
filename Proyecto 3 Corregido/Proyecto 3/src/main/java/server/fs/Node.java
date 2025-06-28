package server.fs;

import java.time.LocalDateTime;

public abstract class Node {
     /* Clase base de un archivo o directorio */
    
     // Atributos
    protected String name;
    protected LocalDateTime created;
    protected LocalDateTime modified;
    protected int size;

    // Constructor
    public Node(String name) {
        this.name = name;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    // Métodos

    public abstract boolean isDirectory();
    
    // ------ GETTERS AND SETTERS ----------------------------

    // Getters
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
    
    // Setters
    public void setModified(LocalDateTime modified) {
         this.modified = modified; 
    }
}
