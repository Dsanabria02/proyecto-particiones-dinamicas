package server.controller;

public record CreateDirectoryRequest(
    String username,
    String name,
    boolean overwrite
) {}
