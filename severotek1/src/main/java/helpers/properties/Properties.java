package helpers.properties;

import helpers.properties.interfaces.MainProperties;
import org.aeonbits.owner.ConfigFactory;

/**
 * Класс, содержащий статическую ссылку для доступа к файлу .property.
 */
public class Properties {
    /**
     * Создаём конфигурационный класс на основе интерфейса MainProperties для доступа к переменным из файла .property.
     */
    public static MainProperties mainProperties = ConfigFactory.create(MainProperties.class);
}
