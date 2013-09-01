package ru.rulex.conclusion.dagger;

import javax.inject.Named;

import ru.rulex.conclusion.*;
import com.google.common.base.Optional;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableLessConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableLessOrEqualsConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMoreConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMoreOrEqualsConclusionPredicate;

import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

@dagger.Module(
        complete = false, library = true,
        injects = ConclusionPredicate.class
)
public class DaggerPredicateModule
{
  private final Optional<?> value;
  private final LogicOperation operation;

  public <T extends Comparable<? super T>> DaggerPredicateModule( T value, LogicOperation operation )
  {
    this.operation = operation;
    this.value = Optional.of( value );
  }

  @dagger.Provides
  protected <T> ConclusionPredicate providePredicate()
  {
    final Comparable<T> v = ( Comparable<T> ) value.get();
    switch ( operation )
    {
      case lessThan:
        return callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                new InjectableLessConclusionPredicate( v ) ) );
      case moreThan:
        return callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                new InjectableMoreConclusionPredicate( v ) ) );
      case moreOrEquals:
        return callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                new InjectableMoreOrEqualsConclusionPredicate( v ) ) );
      case lessOrEquals:
        return callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                new InjectableLessOrEqualsConclusionPredicate( v ) ) );

      default:
        throw new IllegalArgumentException( "DaggerPredicateModule.providePredicate() unsupported operation " );
    }
  }

  @dagger.Module(
          addsTo = DaggerPredicateModule.class,
          injects = ImmutableAssertionUnit.class,
          complete = false, library = true )
  static class CompleteDaggerPredicateModule
  {
    private final SelectorPipeline selectorPipeline;

    public CompleteDaggerPredicateModule( SelectorPipeline selectorPipeline )
    {
      this.selectorPipeline = selectorPipeline;
    }

    @dagger.Provides
    <T, E> Selector provideSelector()
    {
      return selectorPipeline.cast();
    }

    @dagger.Provides
    ImmutableAssertionUnit provideAssertionUnit( final ConclusionPredicate conclusionPredicate, final Selector selector )
    {
      return new ImmutableAssertionUnit()
      {
        @Override
        public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
        {
          return new FluentConclusionPredicate.SelectorPredicate( conclusionPredicate, selector )
                  .apply( event );
        }
      };
    }
  }

  @dagger.Module(
          addsTo = DaggerPredicateModule.class,
          injects = MutableAssertionUnit.class,
          complete = false, library = true )
  static class UncompletedDaggerPredicateModule
  {
    private final String varName;

    public UncompletedDaggerPredicateModule( String varName )
    {
      this.varName = varName;
    }

    @dagger.Provides
    @Named( "emptySelector" )
    <T, E> Selector provideSelector()
    {
      return new Selector<T, E>()
      {
        @Override
        public E select( T argument )
        {
          throw new IllegalStateException( "empty selector was called" );
        }
      };
    }

    @dagger.Provides
    <T> MutableAssertionUnit provideAssertionUnit(
            final ConclusionPredicate conclusionPredicate,
            @Named( "emptySelector" ) final Selector pSelector )
    {
      return new MutableAssertionUnit<T>()
      {
        String varName;
        Selector<T, ?> selector = pSelector;

        @Override
        public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
        {
          return new FluentConclusionPredicate.SelectorPredicate( conclusionPredicate, selector )
                  .apply( event );
        }

        @Override
        public void setVar( String varName )
        {
          this.varName = varName;
        }

        @Override
        public void setSelector( Selector<T, ?> selector )
        {
          this.selector = selector;
        }

      };
    }
  }
}