package ru.rulex.conclusion.dagger;

import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableLessConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMoreConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMoreOrEqualsConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableLessOrEqualsConclusionPredicate;
/**
 * 
 * @author haghard
 *
 */
public final class PredicateFactory
{
  public interface Factory<T extends Comparable<? super T>> 
  {
    ConclusionPredicate<T> createPredicate(T value);
  }

  static class LessPredicateFactory<T extends Comparable<? super T>> implements Factory<T> 
  {
    @Override
    public InjectableLessConclusionPredicate<T> createPredicate( T value )
    {
      return new InjectableLessConclusionPredicate<T>( value );
    }
  }

  static class MorePredicateFactory<T extends Comparable<? super T>> implements Factory<T> 
  {
    @Override
    public InjectableMoreConclusionPredicate<T> createPredicate( T value )
    {
      return new InjectableMoreConclusionPredicate<T>( value );
    }
  }

  static class MoreOrEqualsPredicateFactory<T extends Comparable<? super T>> implements Factory<T>
  {
    @Override
    public ConclusionPredicate<T> createPredicate( T value )
    {
      return new InjectableMoreOrEqualsConclusionPredicate<T>( value );
    }
  }

  static class LessOrEqualsPredicateFactory<T extends Comparable<? super T>> implements Factory<T>
  {
    @Override
    public ConclusionPredicate<T> createPredicate( T value )
    {
      return new InjectableLessOrEqualsConclusionPredicate<T>( value );
    }
  }
}
