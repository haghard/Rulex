/*
 * Copyright (C) 2013 The Conclusions Authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file without in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package ru.rulex.conclusion;

import java.io.*;
import java.util.Map;
import com.google.common.collect.ImmutableSet;

import ru.rulex.conclusion.FluentConclusionPredicate.SelectorPredicate;
import ru.rulex.conclusion.delegate.Delegate;
import ru.rulex.conclusion.delegate.ProxyUtils;
import ru.rulex.conclusion.ImmutableAbstractPhrase.AllTrueImmutableGroovyPhrase;
import ru.rulex.conclusion.groovy.GroovyAllTrueImmutableRuleDslBuilder;
import ru.rulex.conclusion.guice.PredicateImmutableAssertionUnit;
import ru.rulex.external.JvmLanguagesSupport;

import static ru.rulex.conclusion.FluentConclusionPredicate.*;
import static com.google.common.base.Preconditions.checkNotNull;

public final class ParserBuilders
{

  public interface Consequence
  {
    void action();
  }

  public interface ConsequenceSupplier
  {
    Consequence get();
  }

  public interface SimpleWithParser
  {
    <T> void shouldMatch( T argument, ConclusionPredicate<T> predicate );
  }

  public interface ScriptParser {
    void withFile( File file );
    void withScript( String script );
  }

  public static class ScriptParserImpl<T> implements ScriptParser
  {
    private final AllTrueImmutableGroovyPhrase<T> phrase;

    ScriptParserImpl(AllTrueImmutableGroovyPhrase<T> phrase)
    {
      this.phrase = phrase;
    }

    private void process( String script )
    {
      phrase.setScript( script );
      phrase.setDslBuilder( new GroovyAllTrueImmutableRuleDslBuilder<T>() );
    }

    @Override
    public void withFile( File file )
    {
      process( loadScript( file ) );
    }

    @Override
    public void withScript( String script )
    {
      process( script );
    }

    private static String loadScript( File scriptFile )
    {
      final StringBuffer buffer = new StringBuffer();
      try {
        String line;
        final BufferedReader reader = new BufferedReader(
                new InputStreamReader( new FileInputStream( scriptFile ) ) );
        while ( (line = reader.readLine()) != null )
        {
          buffer.append( line ).append( '\n' );
        }
      }
      catch (IOException ex)
      {
        throw new RuntimeException("IO error groovy script file");
      }
      return buffer.toString();
    }
  }

  public static <T> ScriptParser newScriptParser( AllTrueImmutableGroovyPhrase<T> phrase, String description )
  {
    return new ScriptParserImpl<T>( phrase );
  }

  /**
   * 
   * @author haghard
   * 
   * @param <T>
   */
  public interface WithParser<T>
  {
    /**
     * 
     * @param predicate
     */
    void shouldMatch( ConclusionPredicate<T> predicate );

    /**
     * 
     * 
     */
    <E> void shouldMatch( E argument, ConclusionPredicate<E> predicate );

    /**
     * Method for dynamic languages
     * 
     * @param predicate
     * @param selector
     */
    void shouldMatch( Object predicate, Object selector );

    /**
     * Method for dynamic languages
     * 
     * @param predicate
     * @param selector
     */
    void shouldMatch( Map<String, Object> map );

  }

  private static class SimpleWithParserImpl<T> implements SimpleWithParser
  {
    private final String description;
    private final AbstractPhrase<T, ImmutableAssertionUnit<T>> phrase;

    SimpleWithParserImpl( AbstractPhrase<T, ImmutableAssertionUnit<T>> phrase, String description )
    {
      this.phrase = phrase;
      this.description = description;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> void shouldMatch( T argument, ConclusionPredicate<T> predicate )
    {
      Selector<?, T> selector = ProxyUtils.toSelector( argument );
      ConclusionPredicate<?> p = new SelectorPredicate( predicate, selector );
      this.phrase.addUnit( new PredicateImmutableAssertionUnit( p, description ) );
    }
  }

  public static <T> SimpleWithParser newSimpleWithParser( AbstractPhrase<T, ImmutableAssertionUnit<T>> phrase, String desc )
  {
    return new SimpleWithParserImpl<T>( phrase, desc );
  }

  public static <T> WithParser<T> newWithParser( AbstractPhrase<T, ImmutableAssertionUnit<T>> phrase, Class<T> clazz,
      String description )
  {
    return new WithParserBuilder<T>( phrase, clazz, description );
  }

  private static class WithParserBuilder<T> implements WithParser<T>
  {
    private final String description;
    private final AbstractPhrase<T, ImmutableAssertionUnit<T>> phrase;

    private Class<T> clazz;

    public WithParserBuilder( AbstractPhrase<T, ImmutableAssertionUnit<T>> phrase, Class<T> clazz, String description )
    {
      this.clazz = clazz;
      this.phrase = phrase;
      this.description = description;
    }

    @Override
    public void shouldMatch( final ConclusionPredicate<T> predicate )
    {
      this.phrase.setEventClass( clazz );
      this.phrase.addUnit( new PredicateImmutableAssertionUnit<T>( predicate, description ) );
    }

    @Override
    public void shouldMatch( Map<String, Object> map )
    {
      final Object selector = map.get( "selector" );
      final Object predicate = map.get( "predicate" );
      checkIncomingParams( selector, predicate );
      convertToJavaObjectsAndInit( selector, predicate );
    }

    /**
     * 
     * @param selector
     *          ,
     * @param predicate
     * 
     */
    @Override
    public void shouldMatch( final Object selector, Object predicate )
    {
      checkIncomingParams( selector, predicate );
      convertToJavaObjectsAndInit( selector, predicate );
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void convertToJavaObjectsAndInit( final Object selector, Object predicate )
    {
      final ConclusionPredicate<Object> javaPredicate = JvmLanguagesSupport.convertToJavaPredicate( predicate );
      final Selector javaSelector = JvmLanguagesSupport.convertToJavaSelector( selector );
      final ConclusionPredicate<?> predicate0 = query( javaSelector, javaPredicate );
      this.phrase.setEventClass( clazz );
      this.phrase.addUnit( new PredicateImmutableAssertionUnit( predicate0, description ) );
    }

    private void checkIncomingParams( final Object selector, Object predicate )
    {
      checkNotNull( selector, "selector is null" );
      checkNotNull( predicate, "selector is null" );
    }

    @Override
    public <E> void shouldMatch( E argument, ConclusionPredicate<E> predicate )
    {
      Selector<T, E> selector = ProxyUtils.toSelector( argument );
      ConclusionPredicate<T> p = new SelectorPredicate<T, E>( predicate, selector );
      this.phrase.setEventClass( clazz );
      this.phrase.addUnit( new PredicateImmutableAssertionUnit<T>( p, description ) );
    }
  }

  public interface FactConsequenceParser<T>
  {

    public FactConsequenceParser<T> fact( ConclusionPredicate<T> predicate );

    public void consequence( ConsequenceSupplier supplier );
  }

  public static <T> FactConsequenceParser<T> newFactConsequenceParser( AbstractPhrase<T, ImmutableAssertionUnit<T>> phrase0,
      Class<T> clazz, String description )
  {
    return new FactConsequenceParserBuilder<T>( phrase0, clazz, description );
  }

  private static class FactConsequenceParserBuilder<T> implements FactConsequenceParser<T>
  {
    private final AbstractPhrase<T, ImmutableAssertionUnit<T>> phrase;

    private final Class<T> clazz;

    private ConclusionPredicate<T> predicate;

    public FactConsequenceParserBuilder( AbstractPhrase<T, ImmutableAssertionUnit<T>> phrase0, Class<T> clazz,
        String description )
    {
      this.phrase = phrase0;
      this.clazz = clazz;
    }

    @Override
    public FactConsequenceParser<T> fact( ConclusionPredicate<T> predicate )
    {
      this.predicate = predicate;
      return this;
    }

    @Override
    public void consequence( final ConsequenceSupplier supplier )
    {
      phrase.addUnit( new ImmutableAssertionUnit<T>()
      {
        @Override
        public boolean isSatisfies( ConclusionStatePathTrace conclusionPathTrace, final T event )
        {
          if ( conclusionPathTrace.isWorkingState() && predicate.apply( event ) )
          {
            supplier.get().action();
            return true;
          }
          return false;
        }
      } );
    }
  }

  /**
   * @param <T>
   */
  public interface DelegateParser<T, E>
  {

    public DelegateParser<T, E> lambda( ConclusionPredicate<Delegate<E>> predicate );

    public void delegate( Delegate<E> delegate );
  }

  public static <T, E> DelegateParser<T, E> newDelegateParser(
      AbstractImperativePhrases<T> conclusionTask, Class<T> clazz, String description )
  {
    return new DelegateParserBuilder<T, E>( conclusionTask, clazz, description );
  }

  /**
   * @param <T>
   */
  private static class DelegateParserBuilder<T, E> implements DelegateParser<T, E>
  {
    private final AbstractImperativePhrases<T> phrase;

    private ConclusionPredicate<Delegate<E>> predicate;

    private final String description;

    public DelegateParserBuilder( AbstractImperativePhrases<T> phrase0, Class<T> clazz,
        String description0 )
    {
      this.phrase = phrase0;
      this.description = description0;
    }

    @Override
    public DelegateParser<T, E> lambda( ConclusionPredicate<Delegate<E>> predicate )
    {
      this.predicate = predicate;
      return this;
    }

    @Override
    public void delegate( final Delegate<E> delegate )
    {
      phrase.setUnit( new ImperativeAssertUnit<T>()
      {

        @Override
        public void setIterable( Iterable<T> collection )
        {
          delegate.setContent( collection );
        }

        @Override
        public boolean satisfies()
        {
          return predicate.apply( delegate );
        }
      } );
    }
  }

  /**
   * @param <T>
   */
  public interface IterableParser<T>
  {

    public IterableParser<T> shouldMatch( ConclusionPredicate<T> predicate );

    public IterableParser<T> except( ImmutableSet<T> excepts );

    public void iteratorType( Iterators iType );

  }

  /**
   * @param <T>
   */
  public static class IterableParserBuilder<T> implements IterableParser<T>
  {

    private final AbstractIterablePhrases<T> phrase;

    private final String description;

    private ConclusionPredicate<T> predicate;

    private ImmutableSet<T> excepts = ImmutableSet.of();

    public IterableParserBuilder( AbstractIterablePhrases<T> conclusionTask, Class<T> clazz,
        String description )
    {
      this.phrase = conclusionTask;
      this.description = description;
    }

    @Override
    public IterableParser<T> shouldMatch( ConclusionPredicate<T> predicate )
    {
      this.predicate = predicate;
      return this;
    }

    public IterableParser<T> except( ImmutableSet<T> excepts )
    {
      this.excepts = ImmutableSet.copyOf( excepts );
      return this;
    }

    @Override
    public void iteratorType( Iterators iType )
    {
      this.phrase.setIteratorType( iType );
      this.phrase.setWithout( excepts );
      this.phrase.addUnit( new PredicateImmutableAssertionUnit<T>( predicate, description ) );
    }
  }
}
