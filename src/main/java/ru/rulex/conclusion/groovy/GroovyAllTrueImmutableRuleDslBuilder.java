package ru.rulex.conclusion.groovy;

import groovy.lang.GroovyObjectSupport;
import ru.rulex.conclusion.*;
import ru.rulex.conclusion.delegate.ProxyUtils;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.*;

import java.util.ArrayList;
import java.util.List;

import ru.rulex.conclusion.FluentConclusionPredicate.SelectorPredicate;

import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

/**
 * @param <T>
 * @author haghard
 */
public class GroovyAllTrueImmutableRuleDslBuilder<T> extends GroovyObjectSupport
{
  private T event;

  private boolean result = true;

  private final List<ImmutableAssertionUnit<T>> units = new ArrayList<>( 5 );

  public void onEvent( T event )
  {
    this.event = event;
  }

  public <E extends Comparable<? super E>> GroovyAllTrueImmutableRuleDslBuilder<T> operation( E val )
  {
    return this;
  }

  public <E extends Comparable<? super E>> GroovyAllTrueImmutableRuleDslBuilder<T> eq( E val )
  {
    capture( ProxyUtils.<T, E>toSelector( val ),
            callOn( ConclusionPredicate.class,
                    ConclusionPredicate.class.cast( new InjectableEqualsConclusionPredicate<E>( val ) ) ) );
    return this;
  }

  public <E extends Comparable<? super E>> GroovyAllTrueImmutableRuleDslBuilder<T> more( E val )
  {
    capture( ProxyUtils.<T, E>toSelector( val ),
            callOn( ConclusionPredicate.class,
                    ConclusionPredicate.class.cast( new InjectableMoreConclusionPredicate<E>( val ) ) ) );
    return this;
  }

  public <E extends Comparable<? super E>> GroovyAllTrueImmutableRuleDslBuilder<T> atLeast( E val )
  {
    capture( ProxyUtils.<T, E>toSelector( val ),
            callOn( ConclusionPredicate.class,
                    ConclusionPredicate.class.cast( new InjectableAtLeastConclusionPredicate<E>( val ) ) ) );
    return this;
  }

  public <E extends Comparable<? super E>> GroovyAllTrueImmutableRuleDslBuilder<T> atMost( E val )
  {
    capture( ProxyUtils.<T, E>toSelector( val ),
            callOn( ConclusionPredicate.class,
                    ConclusionPredicate.class.cast( new InjectableAtMostConclusionPredicate<E>( val ) ) ) );
    return this;
  }

  public <E extends Comparable<? super E>> GroovyAllTrueImmutableRuleDslBuilder<T> less( E val )
  {
    capture( ProxyUtils.<T, E>toSelector( val ),
            callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                    new InjectableLessConclusionPredicate<E>( val ) ) ) );
    return this;
  }

  public void eval()
  {
    for ( ImmutableAssertionUnit<T> unit : units )
    {
      if ( !unit.isSatisfies( ConclusionStatePathTrace.defaultInstance(), event ) )
      {
        result = false;
        break;
      }
    }
  }

  public boolean getResult()
  {
    return result;
  }

  private <E extends Comparable<? super E>> void capture( final Selector<T, E> selector,
                                                          final ConclusionPredicate<E> predicate )
  {
    units.add( new ImmutableAssertionUnit<T>()
    {
      @Override
      public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, T event )
      {
        return new SelectorPredicate<T, E>( predicate, selector ).apply( event );
      }
    } );
  }
}
