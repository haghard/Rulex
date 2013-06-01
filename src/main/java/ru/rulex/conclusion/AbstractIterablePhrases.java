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
import java.util.Iterator;

public abstract class AbstractIterablePhrases<T> implements IterablePhrases<T>
{

  protected Iterators iteratorType;

  protected ImmutableList<T> collection;

  protected AssertionUnit<T> ruleEntry;

  protected ImmutableSet<T> excepts = ImmutableSet.of();

  protected void setIteratorType( Iterators iteratorType )
  {
    this.iteratorType = iteratorType;
  }

  @Override
  public void setIterable( Iterable<T> collection )
  {
    this.collection = ImmutableList.copyOf( collection );
  }

  public void addUnit( AssertionUnit<T> ruleEntry )
  {
    this.ruleEntry = ruleEntry;
  }

  public void setWithout( ImmutableSet<T> excepts )
  {
    this.excepts = ImmutableSet.copyOf( excepts );
  }

  protected Iterator<T> getIterator()
  {
    IteratorElement<T> iteratorElement = this.iteratorType.<T> withNarrowedType();
    iteratorElement.accept( ruleEntry, collection, excepts );
    return iteratorElement.getIterator();
  }

  /**
   * Static factory method for getting {@code AbstractIterablePhrases} new
   * instance
   * 
   * @param <T>
   * @return AbstractIterablePhrases<T>
   */
  public static <T> AbstractIterablePhrases<T> defaultInstance()
  {
    return new DefaultIterablePhrases<T>();
  }

  /**
   * Static factory method for getting {@code AbstractIterablePhrases} new
   * instance
   * 
   * @param <T>
   * @return AbstractIterablePhrases<T>
   */
  public static <T> AbstractIterablePhrases<T> storableInstance()
  {
    return new StorableIterablePhrases<T>();
  }

  /**
   *
   */
  private static final class DefaultIterablePhrases<T> extends AbstractIterablePhrases<T>
  {

    @Override
    public Boolean evaluate()
    {
      boolean result = false;
      Iterator<T> iterator = getIterator();
      while (iterator.hasNext())
      {
        if (!result)
        {
          result = true;
        }
        // iteration by result here
      }
      return result;
    }
  }

  /**
   * @param <T>
   */
  private static final class StorableIterablePhrases<T> extends AbstractIterablePhrases<T>
  {

    private ImmutableList<T> lResult = ImmutableList.of();

    @Override
    public Boolean evaluate()
    {
      boolean result = false;
      Iterator<T> iterator = getIterator();

      ImmutableList.Builder<T> lBuilder = ImmutableList.builder();

      while (iterator.hasNext())
      {
        lBuilder.add( (T) iterator.next() );

        if (!result)
        {
          result = true;
        }
      }

      lResult = lBuilder.build();
      return result;
    }
  }
}