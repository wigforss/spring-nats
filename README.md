# Spring NATS
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 
![](https://img.shields.io/badge/Package-JAR-2396ad)
![](https://img.shields.io/badge/Repository-Maven%20Central-2396ad)  
![](https://img.shields.io/badge/Java-11%2B-d6a827)
![](https://github.com/wigforss/spring-nats/workflows/Test%20and%20Deploy/badge.svg)
[![codecov](https://codecov.io/gh/wigforss/spring-nats/branch/master/graph/badge.svg)](https://codecov.io/gh/wigforss/spring-nats)



Add the following dependency.
```
 <dependency>
    <groupId>org.kasource</groupId>
    <artifactId>spring-nats</artifactId>
    <version>${spring.nats.version}</version>
</dependency>
```

## Configure via @Configuration
The annotation-based configuration relies on properties, but no properties are required for a minimal configuration.

### Configuration
| Property                                         | Type                                          | Required | Default                                     | Description                |
|:-------------------------------------------------|-----------------------------------------------|----------|:--------------------------------------------|:---------------------------|
| spring.nats.serDeType                            | Enumeration (*JAVA, JSON_JACKSON, JSON_GSON*) | No       | Auto Selected by the SerDeSelectionStrategy | The SerDe framework to use |
| spring.nats.bean.validation.enabled              | Boolean                                       | No       | false                                       | Enable bean validation (JSR303) when serializing and de-serialize objects, given they are annotated with  @org.springframework.validation.annotation.Validated |
| spring.nats.jsonSchema.validation.enabled        | Boolean                                       | No       | false                                       | Enable JSON Schema validation after serialization and before de-serialization, also configure *jsonSchema.scanPackage* to find the JSON schemas  |
| spring.nats.jsonSchema.scanPackage               | String                                        | No       | -                                           | The packages (sub packages will be included) to scan for java classes annotated with @org.kasource.json.schema.JsonSchema annotated classes, support comma separated list of packages, to be used if *jsonSchema.validation.enabled* is set to true  |
| spring.nats.consumer.autoStart                   | Boolean                                       | No       | true                                        | True to automatically start all the registered consumers, when set to false the start method of the ConsumerManager needs to be invoked.  |

### Connection Configuration

| Property                                         | Type              | Required  | Default                | Description  |
|:-------------------------------------------------|-------------------|-----------|:-----------------------|:-------------|
| spring.nats.connection.url                       | URL               | No        | nats://localhost:4222  | Connection URL, supports comman separated list of URLs |
| spring.nats.connection.maxReconnects             | Integer           | No        | 60                     | Maximum number of reconnect attempts, -1 for infinite attempts  |
| spring.nats.connection.timeoutSeconds            | Integer           | No        | 2                      | Connection timeout in seconds  |
| spring.nats.connection.name                      | String            | No        | -                      | Optional connection name, for debug purposes  |
| spring.nats.connection.username                  | String            | No        | -                      | User name  |
| spring.nats.connection.password                  | String            | No        | -                      | Password  |
| spring.nats.connection.jwt.token                 | String            | No        | -                      | JWT Token  |
| spring.nats.connection.jwt.nKey                  | String            | No        | -                      | N Key for JWT signing  |
| spring.nats.connection.tls.enabled               | Boolean           | No        | false                  | Enable TLS  |
| spring.nats.connection.tls.trustStore            | Resource location | No        | -                      | Trust store resource location (supports file: and classpath: prefixes) , if not set all known CAs will be trusted  |
| spring.nats.connection.tls.trustStorePassword    | String            | No        | -                      | Trust store password  |
| spring.nats.connection.tls.identityStore         | Resource Location | No        | -                      | Identity store resource location, provides the client cert for mutual TLS  |
| spring.nats.connection.tls.identityStorePassword | String            | No        | -                      | Identity store password  |

## Minimal Configuration
```
@Import(org.kasource.spring.nats.config.java.NatsConfiguration)
@Configuration
public class MyConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```
This configuration creates a connection to the default NATS URL (localhost:4222) using auto-select to
chose the serialization / de-serialization framework.
 
Jackson will be chosen by the default SerDe selection strategy if its available on classpath otherwise it will try GSON and finally fall back to Java standard serialization.

The SerDe selection strategy can be overridden by adding your own bean of type org.kasource.spring.nats.message.serde.SerDeSelectionStrategy to the Application Context. 

A bean of type *com.fasterxml.jackson.databind.ObjectMapper* must be available in the Application Context if Jackson is chosen (is found on classpath). 

Likewise for GSON a bean of type *com.google.gson.Gson* must be available in the Application Context. 


## Configure via XML Namespace
The NATS support can be configured via the NATS XML Namespace *http://kasource.org/schema/nats*

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:nats="http://kasource.org/schema/nats"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://kasource.org/schema/nats http://kasource.org/schema/nats">

    ...

</beans>
```

The ```<nats:nats/>``` element creates:

* *NatsTemplate* - To send messages
* *Consumer Manager* - Where message consumers can be registered
* Post Bean Processor - Which automatically registers ```@Consumer``` annotated methods of beans with the *Consumer Manager*.

### Minimal Configuration
```
  <bean id="objectMapper" class="com.fasterxml.jackson.databind.ObjectMapper"/>
  <nats:nats/>
```
This configuration creates a connection to the default NATS URL (localhost:4222) using auto-select to
chose the serialization / de-serialization framework.
 
Jackson will be chosen by the default SerDe selection strategy if its available on classpath otherwise it will try GSON and finally fall back to Java standard serialization.

The SerDe selection strategy can be overridden by setting the *serde-selection-strategy* attribute. 

A bean of type *com.fasterxml.jackson.databind.ObjectMapper* must be available in the Application Context if Jackson is chosen (is found on classpath). 

Likewise for GSON a bean of type *com.google.gson.Gson* must be available in the Application Context. 

### Configuring the Connection URL and SerDe
```
  <bean id="gson" class="com.google.gson.Gson"/>
  <nats:nats connection-url="nats://somehost:4222"
             serde-type="json-gson"/>
```
This configuration overrides the auto-selection by setting GSON as the serialization / de-serialization framework.

A bean of type *com.google.gson.Gson* must be available in the Application Context when serde-type is set to *json-gson*. 


### Configuring Message Object Validation
This configuration adds JSR303 validation to the SerDe process if object are annotated with ```@org.springframework.validation.annotation.Validated```.

**Note**: A bean of type *javax.validation.Validator* must be available in the Application Context when this option is enabled.

```
  <bean id="objectMapper" class="com.fasterxml.jackson.databind.ObjectMapper"/>
  <bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean"/>
  <nats:nats connection-url="nats://somehost:4222"
             enable-bean-validation="true"/>

```
### Configuring Data Validation
Adds JSON Schema validation, which scans for Java classes annotated with ```@org.kasource.json.schema.JsonSchema``` in the package and sub-packages of *org.example.app*.

**Note**: The JSON Schema validation is based on Jackson thus enabling it for other serde-types like json/gson will not have any affect.

```
  <bean id="objectMapper" class="com.fasterxml.jackson.databind.ObjectMapper"/>
  <nats:nats connection-url="nats://somehost:4222"
             serde-type="json-jackson"
             enable-json-schema-validation="true"
             json-schema-scan-packages="org.example.app"/>

```

### Custom Connection Configuration
To support more detailed configuration of the connection, its possible to override the connection created with a custom one by setting the connection attribute

```
  <nats:connection id="myConnection"
                   url="nats://somehost1:4222, nats://somehost2:4222"
                   username="username"
                   password="password">
      <tls enabled="true"/>
  </nats:connection>

  <nats:nats connection="myConnection"/>
```

## Sending Messages
Once the spring-nats is initialized a bean of type **org.kasource.spring.nats.publisher.NatsTemplate** will be available for injection (autowiring).

```
    @Autowired
    private NatsTemplate natsTemplate;
    
    ...
    natsTemplate.publish(AnyObject, "a-subject")
```

## Receiving Messages
Messages can be read by setting a Consumer either by annotating a bean method with ```@org.kasource.spring.nats.annotation.Consumer``` or by registering a **java.util.function.Consumer**. 

### Annotated Methods 
By annotating a bean method with ```@org.kasource.spring.nats.annotation.Consumer```. This method must be public with one and only one argument and void return type.
```
 ...
    @Consumer(subject = "a-subject")
    public void onMessage(AnyObject any) {
        System.out.println("onMessage " + any);
    }
 ...
```


### Register Consumer Function
By registering a **java.util.function.Consumer** object or Lambda function with the NatsConsumerManager

```
 ...
    @Autowired
    private NatsConsumerManager manager;

    @PostConstruct
    void setup() {
        manager.register(System.out::println, AnyObject.class, "a-subject");
    }
 ...
```


## NatsConnection

## Ser/De Factory
### Jackson
### Gson

## Message Validation
### Data Validation
### Object Validation

## NatsTemple


## NatsConsumerManager
