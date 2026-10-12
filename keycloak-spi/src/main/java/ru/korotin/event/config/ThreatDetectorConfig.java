package ru.korotin.event.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.keycloak.Config;
import org.keycloak.events.EventType;
import org.keycloak.utils.StringUtil;
import ru.korotin.kafka.JsonSerializer;
import ru.korotin.schema.UserEvent;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import static ru.korotin.event.config.ThreatDetectorConstant.*;

@RequiredArgsConstructor
public class ThreatDetectorConfig {
    private final Config.Scope config;
    private Producer<String, UserEvent> producer;

    public boolean isEnabled() {
        boolean enabled = getBooleanEntry(ENABLED);
        String bootstrapServers = getStringEntry(BOOTSTRAP_SERVERS);
        String topic = getStringEntry(TOPIC);

        return enabled && StringUtil.isNotBlank(bootstrapServers) && StringUtil.isNotBlank(topic);
    }

    public Producer<String, UserEvent> buildProducer() {
        if (producer == null) {
            Properties properties = getProducerProperties();
            producer = new KafkaProducer<>(properties);
        }

        return producer;
    }

    public String getTopic() {
        return getStringEntry(TOPIC);
    }

    public Set<EventType> getRecordedEvents() {
        return getSetEntry(RECORDED_EVENTS);
    }


    public void close() {
        if (producer != null) {
            producer.close();
        }
    }

    private Properties getProducerProperties() {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getStringEntry(BOOTSTRAP_SERVERS));
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        return producerProperties;
    }

    private String getStringEntry(Entry<String> entry) {
        return config.get(entry.name(), entry.defaultValue());
    }

    private boolean getBooleanEntry(Entry<Boolean> entry) {
        return config.getBoolean(entry.name(), entry.defaultValue());
    }

    private Set<EventType> getSetEntry(Entry<Set<EventType>> entry) {
        String[] values = config.getArray(entry.name());
        if (values == null || values.length == 0) {
            return entry.defaultValue();
        }
        Set<EventType> types = EnumSet.noneOf(EventType.class);
        Arrays.stream(values).map(EventType::valueOf).forEach(types::add);
        return types;
    }
}
