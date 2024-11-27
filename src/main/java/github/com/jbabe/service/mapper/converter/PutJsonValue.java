package github.com.jbabe.service.mapper.converter;

import com.fasterxml.jackson.annotation.JsonValue;

public interface PutJsonValue {
    @JsonValue
    String getValue();
    static <T extends PutJsonValue> T fromValue(Class<T> enumType, String value) {
        for (T t : enumType.getEnumConstants()) {
            if (t.getValue().equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
