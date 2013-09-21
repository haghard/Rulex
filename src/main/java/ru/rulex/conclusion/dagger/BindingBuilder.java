package ru.rulex.conclusion.dagger;

import com.google.common.collect.ImmutableSet;
import dagger.ObjectGraph;
import ru.rulex.conclusion.Selector;

import static dagger.ObjectGraph.create;

import ru.rulex.conclusion.dagger.DaggerPredicateModule.InjectionArgument;
import ru.rulex.conclusion.dagger.DaggerPredicateModule.ImmutableDaggerPredicateModule;
import ru.rulex.conclusion.dagger.DaggerPredicateModule.MutableDaggerPredicateModule;

/**
 *
 *
 */
public class BindingBuilder
{
  public static <T extends Comparable<? super T>> InjectionArgument<T> argFor( final T pvalue )
  {
    return new InjectionArgument<T>(){{
      this.value = pvalue;
    }};
  }

  public static <T extends Comparable<? super T>> InjectionArgument<ImmutableSet<T>> argFor( final T[] args )
  {
    return new InjectionArgument<ImmutableSet<T>>(){{
      this.value = ImmutableSet.copyOf( args );
    }};
  }

  public static <T extends Comparable<? super T>> InjectionArgument<ImmutableSet<T>> argFor( final Iterable<T> list )
  {
    return new InjectionArgument<ImmutableSet<T>>(){{
      this.value = ImmutableSet.copyOf( list );
    }};
  }

  /**
   *
   * @param argument
   * @param operation
   * @param selector
   * @return
   */
  public static ObjectGraph immutableGraph( InjectionArgument<?> argument, LogicOperation operation,
                                            Selector selector )
  {
    return create(
            new DaggerPredicateModule( argument, operation ),
            new ImmutableDaggerPredicateModule( selector ) );
  }

  /**
   *
   * @param argument
   * @param operation
   * @param varName
   * @param <T>
   * @return
   */
  public static ObjectGraph mutableGraph( InjectionArgument<?> argument, LogicOperation operation,
                                          String varName )
  {
    return ObjectGraph.create(
            new DaggerPredicateModule( argument, operation ),
            new MutableDaggerPredicateModule( varName ) );
  }
}
