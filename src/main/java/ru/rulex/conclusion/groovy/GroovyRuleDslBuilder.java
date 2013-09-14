package ru.rulex.conclusion.groovy;

import groovy.lang.GroovyObjectSupport;
import ru.rulex.conclusion.*;
import ru.rulex.conclusion.delegate.ProxyUtils;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.*;

import java.util.ArrayList;
import java.util.List;

import ru.rulex.conclusion.FluentConclusionPredicate.SelectorPredicate;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class GroovyRuleDslBuilder<T> extends GroovyObjectSupport
{
  private T event;

  private boolean result = true;

  private final List<ImmutableAssertionUnit<T>> units = new ArrayList<>(5);

  public void withEvent(T event)
  {
    System.out.println( event );
    this.event = event;
  }

  public <E extends Comparable<? super E>> GroovyRuleDslBuilder<T> from( E val )
  {
    return this;
  }

  @SuppressWarnings("unchecked")
  public <E extends Comparable<? super E>> GroovyRuleDslBuilder<T> more( E val )
  {
    final Selector<T, E> selector = ProxyUtils.toSelector( val );
    final ConclusionPredicate<T> predicate = callOn( ConclusionPredicate.class,
      ConclusionPredicate.class.cast(new InjectableMoreConclusionPredicate<E>( val ) ) );
    units.add( new ImmutableAssertionUnit<T>()
    {
      @Override
      public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, T event )
      {
        return new SelectorPredicate( predicate, selector ).apply( event );
      }
    } );
    return this;
  }

  @SuppressWarnings("unchecked")
  public <E extends Comparable<? super E>> GroovyRuleDslBuilder<T> less( E val )
  {
    final Selector<T, E> selector = ProxyUtils.toSelector( val );
    final ConclusionPredicate<T> predicate = callOn( ConclusionPredicate.class,
      ConclusionPredicate.class.cast(new InjectableLessConclusionPredicate<E>( val ) ) );
    
    units.add( new ImmutableAssertionUnit<T>()
    {
      @Override
      public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, T event )
      {
        return new SelectorPredicate( predicate, selector ).apply( event );
      }
    } );
    return this;
  }

  public void eval()
  {
    for(ImmutableAssertionUnit<T> unit: units)
    {
      if( !unit.isSatisfies( ConclusionStatePathTrace.defaultInstance(), event)  )
      {
        result = false;
        break;
      }
    }
  }

  public boolean getResult() { return result; }
}
