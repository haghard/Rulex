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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>
 * This class hierarchy is a <b> implementation </b> path of <b> pattern bridge
 * </b> http://en.wikipedia.org/wiki/Bridge_pattern
 * <p>
 * The <b> abstraction </b> path is a
 * {@code AbstractForwardConclusionPhrasesBuilder} class hierarchy
 * </p>
 * </p>
 */
public abstract class AbstractPhrase<T>
{

  protected List<AssertionUnit<T>> units = new ArrayList<AssertionUnit<T>>();

  protected final ConclusionStatePathTrace conclusionPathTrace = ConclusionStatePathTrace
      .defaultInstance();

  protected Class<T> clazz;

  public abstract void setEvent( T event );

  public abstract void addUnit( AssertionUnit<T> ruleEntry );

  protected abstract Boolean evaluate();

  public void setEventClass( Class<T> clazz )
  {
    this.clazz = clazz;
  }

  public static <T> AbstractPhrase<T> all()
  {
    return new AllTruePhrase<T>();
  }

  public static <T> AbstractPhrase<T> any()
  {
    return new AnyTruePhrases<T>();
  }

  private static final class AllTruePhrase<T> extends AbstractPhrase<T>
  {
    private T event;

    @Override
    public void addUnit( AssertionUnit<T> ruleEntry )
    {
      units.add( ruleEntry );
    }

    @Override
    public void setEvent( T event )
    {
      if ( clazz != null && !clazz.isAssignableFrom( event.getClass() ) )
      {
        conclusionPathTrace.addBlockingError( MessageFormat.format(
            "Class {0} is not a subclass of {1} ", event.getClass(), clazz ) );
      }

      this.event = event;
    }

    @Override
    protected Boolean evaluate()
    {
      if ( units.size() == 0 )
        return Boolean.FALSE;
      for (AssertionUnit<T> unit : units)
      {
        if ( !unit.satisfies( conclusionPathTrace, event ) )
        {
          return Boolean.FALSE;
        }
      }
      return Boolean.TRUE;
    }
  }

  private static final class AnyTruePhrases<T> extends AbstractPhrase<T>
  {
    private T event;

    @Override
    public void setEvent( T event )
    {
      if ( clazz != null && !clazz.isAssignableFrom( event.getClass() ) )
      {
        conclusionPathTrace.addBlockingError( MessageFormat.format(
            "Class {0} is not a subclass of {1} ", event.getClass(), clazz ) );
      }

      this.event = event;
    }

    @Override
    public void addUnit( AssertionUnit<T> ruleEntry )
    {
      units.add( ruleEntry );
    }

    @Override
    protected Boolean evaluate()
    {
      for (AssertionUnit<T> unit : units)
      {
        if ( unit.satisfies( conclusionPathTrace, event ) )
        {
          return Boolean.TRUE;
        }
      }
      return Boolean.FALSE;
    }
  }
}