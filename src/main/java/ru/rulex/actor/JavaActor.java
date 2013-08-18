package ru.rulex.actor;

import java.util.concurrent.Callable;
import ru.rulex.conclusion.ConclusionFunction;
import ru.rulex.conclusion.execution.Callables;
import ru.rulex.conclusion.execution.ParallelStrategy;

/**
 * 
 * @author haghard
 * @param <T>
 */
public final class JavaActor<T> {

  private ConclusionFunction<T, Callable<Unit>> function;
  private ParallelStrategy<Unit> pStrategy;

  public static <T> JavaActor<T> queueActor(final ParallelStrategy<Unit> strategy, 
		  final SomeEffect<T> effect) {
    return actor(strategy, effect);
  }

  private JavaActor(final ParallelStrategy<Unit> pStrategy, 
      ConclusionFunction<T, Callable<Unit>> function) {
    this.pStrategy = pStrategy;
    this.function = function;
  }

  /**
   * Creates a new Actor that uses the given parallelization strategy and has the given side-effect.
   *
   * @param s The parallelization strategy to use for the new Actor.
   * @param e The side-effect to apply to messages passed to the Actor.
   * 
   * @return A new actor that uses the given parallelization strategy and has the given side-effect.
   */
  public static <T> JavaActor<T> actor(final ParallelStrategy<Unit> pStrategy,
		  final SomeEffect<T> effect) {
    return new JavaActor<T>(pStrategy, Callables.curry( effect.lift() ));
  }
}
