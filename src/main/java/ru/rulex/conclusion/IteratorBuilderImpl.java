/*
 * Copyright 2013 Project Forward Conclusion Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

public class IteratorBuilderImpl
{

  public static class WhereIteratorElement<T> implements IteratorElement<T>
  {
    private AssertionUnit<T> conclusionPredicate;

    private ImmutableList<T> source;

    private ImmutableSet<T> excepts;

    @Override
    public Iterator<T> getIterator()
    {
      return IteratorsFacade.whereIterator( conclusionPredicate, source, excepts );
    }

    @Override
    public void accept( AssertionUnit<T> conclusionPredicate, ImmutableList<T> source,
        ImmutableSet<T> excepts )
    {
      this.conclusionPredicate = conclusionPredicate;
      this.source = source;
      this.excepts = excepts;
    }

    public static class IteratorElementBuilder<T>
    {

      private IteratorElementBuilder()
      {
      }

      public WhereIteratorElement<T> build()
      {
        return new WhereIteratorElement<T>();
      }
    }

    public static <T> IteratorElementBuilder<T> newBuilder()
    {
      return new IteratorElementBuilder<T>();
    }
  }
}
