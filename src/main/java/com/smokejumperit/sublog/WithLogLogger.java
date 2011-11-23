package com.smokejumperit.sublog;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import static org.apache.log4j.Level.*;
import groovy.lang.Closure;

/**
* A wrapper around the Log4J logging facility to provide nifty Groovy-specific functionality.
*/
public class WithLogLogger extends Logger {

  private static final class StackTraceHolder extends Throwable {}

  private static final LogLog log = new LogLog();

  // TODO Implement stripping of stack traces

  protected WithLogLogger(String name) {
    super(name);
    log.debug("Instantiated " + this.getClass().getName() + " for " + name, new StackTraceHolder());
  }
  
  public static WithLogLogger getLogger(Class clazz) {
    if(clazz == null) {
      log.error("No class provided to 'getLogger'", new StackTraceHolder());
      throw new IllegalArgumentException("Must have non-null class");
    }
    return (WithLogLogger) Logger.getLogger(clazz.getName(), new WithLogLoggerFactory());
  }

  public void trace(Closure c) {
    if(isTraceEnabled()) {
      trace(c.call());
    }
  }

  public void trace(Throwable t, Closure c) {
    if(isTraceEnabled()) {
      trace(c.call(), t);
    }
  }

  public void debug(Closure c) {
    if(isDebugEnabled()) debug(c.call());
  }

  public void debug(Throwable t, Closure c) {
    if(isDebugEnabled()) debug(c.call(), t);
  }

  public void info(Closure c) {
    if(isInfoEnabled()) info(c.call());
  }

  public void info(Throwable t, Closure c) {
    if(isInfoEnabled()) info(c.call(), t);
  }

  public void warn(Closure c) {
    if(isEnabledFor(WARN)) warn(c.call());
  }

  public void warn(Throwable t, Closure c) {
    if(isEnabledFor(WARN)) warn(c.call(), t);
  }

  public void error(Closure c) {
    if(isEnabledFor(ERROR)) error(c.call());
  }

  public void error(Throwable t, Closure c) {
    if(isEnabledFor(ERROR)) error(c.call(), t);
  }

  public void fatal(Closure c) {
    if(isEnabledFor(FATAL)) fatal(c.call());
  }

  public void fatal(Throwable t, Closure c) {
    if(isEnabledFor(FATAL)) fatal(c.call(), t);
  }

}

