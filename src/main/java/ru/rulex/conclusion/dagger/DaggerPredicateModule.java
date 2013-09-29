package ru.rulex.conclusion.dagger;

import javax.inject.Named;

import com.google.common.collect.ImmutableSet;
import ru.rulex.conclusion.*;

import ru.rulex.conclusion.FluentConclusionPredicate.SelectorPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableAtMostConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMatchAnyOffPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableAtLeastConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableEqualsConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableLessConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMoreConclusionPredicate;

import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

@dagger.Module(
        complete = false, library = true,
        injects = ConclusionPredicate.class
)
public class DaggerPredicateModule
{
  private final AnyArgument<?> value;
  private final LogicOperation operation;

  public DaggerPredicateModule( AnyArgument<?> value, LogicOperation operation )
  {
    this.operation = operation;
    this.value = value;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @dagger.Provides
  protected <T> ConclusionPredicate providePredicate()
  {
    switch ( operation )
    {
      case eq: {
        return callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                new InjectableEqualsConclusionPredicate(  ( Comparable<T> ) value.value ) ) );
      }
      case lessThan: {
        return callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                new InjectableLessConclusionPredicate(  ( Comparable<T> ) value.value ) ) );
      }
      case moreThan: {
        return callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                new InjectableMoreConclusionPredicate(  ( Comparable<T> ) value.value ) ) );
      }
      case moreOrEquals:  {
        return callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                new InjectableAtLeastConclusionPredicate(  ( Comparable<T> ) value.value ) ) );
      }
      case lessOrEquals: {
        return callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                new InjectableAtMostConclusionPredicate(  ( Comparable<T> ) value.value ) ) );
      }
      case equalsAnyOff: {
        return callOn( ConclusionPredicate.class, ConclusionPredicate.class.cast(
                new InjectableMatchAnyOffPredicate( (ImmutableSet )value.value ) ) );
      }
      default:
        throw new IllegalArgumentException( "DaggerPredicateModule.providePredicate() unsupported operation " );
    }
  }

  @dagger.Module(
          addsTo = DaggerPredicateModule.class,
          injects = ImmutableAssertionUnit.class,
          complete = false, library = true )
  static class ImmutableDaggerPredicateModule
  {
    private final Selector selector;

    public ImmutableDaggerPredicateModule( Selector selector )
    {
      this.selector = selector;
    }

	  @dagger.Provides
    @SuppressWarnings("rawtypes")
    <T, E> Selector provideSelector()
    {
      return selector;
    }

	  @dagger.Provides
    @SuppressWarnings({"rawtypes", "unchecked"})
    ImmutableAssertionUnit provideAssertionUnit( final ConclusionPredicate conclusionPredicate, final Selector selector )
    {
      return new ImmutableAssertionUnit()
      {
		@Override
        public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
        {
          return new SelectorPredicate( conclusionPredicate, selector )
                  .apply( event );
        }
      };
    }
  }

  @dagger.Module(
          addsTo = DaggerPredicateModule.class,
          injects = MutableAssertionUnit.class,
          complete = false, library = true )
  static class MutableDaggerPredicateModule
  {
    private final String varName;

    public MutableDaggerPredicateModule( String varName )
    {
      this.varName = varName;
    }

    @dagger.Provides
    String provideVarName()
    {
      return varName;
    }

    @dagger.Provides
    @Named( "emptySelector" )
    @SuppressWarnings("rawtypes")
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
    @SuppressWarnings("rawtypes")
    <T> MutableAssertionUnit provideAssertionUnit(
            final ConclusionPredicate conclusionPredicate,
            @Named( "emptySelector" ) final Selector pSelector,
            final String varName0)
    {
      return new MutableAssertionUnit<T>()
      {
        String varName = varName0;
        Selector<T, ?> selector = pSelector;

		    @Override
		    @SuppressWarnings("unchecked")
        public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, Object event )
        {
          return new SelectorPredicate( conclusionPredicate, selector )
                  .apply( event );
        }

        @Override
        public void setVar( String varName )
        {
          this.varName = varName;
        }

        @Override
        public String getVar()
        {
          return varName;
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