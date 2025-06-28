package server.fs;

public class FileNode extends Node {
    /* Clase que representa un archivo en el file system */
    
    // Atributos
    private String extension;
    private String content;

    // Constructor 
    public FileNode(String name, String extension, String content) {
        super(name);
        this.extension = extension;
        this.content = content;
        this.size = content.length();
    }

    // Métodos 

    // GETTERS
    public String getExtension() { 
        return extension; 
    }

    public String getContent() {
         return content; 
    }

    // SETTERS
    public void setContent(String content) {
        this.content = content;
        this.modified = java.time.LocalDateTime.now();
        this.size = content.length();
    }

    @Override
    public boolean isDirectory() { 
        return false; 
    }
}