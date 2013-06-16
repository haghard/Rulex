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
public final class PredicateCreators
{
  public interface PredicateCreator<T extends Comparable<? super T>> 
  {
    ConclusionPredicate<T> createPredicate(T value);
  }

  static class LessPredicateCreator<T extends Comparable<? super T>> implements PredicateCreator<T> 
  {
    @Override
    public InjectableLessConclusionPredicate<T> createPredicate( T value )
    {
      return new InjectableLessConclusionPredicate<T>( value );
    }
  }

  static class MorePredicateCreator<T extends Comparable<? super T>> implements PredicateCreator<T> 
  {
    @Override
    public InjectableMoreConclusionPredicate<T> createPredicate( T value )
    {
      return new InjectableMoreConclusionPredicate<T>( value );
    }
  }

  static class MoreOrEqualsPredicateCreator<T extends Comparable<? super T>> implements PredicateCreator<T>
  {
    @Override
    public ConclusionPredicate<T> createPredicate( T value )
    {
      return new InjectableMoreOrEqualsConclusionPredicate<T>( value );
    }
  }

  static class LessOrEqualsPredicateCreator<T extends Comparable<? super T>> implements PredicateCreator<T>
  {
    @Override
    public ConclusionPredicate<T> createPredicate( T value )
    {
      return new InjectableLessOrEqualsConclusionPredicate<T>( value );
    }
  }
}
