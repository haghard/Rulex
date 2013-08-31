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
package ru.rulex.conclusion.guice;

import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.ConclusionStatePathTrace;
import ru.rulex.conclusion.AssertionUnit;

public class SimpleAssertionUnit<T> implements AssertionUnit<T>
{
  
  private final ConclusionPredicate<T> predicate;
  private final String description;

  public SimpleAssertionUnit( ConclusionPredicate<T> conclusionPredicate, String exp )
  {
    this.predicate = conclusionPredicate;
    this.description = exp;
  }

  @Override
  public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, T event )
  {
    return conclusionPathTrace.isWorkingState() ? predicate.apply( event ) : false;
  }
}
