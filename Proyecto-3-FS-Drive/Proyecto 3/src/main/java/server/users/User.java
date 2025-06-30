package server.users;

import server.fs.DirectoryNode;
import server.fs.Node;

public class User {
    private String username;
    private String password;
    private int maxSize;
    private DirectoryNode root;
    private DirectoryNode shared;
    private DirectoryNode currentDirectory;

    // Constructor vacío para que Gson pueda deserializar
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.maxSize = 10240;
        this.root = new DirectoryNode("root");
        this.shared = new DirectoryNode("shared");
        this.currentDirectory = root;
    }

    // Constructor adicional con maxSize como en la versión de tu compañera
    public User(String username, int maxSize) {
        this.username = username;
        this.maxSize = maxSize;
        this.root = new DirectoryNode("root");
        this.shared = new DirectoryNode("shared");
        this.currentDirectory = root;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public DirectoryNode getRootDirectory() {
        return root;
    }

    // Método adicional para obtener root como lo nombró tu compañera
    public DirectoryNode getRoot() {
        return root;
    }

    public void setRootDirectory(DirectoryNode root) {
        this.root = root;
    }

    public DirectoryNode getShared() {
        return shared;
    }

    public void setShared(DirectoryNode shared) {
        this.shared = shared;
    }

    public DirectoryNode getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(DirectoryNode currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public DirectoryNode getSharedDirectory() {
        return getShared();
    }

    public int getEspacioUtilizado() {
        return calcularEspacio(this.root);
    }

    private int calcularEspacio(DirectoryNode dir) {
        int total = 0;
        for (Node child : dir.getChildren()) {
            if (child.isDirectory()) {
                total += calcularEspacio((DirectoryNode) child); // recursivo
            } else {
                total += child.getSize(); // archivo
            }
        }
        return total;
    }
}
