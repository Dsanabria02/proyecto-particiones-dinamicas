package server.controller;

public record ShareRequest(String fromUser, String toUser, String name) {
}