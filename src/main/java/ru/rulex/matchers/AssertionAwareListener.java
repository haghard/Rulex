package ru.rulex.matchers;

import org.hamcrest.Matcher;

public interface AssertionAwareListener
{

  void passed( Object analysedObject, Matcher<?> matcher );

  void failed( Object analysedObject, Matcher<?> matcher );

  void filtered( Object analysedObject, Matcher<?> matcher );

  void unexpected( Object analysedObject, Exception exception );

  void done();
}
