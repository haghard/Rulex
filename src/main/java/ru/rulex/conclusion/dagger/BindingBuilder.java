package ru.rulex.conclusion.dagger;

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
