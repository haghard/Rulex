package ru.rulex.matchers;

import org.hamcrest.Matcher;

public interface AssertionAwareListener<T> {

  void passed(T analysedObject, Matcher<?> matcher);

  void failed(T analysedObject, Matcher<?> matcher);

  void filtered(T analysedObject, Matcher<?> matcher);

  void unexpected(T analysedObject, Exception exception);

  void done();
}
