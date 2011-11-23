package com.smokejumperit.sublog;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class WithLogLoggerFactory implements LoggerFactory {
    public Logger makeNewLoggerInstance(String className) {
        return new WithLogLogger(className);
    }
}
