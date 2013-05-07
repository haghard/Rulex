package ru.rulex.conclusion;

public class RulexMatchers {

  public static <T> ConclusionPredicate<T> eq(final T value) {
    return new ConclusionPredicate<T>() {
      @Override
      public boolean apply(T argument) {
        return argument.equals(value);
      }
    };
  }
}
