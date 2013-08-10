package ru.rulex.conclusion.delegate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

class MethodInvokable<T, E> extends Invokable<T, E>
{
  private final Method accessor;
  private final Object[] arguments;

  MethodInvokable( Method accessor, Object[] arguments )
  {
    super( accessor );
    this.accessor = accessor;
    this.arguments = Arrays.copyOf( arguments, arguments.length );
  }

  @Override
  @SuppressWarnings("unchecked")
  E invokeInternal( T receiver, Object... args ) throws InvocationTargetException, IllegalAccessException
  {
    return (E) accessor.invoke( receiver, arguments );
  }

  public Class<E> accessorType()
  {
    return (Class<E>) accessor.getReturnType();
  }

  @Override
  public String toString()
  {
    return accessor.getName();
  }
}