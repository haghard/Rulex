/*
 * Copyright (C) 2013 The Conclusions Authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ru.rulex.conclusion.delegate;

import com.google.common.base.Preconditions;

import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.Selector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
/**
 *
 *
 */
abstract class Invokable<T, E> {

  private final Member reflectionMember;

  Invokable(Member reflectionMember) {
    this.reflectionMember = reflectionMember;
  }

  /**
   * @param receiver
   * @param args
   * @return
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  abstract Object invokeInternal(T receiver, Object... args)
      throws InvocationTargetException, IllegalAccessException;

  @SuppressWarnings ( "unchecked")
  public final E invoke(T receiver, Object... args)
      throws InvocationTargetException, IllegalAccessException {
    return (E) invokeInternal(receiver, Preconditions.checkNotNull(args));
  }

  public static <T, E> Invokable<T, E> invokableMethod(Method method, Object... arguments) {
    return new MethodInvokable<T, E>(method, arguments);
  }

  public static <T> ConclusionPredicate<T> invokablePredicate(Invokable<T, Boolean> invokable) {
    return new InvokableConclusionPredicate<T>(invokable);
  }

  public static <T, E> Selector<T, E> invokableSelector(Invokable<T, E> invokable) {
    return new InvokableSelector<T, E>(invokable);
  }

  static class MethodInvokable<T, E> extends Invokable<T, E> {
    private final Method method;
    private final Object[] arguments;
    
    MethodInvokable(Method method, Object[] arguments) {
      super(method);
      this.method = method;
      this.arguments = Arrays.copyOf(arguments, arguments.length);
    }

    @Override
    @SuppressWarnings("unchecked")
    E invokeInternal(T receiver, Object... args)
        throws InvocationTargetException, IllegalAccessException {
      return (E) method.invoke(receiver, arguments);
    }
  }

  static final class InvokableSelector<T, E> implements Selector<T, E> {
    private final Invokable<T, E> invokable;

    InvokableSelector(Invokable<T, E> invokable) {
      this.invokable = invokable;
    }

    @Override
    public E select(T value) {
      try {
        return invokable.invoke(value);
      } catch (Exception ex) {
        throw new RuntimeException("InvokableSelector invocation error");
      }
    }
  }

  static final class InvokableConclusionPredicate<T> implements ConclusionPredicate<T> {
    private final Invokable<T, Boolean> invokable;

    public InvokableConclusionPredicate(Invokable<T, Boolean> invokable) {
      this.invokable = invokable;
    }

    @Override
    public boolean apply(T value) {
      try {
        return invokable.invoke(value);
      } catch (Exception ex) {
        throw new RuntimeException("InvokableConclusionPredicate invocation error", ex);
      }
    }
  }
}
