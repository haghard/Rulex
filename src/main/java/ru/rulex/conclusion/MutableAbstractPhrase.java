package ru.rulex.conclusion;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class MutableAbstractPhrase<T> implements AbstractPhrase<T, MutableAssertionUnit<T>>
{
  protected T event;

  protected Class<T> clazz;

  protected List<MutableAssertionUnit<T>> units =
          new ArrayList<MutableAssertionUnit<T>>();

  protected final ConclusionStatePathTrace conclusionPathTrace =
          ConclusionStatePathTrace.defaultInstance();

  @Override public void setEventClass( Class<T> clazz )
  {
    this.clazz = clazz;
  }

  @Override
  public void addUnit( MutableAssertionUnit<T> ruleEntry )
  {
    units.add( ruleEntry );
  }

  @Override public void setEvent( T event )
  {
    if ( clazz != null && !clazz.isAssignableFrom( event.getClass() ) )
    {
      conclusionPathTrace.addBlockingError( MessageFormat.format( "Class {0} is not a subclass of {1} ",
              event.getClass(), clazz ) );
    }

    this.event = event;
  }

  private static final class AllTrueMutablePhrase<T> extends MutableAbstractPhrase<T>
  {

    @Override public Boolean evaluate()
    {
      if ( units.size() == 0 ) return Boolean.FALSE;

      for (AssertionUnit<T> unit : units)
      {
        if ( ! unit.isSatisfies( conclusionPathTrace, event ) )
        {
          return Boolean.FALSE;
        }
      }
      return Boolean.TRUE;
    }
  }

  private static final class AnyTrueMutablePhrases<T> extends MutableAbstractPhrase<T>
  {

    @Override public Boolean evaluate()
    {
      for (AssertionUnit<T> unit : units)
      {
        if ( unit.isSatisfies( conclusionPathTrace, event ) )
        {
          return Boolean.TRUE;
        }
      }
      return Boolean.FALSE;
    }
  }


  public static <T> MutableAbstractPhrase<T> all()
  {
    return new AllTrueMutablePhrase<T>();
  }

  public static <T> MutableAbstractPhrase<T> any()
  {
    return new AnyTrueMutablePhrases<T>();
  }

}
