package ru.rulex.conclusion;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import ru.rulex.conclusion.groovy.GroovyAllTrueImmutableRuleDslBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String script;
    private GroovyAllTrueImmutableRuleDslBuilder<T> builder;
    private final Map<String, T> bindingMap = new HashMap<String, T>(1);

    final CompilerConfiguration config = new CompilerConfiguration();

    public AllTrueImmutableGroovyPhrase()
    {
      final ImportCustomizer importCustomizer = new ImportCustomizer();
      importCustomizer.addStaticStars( "ru.rulex.conclusion.groovy.Fields" );
      config.addCompilationCustomizers( importCustomizer );
    }

    void setScript( String script )
    {
      this.script = script;
    }

    void setDslBuilder( GroovyAllTrueImmutableRuleDslBuilder<T> builder )
    {
      this.builder = builder;
    }

    @Override
    public Boolean evaluate()
    {
      bindingMap.put( EVENT_NAME, event );
      final Binding binding = new Binding( bindingMap );
      new GroovyShell( binding, config ).evaluate( script );
      final Closure c = ( Closure ) binding.getVariable( CLOSURE_NAME );
      c.setDelegate( builder );
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
