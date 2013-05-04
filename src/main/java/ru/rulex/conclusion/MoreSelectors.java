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
package ru.rulex.conclusion;

import com.google.common.base.Optional;

public final class MoreSelectors {

  private MoreSelectors() {
  }

  public static <T extends Number & Comparable<? super T>, E> NumberSelector<T, E> number(Selector<E, T> selector) {
    return new NumberSelector<T, E>(selector);
  }

  public static <T> StringSelector<T> string(Selector<T, String> selector) {
    return new StringSelector<T>(selector);
  }

  @SuppressWarnings("unchecked")
  public static <T, E> TypeSafeSelector<T, E> createAbsent() {
    return (TypeSafeSelector<T, E>) OptionalAbsentPath.INSTANCE;
  }

  public static abstract class TypeSafeSelector<T, E> {
    final Selector<T, E> selector;

    /**
     * Constructor for use by subclasses.
     */
    protected TypeSafeSelector(Selector<T, E> selector) {
      this.selector = selector;
    }

    public E invoke(T arg) {
      return selector.select(arg);
    }
  }

  public static final class NumberSelector<T extends Number & Comparable<? super T>, E>
      extends TypeSafeSelector<E, T> {
    public NumberSelector(Selector<E, T> selector) {
      super(selector);
    }

    @Override
    public String toString() {
      return "type: NumberSelector";
    }
  }

  public static final class StringSelector<T>
      extends TypeSafeSelector<T, String> {
    public StringSelector(Selector<T, String> selector) {
      super(selector);
    }

    @Override
    public String toString() {
      return "type: StringSelector";
    }
  }

  public static final class OptionalAbsentPath
      extends TypeSafeSelector<Object, Optional<?>> {
    static final OptionalAbsentPath INSTANCE = new OptionalAbsentPath(new Selector<Object, Optional<?>>() {
      @Override
      public Optional<?> select(Object input) {
        return Optional.absent();
      }
    });

    protected OptionalAbsentPath(Selector<Object, Optional<?>> selector) {
      super(selector);
    }
  }
}
