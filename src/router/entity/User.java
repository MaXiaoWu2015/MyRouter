package router.entity;

import inject.InjectUriParam;

public class User {
    public @InjectUriParam String  name;
    public @InjectUriParam int    age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
