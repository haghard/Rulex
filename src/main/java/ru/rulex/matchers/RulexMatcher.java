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

public abstract class RulexMatcher<T> extends TypeSafeMatcher<T>
{
  private SelectorAdapter<T> adapter;

  RulexMatcher( SelectorAdapter<T> adapter )
  {
    this.adapter = adapter;
  }

  protected SelectorAdapter<T> getAdapter()
  {
    return adapter;
  }

  public void setAdapter( SelectorAdapter<T> adapter )
  {
    this.adapter = adapter;
  }

  public RulexMatcher<T> and( Matcher<T> matcher )
  {
    return RulexDsl.and( this, matcher );
  }

  public RulexMatcher<T> or( Matcher<T> matcher )
  {
    return RulexDsl.or( this, matcher );
  }

  public RulexMatcher<T> toStateful()
  {
    return RulexDsl.statefull( this );
  }
}
