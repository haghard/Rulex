package ru.rulex.conclusion.dagger;

import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableLessConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMoreConclusionPredicate;
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

  static class LessCreator<T extends Comparable<? super T>> implements PredicateCreator<T> 
  {
    @Override
    public InjectableLessConclusionPredicate<T> createPredicate( T value )
    {
      return new InjectableLessConclusionPredicate<T>( value );
    }
  }

  static class MoreCreator<T extends Comparable<? super T>> implements PredicateCreator<T> 
  {
    @Override
    public InjectableMoreConclusionPredicate<T> createPredicate( T value )
    {
      return new InjectableMoreConclusionPredicate<T>( value );
    }
  }
}
