package com.crunch;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(value = {"com.crunch"})
@SuppressWarnings("squid:S2187")
public class ApplicationTests {
}
