package ru.rulex.conclusion.dagger;

import javax.inject.Inject;

import ru.rulex.conclusion.AssertionUnit;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.ConclusionStatePathTrace;
import ru.rulex.conclusion.Selector;
import ru.rulex.conclusion.FluentConclusionPredicate.SelectorPredicate;

public final class DaggerAssertionUnits
{

  static class IntExpression<T> implements AssertionUnit<T>
  {
    private final ConclusionPredicate<Integer> predicate;
    private final Selector<Object, Integer> selector;

    @Inject
    IntExpression( ConclusionPredicate<Integer> predicate, Selector<Object, Integer> selector )
    {
      this.predicate = predicate;
      this.selector = selector;
    }

    @Override
    public boolean satisfies( ConclusionStatePathTrace conclusionPathTrace, T event )
    {
      return new SelectorPredicate<Object, Integer>( predicate, selector ).apply( event );
    }
  }

  static class FloatExpression<T> implements AssertionUnit<T>
  {
    private final ConclusionPredicate<Float> predicate;
    private final Selector<Object, Float> selector;

    @Inject
    FloatExpression( ConclusionPredicate<Float> predicate, Selector<Object, Float> selector )
    {
      this.predicate = predicate;
      this.selector = selector;
    }

    @Override
    public boolean satisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
    {
      return new SelectorPredicate<Object, Float>( predicate, selector ).apply( event );
    }
  }

  static class StringExpression<T> implements AssertionUnit<T>
  {
    private final ConclusionPredicate<String> predicate;
    private final Selector<Object, String> selector;

    @Inject
    StringExpression( ConclusionPredicate<String> predicate, Selector<Object, String> selector )
    {
      this.predicate = predicate;
      this.selector = selector;
    }

    @Override
    public boolean satisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
    {
      return new SelectorPredicate<Object, String>( predicate, selector ).apply( event );
    }
  }
}
