package ru.korotin.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import ru.korotin.event.config.ThreatDetectorConfig;
import ru.korotin.schema.UserEvent;

import java.util.EnumSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class ThreatDetectorEventListener implements EventListenerProvider {
    private static final Set<EventType> RECORDED_EVENTS = EnumSet.of(EventType.LOGIN_ERROR, EventType.LOGIN);

    private final ThreatDetectorConfig config;

    @Override
    public void onEvent(Event event) {
        log.trace("Received event: {}. Realm: {}", event.getType(), event.getRealmId());
        if (!RECORDED_EVENTS.contains(event.getType())) {
            log.trace("Ignoring event: {}", event.getType());
            return;
        }
        var producer = config.buildProducer();
        var topic = config.getTopic();
        var key = event.getIpAddress();
        log.trace("Trying to send event to Kafka topic: {}", topic);
        producer.send(new ProducerRecord<>(topic, key, mapToUserEvent(event)), (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to send event", exception);
            } else {
                log.debug("Event sent. Topic={}, partition={}, offset={}", metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

    private UserEvent mapToUserEvent(Event event) {
        return new UserEvent(
                event.getType().name(),
                event.getUserId(),
                event.getRealmName(),
                event.getSessionId(),
                event.getIpAddress(),
                event.getError(),
                event.getTime()
        );
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        log.trace("Received AdminEvent. No op");
    }

    @Override
    public void close() {
        log.trace("Publisher closed");
    }
}
