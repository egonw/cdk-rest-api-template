package cdkapi.tomcat;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cdkapi.tomcat.TomcatServerProperties.DAEMON;
import static cdkapi.tomcat.TomcatServerProperties.THREAD_PRIORITY;
import static java.util.Collections.emptyMap;

/**
 * @author Oleg Tkachenko
 */
public class EmbeddedTomcat {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedTomcat.class);
    private final Tomcat tomcat;
    private final Context context;

    public EmbeddedTomcat(String springConfigLocation) {
        this(springConfigLocation, TomcatServerProperties.fromSystemProperties());
    }

    public EmbeddedTomcat(String springConfigLocation, TomcatServerProperties properties) {
        tomcat = new Tomcat();
        tomcat.setBaseDir(properties.getBaseDirPath());
        context = tomcat.addContext(properties.getContextPath(), null);
        context.setSessionTimeout(properties.getSessionTimeoutMin());
        tomcat.setConnector(createConnector(properties));
        addSpringContext(springConfigLocation);

        addFilter(CharacterEncodingFilter.class, "encodingFilter", Map.of("encoding", "UTF-8"));
        addFilter(ForwardedHeaderFilter.class, "forwardedHeaderFilter", emptyMap());
    }

    public void startServer() {
        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException ex) {
            LOGGER.error("Can't start Tomcat", ex);
            throw new RuntimeException(ex);
        }
    }

    private void addSpringContext(String contextConfigLocation) {
        Context tomcatContext = this.context;
        Container springContext = tomcatContext.findChild("spring-dispatcher");
        if (springContext == null) {
            Wrapper spring = tomcatContext.createWrapper();
            spring.setName("spring-dispatcher");
            spring.setServletClass("org.springframework.web.servlet.DispatcherServlet");
            spring.addInitParameter("contextConfigLocation", contextConfigLocation);
            spring.setLoadOnStartup(1);
            tomcatContext.addChild(spring);
            tomcatContext.addServletMappingDecoded("/", "spring-dispatcher");
        }
    }


    private void addFilter(Class<?> filterClass, String filterName, Map<String, String> params) {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterClass(filterClass.getName());
        filterDef.setFilterName(filterName);
        for (Map.Entry<String, String> param : params.entrySet())
            filterDef.addInitParameter(param.getKey(), param.getValue());
        this.context.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterName);
        filterMap.addURLPattern("/*");// if we ever need something else, need to create a separate method w/ a param
        this.context.addFilterMap(filterMap);
    }



    private Connector createConnector(TomcatServerProperties properties) {
        Connector connector = new Connector();
        connector.setPort(properties.getPort());
        AbstractProtocol protocolHandler = (AbstractProtocol) connector.getProtocolHandler();
        protocolHandler.setExecutor(createExecutor(properties));
        connector.setParseBodyMethods("POST,PUT,PATCH,DELETE");
        return connector;
    }

    private Executor createExecutor(TomcatServerProperties properties) {
        TaskThreadFactory tf = new TaskThreadFactory(properties.getThreadPrefix(), DAEMON, THREAD_PRIORITY);
        return new ThreadPoolExecutor(properties.getMinThreads(), properties.getMaxThreads(),
                60, TimeUnit.SECONDS, new TaskQueue(), tf);
    }
}
