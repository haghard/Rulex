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

import ru.rulex.conclusion.execution.ParallelStrategy;

public class PhraseSettings<E, T> {
  
  private final Class<T> targetClass;
  private final String accessorName;
  private final ParallelStrategy<Object, CodedException> pStrategy;
  private final E value;
  private final ConclusionPredicate<E> predicate;
  
  
  private PhraseSettings(PhraseSettingsBuilder<E, T> builder) {
    this.targetClass = builder.targetClass; 
    this.accessorName = builder.accessorName; 
    this.value = builder.value; 
    this.pStrategy = builder.pStrategy; 
    this.predicate = builder.predicate;
  }
  
  public static <E, T> PhraseSettingsBuilder<E, T> newBuilder() {
    return new PhraseSettingsBuilder<E,T>();  
  } 
  
  static final class PhraseSettingsBuilder<E, T> {
    private Class<T> targetClass;
    private String accessorName;
    private ParallelStrategy<Object, CodedException> pStrategy;
    private E value;
    private ConclusionPredicate<E> predicate;
    
    public PhraseSettingsBuilder<E, T> value(E value) {
      this.value = value;
      return this;
    }
    
    public PhraseSettingsBuilder<E, T> predicate(ConclusionPredicate<E> predicate) {
      this.predicate = predicate;
      return this;
    }
    
    public PhraseSettingsBuilder<E, T> targetClass(Class<T> targetClass) {
      this.targetClass = targetClass;
      return this;
    }
    
    public PhraseSettingsBuilder<E, T> accessorName(String accessorName) {
      this.accessorName = accessorName;
      return this;
    }
    
    @SuppressWarnings("unchecked")
    public <U, X extends CodedException> PhraseSettingsBuilder<E, T> pStrategy(ParallelStrategy<U, X> pStrategy) {
      this.pStrategy = (ParallelStrategy<Object, CodedException>) pStrategy;
      return this;
    }
    
    public PhraseSettings<E,T> build() {
      return new PhraseSettings<E,T>(this);
    }
    
  }
  
  public Class<T> getTargetClass() {
    return targetClass;
  }

  public String getAccessorName() {
    return accessorName;
  }

  @SuppressWarnings("unchecked")
  public <U, X extends CodedException> ParallelStrategy<U, X> getParallelStrategy() {
    return (ParallelStrategy<U, X>) pStrategy;
  }
  
  public E getValue() {
    return value;
  }
  
  public ConclusionPredicate<E> getPredicate() {
    return predicate;
  }
}
