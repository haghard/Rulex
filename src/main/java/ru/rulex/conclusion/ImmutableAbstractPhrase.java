package ru.rulex.conclusion;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class ImmutableAbstractPhrase<T> implements AbstractPhrase<T, ImmutableAssertionUnit<T>>
{
  protected T event;
  protected Class<T> clazz;
  protected List<ImmutableAssertionUnit<T>> units = new ArrayList<ImmutableAssertionUnit<T>>();

  protected final ConclusionStatePathTrace conclusionPathTrace =
          ConclusionStatePathTrace.defaultInstance();

  @Override
  public void setEventClass( Class<T> clazz )
  {
    this.clazz = clazz;
  }

  @Override
  public void addUnit( ImmutableAssertionUnit<T> ruleEntry )
  {
    units.add( ruleEntry );
  }

  @Override
  public void setEvent( T event )
  {
    if ( clazz != null && !clazz.isAssignableFrom( event.getClass() ) )
    {
      conclusionPathTrace.addBlockingError( MessageFormat.format( "Class {0} is not a subclass of {1} ",
              event.getClass(), clazz ) );
    }

    this.event = event;
  }

  private static final class AllTrueImmutablePhrase<T> extends ImmutableAbstractPhrase<T>
  {

    @Override
    public Boolean evaluate()
    {
      if ( units.size() == 0 ) return Boolean.FALSE;

      for (ImmutableAssertionUnit<T> unit : units)
      {
        if ( ! unit.isSatisfies( conclusionPathTrace, event ) )
        {
          return Boolean.FALSE;
        }
      }
      return Boolean.TRUE;
    }
  }

  private static final class AnyTrueImmutablePhrases<T> extends ImmutableAbstractPhrase<T>
  {
    @Override
    public Boolean evaluate()
    {
      for (ImmutableAssertionUnit<T> unit : units)
      {
        if ( unit.isSatisfies( conclusionPathTrace, event ) )
        {
          return Boolean.TRUE;
        }
      }
      return Boolean.FALSE;
    }
  }


  public static <T> ImmutableAbstractPhrase<T> all()
  {
    return new AllTrueImmutablePhrase<T>();
  }

  public static <T> ImmutableAbstractPhrase<T> any()
  {
    return new AnyTrueImmutablePhrases<T>();
  }

}
