package github.com.jbabe.config.converter;

import jakarta.persistence.AttributeConverter;

import java.util.EnumSet;

public abstract class StringToEnumConverter<T extends Enum<T> & ConvertibleEnum> implements AttributeConverter<T, String> {


    private final Class<T> enumClass;

    public StringToEnumConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String convertToDatabaseColumn(T convertibleEnum) {
        return convertibleEnum.fromValue();
    }

    @Override
    public T convertToEntityAttribute(String myEnumName) {
        return myEnumName == null ? null : EnumValueToEnum(myEnumName, enumClass);
    }

    private T EnumValueToEnum(String myEnumName, Class<T> enumClass) {
        for (T convertibleEnum : EnumSet.allOf(enumClass)) {
            if (convertibleEnum.fromValue().equals(myEnumName)) return convertibleEnum;
        }
        return null;
    }
}
