package com.example.library.greeting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"default", "en"})
public class EnglishGreetingService implements GreetingService {

    private final String prefix;

    public EnglishGreetingService(@Value("${greeting.prefix:Hello}") String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String greet(String name) {
        return prefix + ", " + name + "!";
    }
}
