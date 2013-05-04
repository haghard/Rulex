/**
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

public abstract class AbstractImperativePhrases<T> implements IterablePhrases<T> {

  protected ImperativeAssertUnit<T> imperativeUnit;

  protected final ConclusionStatePathTrace conclusionPathTrace = 
      ConclusionStatePathTrace.defaultInstance();

  public void setIterable(Iterable<T> iterable) {
    this.imperativeUnit.setIterable(iterable);
  }

  public static <T> AbstractImperativePhrases<T> delegatePhrases() {
    return new DefaultImperativePhrases<T>();
  }

  public void setUnit(ImperativeAssertUnit<T> algorithmValidationRuleEntry) {
    this.imperativeUnit = algorithmValidationRuleEntry;
  }

  private static final class DefaultImperativePhrases<T>
      extends AbstractImperativePhrases<T> {

    @Override
    public Boolean evaluate() {
      return imperativeUnit.satisfies();
    }
  }
}
