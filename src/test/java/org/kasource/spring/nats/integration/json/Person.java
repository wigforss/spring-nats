package org.kasource.spring.nats.integration.json;

import org.kasource.json.schema.JsonSchema;

@JsonSchema(name = "person", version = "1", location = "/json/person-v1.schema.json")
public class Person {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
