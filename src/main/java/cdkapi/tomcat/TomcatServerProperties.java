package cdkapi.tomcat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cdkapi.util.PropertyReader.readProperty;

/**
 * @author Oleg Tkachenko
 */
public class TomcatServerProperties {
    private static final Logger LOG = LoggerFactory.getLogger(TomcatServerProperties.class);
    final static boolean DAEMON = true;
    final static int THREAD_PRIORITY = Thread.NORM_PRIORITY;
    private int port;
    private int minThreads;
    private int maxThreads;
    private int sessionTimeoutMin;
    private String contextPath;
    private String baseDirPath;

    private TomcatServerProperties() { }

    public int getPort() { return port; }
    int getMinThreads() { return minThreads; }
    int getMaxThreads() { return maxThreads; }
    String getContextPath() { return contextPath; }
    String getBaseDirPath() { return baseDirPath; }
    String getThreadPrefix(){ return String.format("http-%s-", getPort()); }
    int getSessionTimeoutMin() { return sessionTimeoutMin; }

    public TomcatServerProperties setPort(int port) { this.port = port; return this;}
    private TomcatServerProperties setBaseDirPath(String baseDirPath) { this.baseDirPath = baseDirPath; return this;}
    private TomcatServerProperties setMinThreads(int minThreads) { this.minThreads = minThreads; return this; }
    private TomcatServerProperties setMaxThreads(int maxThreads) { this.maxThreads = maxThreads; return this; }
    private TomcatServerProperties setSessionTimeoutMin(int sessionTimeoutMin) { this.sessionTimeoutMin = sessionTimeoutMin; return this; }

    private TomcatServerProperties setContextPath(String contextPath) { this.contextPath = contextPath; return this; }
    static TomcatServerProperties fromSystemProperties() {
        String baseDirPathProp = "server.baseDirPath",
               tmpDirProp = "java.io.tmpdir";
        String tmpDir = System.getProperty(tmpDirProp);
        String baseDir = readProperty(baseDirPathProp).stringValueOr(tmpDir);
        LOG.info("Tomcat base dir: {}. To override set {} or {}", baseDir, baseDirPathProp, tmpDirProp);

        String minThreadsProp = "server.minThreads",
               maxThreadsProp = "server.maxThreads";
        int minThreads = readProperty(minThreadsProp).intValueOr(Runtime.getRuntime().availableProcessors());
        // There's a lot of UI (when working with DB), so setting thread pool size to CPUs * 3. Maybe it could
        // be higher, we haven't tested.
        int maxThreads = readProperty(maxThreadsProp).intValueOr(Runtime.getRuntime().availableProcessors()*3);
        LOG.info("Thread pool min={}, max={}. To override set {} and {}", minThreads, maxThreads, minThreadsProp, maxThreadsProp);

        // DB Pool Size depends on the Thread Pool size since each thread usually takes up only one DB connection.
        if(System.getProperty("db.pool.max_connections") == null)
            // +4 for Scheduled and Async code that can acess DB too
            System.setProperty("db.pool.max_connections", (maxThreads+4) + "");

        String sessionTimeoutProp = "server.servlet.session.timeout_min";
        int sessionTimeoutMin = readProperty(sessionTimeoutProp).intValueOr(30);
        LOG.info("Session timeout: {}. To override set {}", sessionTimeoutMin, sessionTimeoutProp);

        return new TomcatServerProperties()
                .setPort(readProperty("server.port").intValueOr(8080))
                .setBaseDirPath(baseDir)
                .setMinThreads(minThreads)
                .setMaxThreads(maxThreads)
                .setContextPath(readProperty("server.contextPath").stringValueOr(""))
                .setSessionTimeoutMin(sessionTimeoutMin);
    }
}
