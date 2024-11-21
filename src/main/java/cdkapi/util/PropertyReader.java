package cdkapi.util;

/**
 * Utility class that helps read both System and ENV properties. At the first it looks into System properties and if
 * property was not found it goes to the ENV properties. In case if property has been defined nowhere, it falls back
 * to the default value, passed by user.
 */
public class PropertyReader {
    private String value;

    private PropertyReader(String value) {
        this.value = value;
    }

    public int intValueOr(int defaultValue){
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public String stringValueOr(String defaultValue){
        return value != null ? value : defaultValue;
    }
    public static PropertyReader readProperty(String propertyName){
        return findProperty(propertyName);
    }

    private static PropertyReader findProperty(String propertyName){
        String systemProperty = System.getProperty(propertyName);
        if (systemProperty != null) return new PropertyReader(systemProperty);
        String envProperty = System.getenv(propertyName);
        if (envProperty != null) return new PropertyReader(envProperty);
        return new PropertyReader(null);
    }
}
