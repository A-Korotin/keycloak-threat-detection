package ru.korotin.schema;

public record UserEvent(
        String type,
        String userId,
        String realmName,
        String sessionId,
        String ipAddress,
        String error,
        long time) {
}
