package com.smokejumperit.sublog

import org.apache.log4j.Level

class WithLogLoggerTests {

  void testLoggerRunsClosures() {
    def log = WithLogLogger.getLogger(this.class)
    ["DEBUG", "ERROR", "FATAL", "INFO", "TRACE", "WARN"].each { lvl ->
      log.level = Level."$lvl"
      def(withoutThrowable, withThrowable, afterThrowable) = [false, false, false]
      log."${lvl.toLowerCase()}"({ withoutThrowable = true })
      log."${lvl.toLowerCase()}"({ withThrowable = true }, new Throwable())
      log."${lvl.toLowerCase()}"(new Throwable()) { afterThrowable = true }
      assertTrue "Did not execute the call without a throwable", withoutThrowable
      assertTrue "Did not execute the call with a throwable", withThrowable
      assertTrue "Did not execute the call after a throwable", afterThrowable
    }
  }

}
