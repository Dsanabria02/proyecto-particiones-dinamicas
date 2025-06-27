package server.user.users;

import server.fs.DirectoryNode;

public class User {
    private String username;
    private int maxSize;
    private DirectoryNode root;
    private DirectoryNode shared;
    private DirectoryNode currentDirectory;

    public User(String username, int maxSize) {
        this.username = username;
        this.maxSize = maxSize;
        this.root = new DirectoryNode("root");
        this.shared = new DirectoryNode("shared");
        this.currentDirectory = root;
    }

    public String getUsername() { return username; }
    public DirectoryNode getRoot() { return root; }
    public DirectoryNode getCurrentDirectory() { return currentDirectory; }
    public void setCurrentDirectory(DirectoryNode dir) { this.currentDirectory = dir; }
}