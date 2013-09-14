package ru.rulex.external;

import org.apache.log4j.Logger;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.Selector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class JvmLanguageUtils
{

  private static final Logger logger = Logger.getLogger( JvmLanguageUtils.class );

  private final static Map<Class<?>, JvmBasedLanguageAdapter> languageAdaptors = new HashMap<Class<?>, JvmBasedLanguageAdapter>();

  /**
   * External JVM language support, like in RxJava
   *
   */
  static
  {
    /* look for supported languages if they are in the classpath */
    loadLanguageAdaptor( "GroovyLanguageAdapter" );
    // loadLanguageAdaptor("JRuby");
    // loadLanguageAdaptor("Clojure");
    // loadLanguageAdaptor("Scala");
  }

  private static boolean loadLanguageAdaptor( String className )
  {
    try
    {
      Class<?> c = Class.forName( "ru.rulex.external." + className );
      JvmBasedLanguageAdapter a = (JvmBasedLanguageAdapter) c.newInstance();
      registerLanguageAdaptor( a.getFunctionClass(), a );
      logger.info( "Successfully loaded function language adaptor: " + className );
    }
    catch (ClassNotFoundException e)
    {
      logger.info( "Could not find function language adaptor: " + className );
      return false;
    }
    catch (Exception e)
    {
      logger.error( "Failed trying to initialize function language adaptor: " + className, e );
      return false;
    }
    return true;
  }

  public static void registerLanguageAdaptor( Class<?> functionClass,
                                              JvmBasedLanguageAdapter adaptor )
  {
    if ( functionClass.getPackage().getName().startsWith( "java." ) )
      throw new IllegalArgumentException(
              "FunctionLanguageAdaptor implementations can not specify java.lang.* classes." );
    languageAdaptors.put( functionClass, adaptor );
  }

  public static void removeLanguageAdaptor( Class<?> functionClass )
  {
    languageAdaptors.remove( functionClass );
  }

  public static Collection<JvmBasedLanguageAdapter> getRegisteredLanguageAdaptors()
  {
    return languageAdaptors.values();
  }

  // check for language adaptor
  private static JvmBasedLanguageAdapter findLanguageAdapter( Object closure )
  {
    for (final Class<?> c : languageAdaptors.keySet())
    {
      if ( c.isInstance( closure ) )
      {
        final JvmBasedLanguageAdapter la = languageAdaptors.get( c );
        return la;
      }
    }
    throw new RuntimeException( "Unsupported closure type: " + closure.getClass().getSimpleName() );
  }

  public static <T> ConclusionPredicate<T> toJavaPredicate( final Object predicate )
  {
    if ( predicate == null )
      throw new RuntimeException( "predicate is null. Can't send arguments to null predicate." );
    final JvmBasedLanguageAdapter la = findLanguageAdapter( predicate );
    return new ConclusionPredicate<T>()
    {
      @Override
      public boolean apply( Object argument )
      {
        Object result = la.call( predicate, new Object[] { argument } );
        if (result instanceof Boolean) 
          return ((Boolean)result).booleanValue();
        
        throw new IllegalArgumentException("result should be boolean");
      }
    };
  }

  public static <E, U extends Comparable<? super U>> Selector<E, U> toJavaSelector(
          final Object selector )
  {
    if ( selector == null )
      throw new RuntimeException( "selector is null. Can't send arguments to null selector." );
    final JvmBasedLanguageAdapter la = findLanguageAdapter( selector );
    return new Selector<E, U>()
    {
      @Override
      @SuppressWarnings("unchecked")
      public U select( Object argument )
      {
        return (U) la.call( selector, new Object[] { argument } );
      }
    };
  }

}
