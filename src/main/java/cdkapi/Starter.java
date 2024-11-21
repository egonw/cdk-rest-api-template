package cdkapi;


import cdkapi.tomcat.EmbeddedTomcat;

import java.util.List;
import java.util.Locale;

public class Starter {

    private static final List<String> CONTEXTS = List.of(
            "classpath:/web-app-context.xml"
    );

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        setDefaultSystemProp("liquibase.changeLogLockWaitTimeInMinutes", "60");
        String springConfigLocations = String.join(" ", CONTEXTS);


        new EmbeddedTomcat(springConfigLocations).startServer();
    }

    private static void setDefaultSystemProp(String propName, String value) {
        System.setProperty(propName, System.getProperty(propName, value));
    }
}
