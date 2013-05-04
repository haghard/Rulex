/*
 * Copyright 2013 Project Forward Conclusion Contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.rulex.conclusion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;

public final class IteratorsFacade {

  enum IterationState {
    Regular,
    Exit;
  }

  private IteratorsFacade() {
  }

  /**
   * @param validationRuleEntry
   * @param source
   * @param <T>
   * @return Iterator<T>
   */
  public static <T> Iterator<T> whereIterator(final AssertionUnit<T> validationRuleEntry,
                                              final ImmutableList<T> source) {
    return whereIterator(validationRuleEntry, source, ImmutableSet.<T>of());
  }

  /**
   * @param validationRuleEntry
   * @param source
   * @param excepts
   * @param <T>
   * @return Iterator<T>
   */
  public static <T> Iterator<T> whereIterator(final AssertionUnit<T> validationRuleEntry,
                                              final ImmutableList<T> source, final ImmutableSet<T> excepts) {
    return new AbstractIterator<T>() {
      private final UnmodifiableIterator<T> iterator = source.iterator();

      private final ConclusionStatePathTrace pathTrace = ConclusionStatePathTrace.defaultInstance();

      private IterationState state = IterationState.Regular;

      @Override
      public boolean hasNext() {
        switch (state) {
          case Regular:
            while (iterator.hasNext()) {
              T event = iterator.next();
              if (validationRuleEntry.satisfies(pathTrace, event) && !excepts.contains(event)) {
                state = IterationState.Exit;
                return computeNext(event);
              }
            }
          case Exit: {
            return interrupt;
          }
        }
        return interrupt;
      }
    };
  }
}
