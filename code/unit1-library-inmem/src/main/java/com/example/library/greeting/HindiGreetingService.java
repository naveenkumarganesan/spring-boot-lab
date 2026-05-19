package com.example.library.greeting;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("hi")
public class HindiGreetingService implements GreetingService {

    @Override
    public String greet(String name) {
        return "Namaste, " + name + "!";
    }
}
