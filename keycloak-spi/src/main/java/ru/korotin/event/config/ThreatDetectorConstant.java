package ru.korotin.event.config;

import org.keycloak.events.EventType;

import java.util.EnumSet;
import java.util.Set;

public final class ThreatDetectorConstant {
    /**
     * --spi-events-listener--threat-detector--enabled
     */
    public static final Entry<Boolean> ENABLED = new Entry<>("enabled", false);

    /**
     * --spi-events-listener--threat-detector--bootstrap-servers
     */
    public static final Entry<String> BOOTSTRAP_SERVERS = new Entry<>("bootstrap-servers");

    /**
     * --spi-events-listener--threat-detector--topic
     */
    public static final Entry<String> TOPIC = new Entry<>("topic");

    /**
     * --spi-events-listener--threat-detector--recorded-events
     */
    public static final Entry<Set<EventType>> RECORDED_EVENTS = new Entry<>("recorded-events", EnumSet.of(EventType.LOGIN, EventType.LOGIN_ERROR));

    public record Entry<T>(String name, T defaultValue) {
        public Entry(String name) {
            this(name, null);
        }
    }
}
