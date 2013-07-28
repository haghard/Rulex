package ru.rulex.conclusion.dagger;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@java.lang.annotation.Target(METHOD) @Retention(RUNTIME)
public @interface More
{
  String value();
}
