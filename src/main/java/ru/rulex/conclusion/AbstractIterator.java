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

import java.util.Iterator;

/**
 * @param <T>
 */
public abstract class AbstractIterator<T>
    implements Iterator<T> {

  private T current;

  protected static final boolean interrupt = false;

  protected AbstractIterator() {
  }

  protected final boolean computeNext(T current) {
    this.current = current;
    return true;
  }

  @Override
  public T next() {
    return current;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Can't remove elements");
  }
}
