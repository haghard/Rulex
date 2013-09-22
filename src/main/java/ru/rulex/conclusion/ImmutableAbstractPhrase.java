package ru.rulex.conclusion;

import groovy.lang.Binding;
import groovy.lang.Closure;
import ru.rulex.conclusion.groovy.GroovyAllTrueRuleDslBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class ImmutableAbstractPhrase<T> implements AbstractPhrase<T, ImmutableAssertionUnit<T>>
{
  protected T event;
  protected Class<T> clazz;
  protected final List<ImmutableAssertionUnit<T>> units = new ArrayList<ImmutableAssertionUnit<T>>();

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

  public static class AllTrueImmutableGroovyPhrase<T> extends ImmutableAbstractPhrase<T> {
    private static final String EVENT_NAME = "event";
    private static final String CLOSURE_NAME = "rule";

    Binding binding;
    GroovyAllTrueRuleDslBuilder builder;

    void setBinding( Binding binding )
    {
      this.binding = binding;
    }

    void setDslBuilder( GroovyAllTrueRuleDslBuilder builder )
    {
      this.builder = builder;
    }

    @Override
    public Boolean evaluate()
    {
      binding.setVariable(EVENT_NAME, event);
      final Closure c = (Closure) binding.getVariable(CLOSURE_NAME);
      c.setDelegate(builder);
      c.call();
      return builder.getResult();
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

  public static <T> AllTrueImmutableGroovyPhrase<T> allGroovy()
  {
    return new AllTrueImmutableGroovyPhrase<T>();
  }

}
