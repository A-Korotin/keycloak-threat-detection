package ru.korotin.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.keycloak.util.JsonSerialization;
import ru.korotin.schema.UserEvent;

import java.io.IOException;

@Slf4j
public class JsonSerializer implements Serializer<UserEvent> {
    @Override
    public byte[] serialize(String topic, UserEvent data) {
        if (data == null) {
            log.warn("Received null user event for serialization. Topic={}", topic);
            return null;
        }
        try {
            return JsonSerialization.writeValueAsBytes(data);
        } catch (IOException e) {
            throw new SerializationException("Error serializing user event",e);
        }
    }
}
