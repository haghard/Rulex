package ru.rulex.matchers;

import ru.rulex.conclusion.Selector;

public interface SelectorMatcherAdapter<T> {

  Selector<T, ?> valueOf(T value);

  String matcherDisplayName();
  
}
