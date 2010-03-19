# WithLog

Provides a `@WithLog` AST Transform that attaches a Log4J-based Logger to the annotated class.

    import com.smokejumperit.sublog.*

    @WithLog
    class Foo {
      static logStaticMsg() { log.info("This is a static message!") }
      def logInstanceMsg() { log.info("This is an instance message!") }
    }

# Enhanced Logging

Unless you set the `withLog.apacheLogging` system property to true *at compile time*, an enhanced
logger is attached to your Groovy classes.  If you attach the enhanced logger, the JAR for WithLog
has to be available at runtime as well as at compile time.  If you use the `withLog.apacheLogging`
logger, then you only need the Apache `log4j` library at runtime.  

In addition to all the normal Log4J functionality (the enhanced logger is-a Log4J Logger), the 
following additional APIs are provided by the enhanced logger:

    log.trace { "Check me out, ${"I'm only evaluated at run-time!"}" }
    log.debug { "Check me out, ${"I'm only evaluated at run-time!"}" }
    log.info { "Check me out, ${"I'm only evaluated at run-time!"}" }
    log.warn { "Check me out, ${"I'm only evaluated at run-time!"}" }
    log.error { "Check me out, ${"I'm only evaluated at run-time!"}" }
    log.fatal { "Check me out, ${"I'm only evaluated at run-time!"}" }

    log.trace(someThrowable) { "Check me out, ${"I'm only evaluated at run-time!"}" }
    log.debug(someThrowable) { "Check me out, ${"I'm only evaluated at run-time!"}" }
    log.info(someThrowable) { "Check me out, ${"I'm only evaluated at run-time!"}" }
    log.warn(someThrowable) { "Check me out, ${"I'm only evaluated at run-time!"}" }
    log.error(someThrowable) { "Check me out, ${"I'm only evaluated at run-time!"}" }
    log.fatal(someThrowable) { "Check me out, ${"I'm only evaluated at run-time!"}" }

    log.trace({ "Check me out, ${"I'm only evaluated at run-time!"}" }, someThrowable)
    log.debug({ "Check me out, ${"I'm only evaluated at run-time!"}" }, someThrowable)
    log.info({ "Check me out, ${"I'm only evaluated at run-time!"}" }, someThrowable)
    log.warn({ "Check me out, ${"I'm only evaluated at run-time!"}" }, someThrowable)
    log.error({ "Check me out, ${"I'm only evaluated at run-time!"}" }, someThrowable)
    log.fatal({ "Check me out, ${"I'm only evaluated at run-time!"}" }, someThrowable)

# License

Released under the WTFPL.  See LICENSE for more information.

# Installation

To install this global transform in your packge, check out the project
and then run:

    gradle jar

Drop the resulting JAR (in `build/libs`) into your project classpath and away you go!

# Maven Repo

There is a Maven repository holding this artifact at `http://repo.smokejumperit.com`.  The group is `com.smokejumperit` and the artifact is `with-log`.
