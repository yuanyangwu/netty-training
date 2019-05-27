# Customize Netty Logger Setting

## Customize logback in a Netty project

add log dependency to pom.xml

```text
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.25</version>
    </dependency>

    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.3</version>
    </dependency>
```

add logback.xml under directory src/main/resources

```text
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/logFile.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="FILE"/>
    </root>
    <logger name="io.netty" level="DEBUG" additivity="true" />
</configuration>
```

## Customize log4j in a Netty project

add log dependency to pom.xml

```text
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.25</version>
    </dependency>

    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.25</version>
    </dependency>
```

add log4j.properties under directory src/main/resources

```text
log4j.rootLogger=DEBUG, consoleAppender, fileAppender

log4j.appender.consoleAppender=org.apache.log4j.ConsoleAppender
log4j.appender.consoleAppender.Target=System.out
log4j.appender.consoleAppender.layout=org.apache.log4j.PatternLayout
log4j.appender.consoleAppender.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c %x - %m%n

log4j.appender.fileAppender=org.apache.log4j.RollingFileAppender
log4j.appender.fileAppender.layout=org.apache.log4j.PatternLayout
log4j.appender.fileAppender.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c %x - %m%n
log4j.appender.fileAppender.File=logs/logFile.log
log4j.appender.fileAppender.MaxFileSize=10MB
log4j.appender.fileAppender.MaxBackupIndex=10
```

## Code

```text
InternalLoggerFactory.getInstance
    InternalLoggerFactory.getDefaultFactory
        InternalLoggerFactory.newDefaultFactory
            new Slf4JLoggerFactory
                org.slf4j.LoggerFactory.getILoggerFactory
```
