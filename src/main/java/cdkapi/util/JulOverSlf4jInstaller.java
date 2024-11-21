package cdkapi.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.LifeCycle;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * To set up java.util.logging -> Logback bridging, we need to install() it first. This Logback Listener will do this
 * when Logback is initialized.
 * <h2>Justification</h2>
 * We could do this explicitly somewhere at early stages in the code, but then we'd have to figure out the best place.
 * And don't forget about the tests - there's no good single point for tests where we could inject this code. So doing
 * this as a Logback listener makes sense - it's explicitly declared in the logback.xml, and it's initialized
 * automatically by Logback itself at the startup.
 *
 * <h2>Configuring JUL over SLF4J bridge</h2>
 * <p>
 *     <b>This memo is duplicated in all the places where the bridging is configured, keep it up-to-date in all of them!</b>
 * </p>
 * Overall there are several places that need changes to enabled java.util.logging -> SLF4j bridging
 * (see <a href="https://www.slf4j.org/legacy.html#jul-to-slf4j">official documentation</a>):
 * <ol>
 *     <li>Add dependency jul-to-slf4j to pom.xml. The dependency contains the bridging code that accepts LogRecords from JUL</li>
 *     <li>Update production logback.xml with 2 listeners: our own JulOverSlf4jInstaller (sets up the bridging),
 *     LevelChangePropagator (passes logging levels from logback.xml to JUL)</li>
 *     <li>Do the same for the test logback.xml or any other present in the project</li>
 * </ol>
 *
 * @author Oleg Tkachenko
 */
public class JulOverSlf4jInstaller implements LoggerContextListener, LifeCycle {
    private boolean isStarted = false;

    @Override public void start() {
        // Remove existing handlers attached to j.u.l root logger
        // by default there is only one ConsoleHandler with output to System.err
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during the initialization phase
        SLF4JBridgeHandler.install();
        isStarted = true;
    }
    @Override public void stop() { isStarted = false; }
    @Override public void onStop(LoggerContext context) { }
    @Override public void onStart(LoggerContext context) { }
    @Override public void onReset(LoggerContext context) { }
    @Override public boolean isStarted() { return isStarted; }
    @Override public boolean isResetResistant() { return true; }
    @Override public void onLevelChange(Logger logger, Level level) { }
}
