package ru.rulex.matchers;

public interface RulexRule<T> {

  RulexAnalyzer in(Iterable<T> iterator);

  RulexAnalyzer on(T item);
}
