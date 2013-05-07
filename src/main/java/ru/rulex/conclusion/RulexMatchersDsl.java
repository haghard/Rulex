/*
 * Copyright (C) 2013 The Conclusions Authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file without in compliance with the License. You may obtain a copy
 * of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.rulex.conclusion;

import static com.google.common.base.Preconditions.checkNotNull;
/**
 * 
 * Class for represent named parameters
 * 
 */
public final class RulexMatchersDsl {

  private RulexMatchersDsl() {
  }
  
  public static <T extends Comparable<? super T>> ConclusionPredicate<T> eq(final T value0) {
    return new ConclusionPredicate<T>() {
      private final T value = checkNotNull(value0);

      @Override
      public boolean apply(T argument) {
        checkNotNull(argument);
        return value.equals(argument);
      }
    };
  }

  public static <T extends Number & Comparable<? super T>> ConclusionPredicate<T> greaterThan(final T value0) {
    return new ConclusionPredicate<T>() {
      private final T value = checkNotNull(value0);

      @Override
      public boolean apply(T argument) {
        checkNotNull(argument);
        return value.compareTo(argument) > 0;
      }
    };
  }

  public static <T extends Number & Comparable<? super T>> ConclusionPredicate<T> lessThan(final T value0) {
    return new ConclusionPredicate<T>() {
      private final T value = checkNotNull(value0);

      @Override
      public boolean apply(T argument) {
        checkNotNull(argument);
        return value.compareTo(argument) < 0;
      }
    };
  }

  public static <T extends Number & Comparable<? super T>> ConclusionPredicate<T> greaterOrEq(final T value0) {
    return new ConclusionPredicate<T>() {
      private final T value = checkNotNull(value0);

      @Override
      public boolean apply(T argument) {
        checkNotNull(argument);
        return value.compareTo(argument) >= 0;
      }
    };
  }

  public static <T extends Number & Comparable<? super T>> ConclusionPredicate<T> lessOrEq(final T value0) {
    return new ConclusionPredicate<T>() {
      private final T value = checkNotNull(value0);

      @Override
      public boolean apply(T argument) {
        checkNotNull(argument);
        return value.compareTo(argument) <= 0;
      }
    };
  }

  /**
   * Method to simulate value named parameters
   * Static factory methods are convenient way to simulate named parameters in Java 
   */
  public static <T extends Number & Comparable<? super T>> Argument<T> argument(T value) {
    return new Argument<T>(value);
  }

  /**
   * Method to simulate property descriptor parameters
   * Static factory methods are convenient way to simulate named parameters in Java
   */
  public static <T> AccessorDescriptor<T> descriptor(Class<T> clazz, String method) {
    return new AccessorDescriptor<T>(clazz, method);
  }

  static final class Argument<T extends Number & Comparable<? super T>> {
    private final T value;
    
    public T getArgumentClazz() {
      return value;
    }

    Argument(T value) {
      this.value = checkNotNull(value);
    }
  }

  /**
   * Method to simulate named parameters
   * 
   */
  static final class AccessorDescriptor<T> {
    private final Class<T> clazz;
    private final String method;

    public Class<T> getClazz() {
      return clazz;
    }

    public String getMethod() {
      return method;
    }
    
    AccessorDescriptor(Class<T> clazz, String method) {
      this.clazz = checkNotNull(clazz);
      this.method = checkNotNull(method);
    }
  }
}
