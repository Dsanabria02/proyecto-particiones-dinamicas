package server.controller;

public record CreateFileRequest(
    String username,
    String name,
    String extension,
    String content,
    boolean overwrite
) {}

