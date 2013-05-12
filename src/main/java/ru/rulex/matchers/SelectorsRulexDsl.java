package ru.rulex.matchers;

import ru.rulex.conclusion.Selector;
import static ru.rulex.conclusion.delegate.ProxyUtils.toSelector;

public final class SelectorsRulexDsl {

  private SelectorsRulexDsl() {
  }

  public static <T, E extends Comparable<? super E>> RulexMatchersBuilder<T> selectors(
      final Class<T> type, final E arg) {

    return new RulexMatchersBuilder<T>(new SelectorMatcherAdapter<T>() {
      
      @Override public Selector<T,E> valueOf(final T matched) {
        return toSelector(arg);
      }

      @Override public String matcherDisplayName() {
        return "IntSelectors(" + type.getClass().getName() + ")";
      }
    });
  }
}
