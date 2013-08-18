package ru.rulex.actor;

import ru.rulex.conclusion.ConclusionFunction;

/**
 * 
 * @author haghard
 * 
 * @param <T>
 */
public abstract class SomeEffect<T>
{

  public abstract void doEffect( T item );

  public final ConclusionFunction<T, Unit> lift()
  {
    return new ConclusionFunction<T, Unit>()
    {
      public Unit apply( final T item )
      {
        apply( item );
        return Unit.unit();
      }
    };
  }
}