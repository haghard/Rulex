/*
 * Copyright (C) 2013 The Conclusions Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ru.rulex.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public abstract class RulexVerb<T> extends TypeSafeMatcher<T>
{
  private SelectorDelegate<T> delegate;

  RulexVerb(SelectorDelegate<T> delegate)
  {
    this.delegate = delegate;
  }

  protected SelectorDelegate<T> getDelegate()
  {
    return delegate;
  }

  public void setDelegate(SelectorDelegate<T> delegate)
  {
    this.delegate = delegate;
  }

  public RulexVerb<T> and( Matcher<T> matcher )
  {
    return RulexDsl.and( this, matcher );
  }

  public RulexVerb<T> or( Matcher<T> matcher )
  {
    return RulexDsl.or( this, matcher );
  }

  public RulexVerb<T> toStatefull()
  {
    return RulexDsl.toStatefull(this);
  }
}
