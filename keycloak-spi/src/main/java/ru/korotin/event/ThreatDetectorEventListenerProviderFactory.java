package ru.korotin.event;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import ru.korotin.event.config.ThreatDetectorConfig;

@Slf4j
public class ThreatDetectorEventListenerProviderFactory implements EventListenerProviderFactory {
    public static final String PROVIDER_ID = "threat-detector";

    private EventListenerProvider lazyInstance;
    private ThreatDetectorConfig config;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        if (lazyInstance == null) {
            log.trace("Creating Event listener instance");
            lazyInstance = getLazyInstance();
        }
        return lazyInstance;
    }

    private EventListenerProvider getLazyInstance() {
        if (config.isEnabled()) {
            return new ThreatDetectorEventListener(config);
        }

        log.warn("ThreadDetectorEventListener SPI is disabled. Falling back to no-op default listener");

        return new EventListenerProvider() {
            @Override
            public void onEvent(Event event) {
            }

            @Override
            public void onEvent(AdminEvent event, boolean includeRepresentation) {
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public void init(org.keycloak.Config.Scope scope) {
        log.trace("Initializing configuration");
        config = new ThreatDetectorConfig(scope);
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        log.trace("Post init");
    }

    @Override
    public void close() {
        log.trace("Closing configuration");
        config.close();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
