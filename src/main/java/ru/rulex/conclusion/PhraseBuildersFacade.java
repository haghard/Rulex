/*
 * Copyright (C) 2013 The Conclusions Authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file without in compliance with the License. You may obtain a copy
 * of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.rulex.conclusion;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import ru.rulex.conclusion.ParserBuilders.*;
import ru.rulex.conclusion.delegate.ProxyUtils;
import ru.rulex.conclusion.execution.ParallelStrategy;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;

import ru.rulex.conclusion.ImmutableAbstractPhrase.AllTrueImmutableGroovyPhrase;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import static ru.rulex.conclusion.execution.Callables.*;
import static ru.rulex.conclusion.guice.PhraseDslBuilders.val;

/**
 * <pre>
 *                         <b> Classes hierarchy (single object oriented)</b>
 *                            __________________________________________                                  __________________
 *                           |   AbstractEventOrientedPhraseBuilder    |-------------------------------->|  AbstractPhrase |
 *                           |_________________________________________|                                 |_________________|
 *                              |                |             |                                         ___________|____________
 *                          ____|________________|_____________|______                                  |                       |
 *                         |    BaseEventOrientedPhraseBuilder       |
 *                         |_________________________________________|
 *                              |  |             |             |
 *                              |  |             |             |
 *         _____________________|__|_____________|_____________|________________________________
 *        |                     |  |                         |       |                       |
 *        |        _____________|__|____________________     |       |            ___________|_____________________
 *        |       |   GuiceEventOrientedPhrasesBuilder |     |       |           |    DaggerImmutableEventPhrasesBuilder    |
 *        |       |____________________________________|     |       |           |_________________________________|
 *  ______|_____________________|_ |  _______________________|_______|_____________
 * | EventOrientedPhrasesBuilder | | | EventOrientedFactConsequencePhrasesBuilder|
 * |_____________________________| | |___________________________________________|
 *               _______________|__|__________________       |
 *              | SimpleEventOrientedPhrasesBuilder  |       |
 *              |____________________________________|       |
 *                              |                            |
 *                             <b> Classes hierarchy (collection oriented)</b>
 *                             _|____________________________|____________                            ___________________
 *                            |   AbstractIterableOrientedPhrasesBuilder |-------------------------->| IterablePhrases  |
 *                            |__________________________________________|                           |__________________|
 *                              |                  |         |                                     ____________|_____________
 *                         _____|__________________|_________|__________                          |                         |
 *   _____________________|_____|________________________    |         |
 *  | AbstractConclusionCollectionExecutionPhrasesBuilder|   |         |
 *  |____________________________________________________|   |         |
 *                       |      |                            |         |
 *                       |      |                            |         |
 *                       |      |                            |         |
 *                       |      |                ____________|_________|____________________
 *                       |      |               |AbstractImperativeExecutionPhrasesBuilder |
 *                       |      |               |__________________________________________|
 *  _____________________|______|______________              |        |
 * |IterableValidationCollectionPhrasesBuilder|              |        |
 * |__________________________________________|              |        |
 *                            |                       _______|________|__________________
 * ___________________________|_________             | ImperativeExecutionPhrasesBuilder|
 *|BaseGroovyEventOrientedPhraseBuilder|             |__________________________________|
 *|____________________________________|                     |
 *             |                          ____________________|_______________________
 * ____________|________________________ | AbstractMutableEventOrientedPhraseBuilder |
 *|GroovyEventOrientedPhrasesBuilder   | |___________________________________________|
 *|____________________________________|                  |
 *                                                        |
 *                                           _____________|__________________
 *                                          |DaggerMutableEventPhraseBuilder|
 * </pre>                                   |_______________________________|
 */
public final class PhraseBuildersFacade
{

  private PhraseBuildersFacade()
  {
  }

  /**
   * {@code AbstractEventOrientedPhraseBuilder} class is a root of class
   * hierarchy define a base abstraction for single event oriented
   * PhraseBuilders
   * <p/>
   * This class extended for implementation specific behavior.
   * <p/>
   * <p/>
   * <b>Note:</b> {@code AbstractEventOrientedPhraseBuilder}'s class hierarchy
   * is a abstraction part of <b>pattern bridge </b>. Every subclass relies on a
   * set of abstract operations, where several implementations of the set of
   * abstract operations are possible. The implementation part (Bridge Pattern)
   * is a {@code AbstractPhrase} hierarchy
   * <p/>
   * <p/>
   * See wiki article on <a href=<a
   * href="http://en.wikipedia.org/wiki/Bridge_pattern"> Builder Design
   * Pattern</a>.
   */
  public static abstract class AbstractEventOrientedPhraseBuilder<T> {

    protected final AbstractPhrase<T, ?> phrase;

    protected ParallelStrategy<Boolean> pStrategy;

    private static final Logger logger = Logger.getLogger( AbstractEventOrientedPhraseBuilder.class );

    private AbstractEventOrientedPhraseBuilder( AbstractPhrase<T, ?> phrase,
                                              ParallelStrategy<Boolean> pStrategy )
    {
      this.phrase = phrase;
      this.pStrategy = pStrategy;
    }

    protected abstract AbstractPhrase<T, ?> getPhrase();

    protected void setParallelStrategy( ParallelStrategy<Boolean> pStrategy )
    {
      this.pStrategy = pStrategy;
    }

    protected void setEvent( T event )
    {
      getPhrase().setEvent( event );
    }

    public Boolean sync( final T event )
    {
      try
      {
        return call( obtain( pStrategy.lift( createFunction( event ) ).apply( this ) ) );
      }
      catch ( Exception e )
      {
        logger.error( e.getMessage() );
      }
      return false;
    }

    public CheckedFuture<Boolean, PhraseExecutionException> async( final T event )
    {
      return pStrategy.lift( this.createFunction( event ) ).apply( this );
    }

    public Boolean sync( final T event, Runnable callback )
    {
      return sync( event, callback, MoreExecutors.sameThreadExecutor() );
    }

    public Boolean sync( final T event, Runnable callback, Executor callbackExecutor )
    {
      try
      {
        return call( obtain( pStrategy.lift( createFunction( event ) ).apply( this ), callback, callbackExecutor ) );
      } catch ( Exception ex )
      {
        logger.error( ex.getMessage() );
      }
      return false;
    }

    protected <E extends AbstractEventOrientedPhraseBuilder<T>> ConclusionFunction<E, Boolean> createFunction(
            final T event )
    {
      return new ConclusionFunction<E, Boolean>()
      {
        @Override
        public Boolean apply( E arg )
        {
          setEvent( event );
          return getPhrase().evaluate();
        }
      };
    }
  }

  public static abstract class BaseEventOrientedPhraseBuilder<T> extends AbstractEventOrientedPhraseBuilder<T>
  {
    private BaseEventOrientedPhraseBuilder( ImmutableAbstractPhrase<T> phrase,
                                              ParallelStrategy<Boolean> pStrategy )
    {
      super( phrase, pStrategy );
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ImmutableAbstractPhrase<T> getPhrase()
    {
      return ( ImmutableAbstractPhrase<T> ) phrase;
    }
  }

  public static abstract class BaseGroovyEventOrientedPhraseBuilder<T> extends AbstractEventOrientedPhraseBuilder<T>
  {
    private BaseGroovyEventOrientedPhraseBuilder( AllTrueImmutableGroovyPhrase<T> phrase,
                                            ParallelStrategy<Boolean> pStrategy )
    {
      super( phrase, pStrategy );
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AllTrueImmutableGroovyPhrase<T> getPhrase()
    {
      return ( AllTrueImmutableGroovyPhrase<T> ) phrase;
    }
  }
  /**
   * <p>
   * The class used for creation, configuration and running evaluation on
   * manually created predicates. Main usage configure anonymous inner class
   * instance.
   * </p>
   * <p/>
   * <pre>
   * <b>Usage example in {@code TestEventOrientedPhraseBuilders}</b>
   * </pre>
   */
  public static abstract class EventOrientedPhrasesBuilder<T> extends BaseEventOrientedPhraseBuilder<T>
  {

    public EventOrientedPhrasesBuilder()
    {
      super( ImmutableAbstractPhrase.<T>all(), ParallelStrategy.<Boolean>serial() );
      build();
    }

    /**
     * method which should be override in all anonymous class instances and
     * include logic for construction underline engine with predicates.
     */
    protected abstract void build();

    protected WithParser<T> configure( Class<T> clazz, String description )
    {
      return configure( ParallelStrategy.<Boolean>serial(), clazz, description );
    }

    protected WithParser<T> configure( ParallelStrategy<Boolean> pStrategy, Class<T> clazz,
                                       String description )
    {
      setParallelStrategy( pStrategy );
      return ParserBuilders.newWithParser( getPhrase(), clazz, description );
    }
  }

  /**
   *
   */
  public static abstract class SimpleEventOrientedPhrasesBuilder<T> extends BaseEventOrientedPhraseBuilder<T>
  {

    public SimpleEventOrientedPhrasesBuilder()
    {
      super( ImmutableAbstractPhrase.<T>all(), ParallelStrategy.<Boolean>serial() );
      build();
    }

    /**
     * method which should be override in all anonymous class instances and
     * include logic for construction underline engine with predicates.
     */
    protected abstract void build();

    public SimpleWithParser as( String description )
    {
      return configure( ParallelStrategy.<Boolean>serial(), description );
    }

    protected SimpleWithParser configure( ParallelStrategy<Boolean> pStrategy,
                                          String description )
    {
      setParallelStrategy( pStrategy );
      return ParserBuilders.newSimpleWithParser( getPhrase(), description );
    }
  }

  /**
   * <pre>
   * <b>Usage example in {@code TestEventOrientedPhraseBuilders}</b>
   * </pre>
   */
  public static abstract class EventOrientedFactConsequencePhrasesBuilder<T> extends BaseEventOrientedPhraseBuilder<T>
  {

    public EventOrientedFactConsequencePhrasesBuilder()
    {
      super( ImmutableAbstractPhrase.<T>all(), ParallelStrategy.<Boolean>serial() );
      build();
    }

    /**
     * method which should be override in all anonymous subclasses and include
     * logic for construction underline phrase with predicates.
     */
    protected abstract void build();

    protected FactConsequenceParser<T> configure(
            ParallelStrategy<Boolean> pStrategy, Class<T> clazz,
            String description )
    {
      setParallelStrategy( pStrategy );
      return configure( clazz, description );
    }

    protected FactConsequenceParser<T> configure( Class<T> clazz, String description )
    {
      return ParserBuilders.newFactConsequenceParser( getPhrase(), clazz, description );
    }
  }

  public static abstract class GroovyEventOrientedPhrasesBuilder<T> extends BaseGroovyEventOrientedPhraseBuilder<T>
  {
    public GroovyEventOrientedPhrasesBuilder()
    {
      super( ImmutableAbstractPhrase.<T>allGroovy(), ParallelStrategy.<Boolean>serial());
      build();
    }

    protected abstract void build();

    protected ScriptParser configure( String description )
    {
      return configure( ParallelStrategy.<Boolean>serial(), description );
    }

    protected ScriptParser configure( ParallelStrategy<Boolean> pStrategy, String description )
    {
      setParallelStrategy( pStrategy );
      return ParserBuilders.newScriptParser( getPhrase(), description );
    }
  }

  /**
   * <p>
   * The class used for creation, configuration and running evaluation using
   * Guice managed predicates from {@code GuicefyConclusionPredicates}. Main
   * usage configure
   * {@code ru.rulex.conclusion.guice.AbstractPhrasesAnalyzerModule} class
   * static methods.
   * </p>
   * <p/>
   * <p/>
   * <pre>
   * <b>Usage example: </b>
   * {@code
   * Injector injector = Guice.createInjector(
   *    val( 8f ).more( callOn( Model.class ).getFloat() ),
   *    val( "visa1", "visa", "visa2" ).equalsAnyOff( callOn( Model.class ).getString() ));
   *
   * final GuiceImmutablePhrasesBuilder phraseBuilder =
   *       injector.getInstance(GuiceImmutablePhrasesBuilder.class);
   *
   * Boolean result = phraseBuilder.async(andFoo).checkedGet();
   * }
   * </pre>
   * <p/>
   * <b>More examples in {@code GuiceEventOrientedEngineModuleTest} </b>
   * <p/>
   * See Google Guice documentation on <a href=<a
   * href="http://code.google.com/p/google-guice/"> Google Guice DI framework
   * </a>.
   * <p/>
   * <b> Attention !!! </b> Other usages of this class are prohibited
   */
  public static final class GuiceImmutablePhrasesBuilder extends BaseEventOrientedPhraseBuilder<Object>
  {
    public <T> GuiceImmutablePhrasesBuilder( ImmutableAbstractPhrase<T> delegate )
    {
      super( (ImmutableAbstractPhrase<Object>) delegate, ParallelStrategy.<Boolean>serial() );
    }
  }

  /**
   *
   */
  public static final class DaggerImmutableEventPhrasesBuilder extends BaseEventOrientedPhraseBuilder<Object>
  {
    public DaggerImmutableEventPhrasesBuilder( ImmutableAbstractPhrase<Object> delegate )
    {
      super( delegate, ParallelStrategy.<Boolean>serial() );
    }
  }

  /**
   * 
   * @author haghard
   *
   * @param <T>
   */
  public static abstract class AbstractMutableEventOrientedPhraseBuilder<T> 
  										extends AbstractEventOrientedPhraseBuilder<T>
  {

    private AbstractMutableEventOrientedPhraseBuilder(MutableAbstractPhrase<T> phrase,
                                                      ParallelStrategy<Boolean> pStrategy)
    {
      super(phrase, pStrategy);
    }

  	@Override
  	@SuppressWarnings("unchecked")
    protected MutableAbstractPhrase<T> getPhrase()
    {
      return ( MutableAbstractPhrase<T> ) phrase;
    }
  
    public abstract <E extends AbstractMutableEventOrientedPhraseBuilder<T>> E populateFrom( VarEnvironment environment );
  }

  /**
   *
   *
   */
  public static final class DaggerMutableEventPhraseBuilder extends
          							AbstractMutableEventOrientedPhraseBuilder<Object>
  {
    public DaggerMutableEventPhraseBuilder( MutableAbstractPhrase<Object> phrase )
    {
      super( phrase, ParallelStrategy.<Boolean>serial() );
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes" })
    public DaggerMutableEventPhraseBuilder populateFrom( VarEnvironment environment )
    {
      Preconditions.checkNotNull( environment );
      //it's agly way to do this, fix later
      final Set<String> undefined = new HashSet<String>( getPhrase().availableVars() );
      undefined.removeAll( environment.environmentVars() );
      if( undefined.size() != 0 )
        throw new IllegalStateException( "Undefined variables was found: " + Joiner.on(',').join( undefined ) );

      for (String varName: environment.environmentVars())
      {
        MutableAssertionUnit unit = getPhrase().correspondingUnit( varName );
        Optional<? extends Selector<?,?>> op = environment.get( varName );
        if (op.isPresent()) {
          unit.setSelector( op.get() );
        } else {
          throw new IllegalStateException( "Selector for variable not found " + varName);
        }
      }
      return this;
    }
  }
  /**
   * {@code AbstractIterableOrientedPhrasesBuilder} class is a root of class
   * hierarchy define a base abstraction for collection oriented PhraseBuilders
   * <p/>
   * <b>Note:</b> {@code AbstractIterableOrientedPhrasesBuilder}'s class
   * hierarchy is a abstraction part of <b>pattern bridge </b>. Every subclass
   * relies on a set of abstract operations, where several implementations of
   * the set of abstract operations are possible. The implementation part
   * (Bridge Pattern) is a {@code IterablePhrases} hierarchy
   * <p/>
   * See wiki article on <a href=<a
   * href="http://en.wikipedia.org/wiki/Bridge_pattern"> Builder Design
   * Pattern</a>.
   * <p/>
   * Class similar to {@code AbstractEventOrientedPhraseBuilder} by
   * functionality, but used for evaluation on Iterable.
   *
   * @see PhraseBuildersFacade.IterableOrientedPhrasesBuilder
   * @see PhraseBuildersFacade.ImperativeIterableOrientedPhrasesBuilder
   */
  public static abstract class AbstractIterableOrientedPhrasesBuilder
  {

    protected ParallelStrategy<Boolean> pStrategy;

    protected void setParallelStrategy(
            ParallelStrategy<Boolean> pStrategy )
    {
      this.pStrategy = pStrategy;
    }

    /**
     * Submits a event instance for execution and initiate an asynchronous
     * computation returns {@code ListenableFuture<Boolean>}
     * <p/>
     * The CheckedFuture's <tt>getChecked</tt> method will return the task's
     * result upon successful completion in blocking way.
     * <p/>
     * The CheckedFuture's <tt>addListener</tt> method will return the task's
     * result upon successful completion in async way.
     *
     * @param <T>
     * @return CheckedFuture<Boolean> representing the pending results of the
     *         task.
     */
    public abstract <T> CheckedFuture<Boolean, PhraseExecutionException> async(
            final Iterable<T> collection );

    /**
     * @param collection
     */
    public abstract <T> void setIterable( Iterable<T> collection );

    /**
     * @param iterable
     * @return
     */
    public abstract <T> Boolean sync( final Iterable<T> iterable );

    /**
     * @param iterable
     * @param callback
     * @return
     */
    public <T> Boolean sync( final Iterable<T> iterable, Runnable callback )
    {
      return sync( iterable, callback, MoreExecutors.sameThreadExecutor() );
    }

    /**
     * @param callback
     * @param service
     * @return Boolean
     */
    public abstract <T> Boolean sync( final Iterable<T> collection, Runnable callback,
                                      ListeningExecutorService service );

    /**
     * @return AbstractIterablePhrases
     */
    protected abstract <T> IterablePhrases<T> getIterablePhrase();

    /**
     * @param iterable
     * @return ConclusionFunction<? extends
     *         AbstractConclusionCollectionPhrasesBuilder, Boolean>
     */
    protected <T, E extends AbstractIterableOrientedPhrasesBuilder> ConclusionFunction<E, Boolean> makePhraseFunction(
            final Iterable<T> iterable )
    {
      return new ConclusionFunction<E, Boolean>()
      {
        @Override
        public Boolean apply( E arg )
        {
          setIterable( iterable );
          return getIterablePhrase().evaluate();
        }
      };
    }
  }

  /**
   * {@code AbstractIterableOrientedPhrasesBuilderImpl} abstract class which
   * extends hierarchy for iterable oriented PhraseBuilders
   * <p/>
   * <p/>
   * <b>Note:</b> This class cannot be instantiated directly. It's just
   * implements main behavior
   * <p/>
   * For instantiation use subclasses.
   */
  private abstract static class AbstractIterableOrientedPhrasesBuilderImpl extends
          AbstractIterableOrientedPhrasesBuilder
  {

    protected final IterablePhrases<?> delegate;

    private static final Logger logger = Logger
            .getLogger( AbstractIterableOrientedPhrasesBuilderImpl.class );

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> void setIterable( Iterable<T> collection )
    {
      getIterablePhrase().setIterable( ( Iterable<Object> ) collection );
    }

    private <T> AbstractIterableOrientedPhrasesBuilderImpl( IterablePhrases<T> delegate,
                                                            ParallelStrategy<Boolean> pStrategy )
    {
      this.delegate = delegate;
      this.pStrategy = pStrategy;
    }

    @Override
    public <T> CheckedFuture<Boolean, PhraseExecutionException> async( final Iterable<T> iterable )
    {
      return pStrategy.lift( makePhraseFunction( iterable ) ).apply( this );
    }

    public <T> Boolean sync( final Iterable<T> iterable )
    {
      try
      {
        return call( obtain( pStrategy.lift( makePhraseFunction( iterable ) ).apply( this ) ) );
      } catch ( PhraseExecutionException e )
      {
        logger.error( e.getMessage() );
      } catch ( Exception e )
      {
        logger.error( e.getMessage() );
      }
      return false;
    }

    @Override
    public <T> Boolean sync( final Iterable<T> iterable, Runnable callback,
                             ListeningExecutorService service )
    {
      try
      {
        return call( obtain( pStrategy.lift( makePhraseFunction( iterable ) ).apply( this ),
                callback, service ) );
      } catch ( PhraseExecutionException e )
      {
        logger.error( e.getMessage() );
      } catch ( Exception e )
      {
        logger.error( e.getMessage() );
      }
      return false;
    }
  }

  /**
   * <p>
   * Go configure Iterate, except defined and and stop after first success.
   * <p>
   * This class to direct usage configure anonymous inner class instance in client
   * code
   * </p>
   * <p/>
   * <pre>
   * See usage in {@code IterablePhrasesBuilderTest}
   *
   * Class for extending on client side
   */
  public static abstract class IterableOrientedPhrasesBuilder extends
          AbstractIterableOrientedPhrasesBuilderImpl
  {

    public IterableOrientedPhrasesBuilder()
    {
      super( AbstractIterablePhrases.defaultInstance(), ParallelStrategy.<Boolean>serial() );
      build();
    }

    @Override
    protected <T> AbstractIterablePhrases<T> getIterablePhrase()
    {
      return ( AbstractIterablePhrases<T> ) delegate;
    }

    /**
     * method which should be override in all anonymous subclasses and include
     * logic for construction underline phrase with predicates.
     */
    protected abstract void build();

    protected <T> IterableParser<T> configure( Class<T> clazz, String description )
    {
      return new IterableParserBuilder<T>( this.<T>getIterablePhrase(), clazz, description );
    }

    protected <T> IterableParser<T> configure( ParallelStrategy<Boolean> pStrategy, Class<T> clazz,
                                               String description )
    {
      setParallelStrategy( pStrategy );
      return configure( clazz, description );
    }
  }

  /**
   * Imperative way for evaluation :
   * <p>
   * We define and pass to the {@code ImperativeExecutionPhrasesBuilder} two
   * instances
   * <p>
   * 1. {@code ConclusionPredicate<Delegate<FluentConclusionPredicate>> } -
   * Simple or complex predicate with conditional logic
   * <p>
   * 2. {@code Delegate<FluentConclusionPredicate> } phrase, this is the place
   * where in runtime instance of FluentConclusionPredicate will be passed to in
   * {@code Delegate<T>.execute(T... args)} method. This give you opportunity to
   * define evaluation way based on your FluentConclusionPredicate.
   * <p>
   * <p/>
   * <pre>
   *  <b> Simple example: </b>
   *  {@code
   *   final Delegate<FluentConclusionPredicate> targetDelegate = new Delegate<FluentConclusionPredicate>() {
   *    final Iterable<Object> collection;
   *
   *    public void setIterable(Iterable<?> collection) {
   *      this.collection = ImmutableList.copyOf(collection);
   *    }
   *
   *    public boolean execute(FluentConclusionPredicate... fluentPredicate) {
   *      Preconditions.checkArgument(fluentPredicate[0] != null, "null FluentConclusionPredicate");
   *      for (Object object : collection) {
   *        if (fluentPredicate[0].apply(object)) {
   *          return true;
   *        }
   *      }
   *      return false;
   *    }
   *  };
   * }
   * </pre>
   * <p/>
   * {@code Delegate<T>execute(FluentConclusionPredicate...)} is a varargs
   * method, you can pass complete FluentConclusionPredicate
   * <p>
   * {@code
   * final ConclusionPredicate<Delegate<FluentConclusionPredicate>> startEndPredicates =
   * DelegateFactory.<FluentConclusionPredicate>values(
   * fluent().eq(4, Entity.class, Integer.class, "getInteger"),
   * fluent().eq(5, Entity.class, Integer.class, "getInteger")
   * .or(fluent().eq(55, Entity.class, Integer.class, "getInt")));
   * <p/>
   * <p/>
   * final Delegate<FluentConclusionPredicate> targetDelegate = new
   * Delegate<FluentConclusionPredicate>() Iterable<Object> collection; public
   * void setContent(Iterable<?> collection) this.collection =
   * ImmutableList.copyOf(collection); }
   * <p/>
   * public boolean execute(FluentConclusionPredicate... arguments) {
   * assertTrue(collection.size() == 4); assertTrue(arguments.length == 2);
   * return arguments[0].apply(collection.get(0)) &&
   * arguments[1].apply(collection.get(collection.size()-1)); } }; }
   * </p>
   * See usage in {@code TestImperativeIterableOrientedPhrasesBuilder} <b>Class
   * for extending on client side </b>
   */
  // TO DO : replace Delegate.execute(T...) varargs method with typesafe
  // variation base on BiMap for example
  public static abstract class ImperativeIterableOrientedPhrasesBuilder extends
          AbstractIterableOrientedPhrasesBuilderImpl
  {

    public <T> ImperativeIterableOrientedPhrasesBuilder()
    {
      super( AbstractImperativePhrases.delegatePhrases(), ParallelStrategy
              .<Boolean>serial() );
      build();
    }

    /**
     * method which should be override in all anonymous class instances and
     * include logic for construction underline engine with predicates.
     */
    protected abstract void build();

    @Override
    protected <T> AbstractImperativePhrases<T> getIterablePhrase()
    {
      return ( AbstractImperativePhrases<T> ) delegate;
    }

    protected <T, E> DelegateParser<T, E> configure(
            ParallelStrategy<Boolean> pStrategy, Class<T> clazz,
            Class<E> argClass, String description )
    {
      setParallelStrategy( pStrategy );
      return configure( clazz, argClass, description );
    }

    protected <T, E> DelegateParser<T, E> configure( Class<T> clazz, Class<E> argClass,
                                                     String description )
    {
      return ParserBuilders.newDelegateParser( this.<T>getIterablePhrase(), clazz, description );
    }
  }

  public static VarEnvironment environment( Object... varEntries )
  {
    if ( !( varEntries[0] instanceof VarEntry ) )
      throw new IllegalArgumentException( "VarEntry type expected" );

    final VarEntry<?,?>[] params = ( VarEntry[] ) Array.newInstance( VarEntry.class, varEntries.length );
    System.arraycopy( varEntries, 0, params, 0, varEntries.length );
    return new VarEnvironment( params );
  }

  public static <T, E> VarEntry<T, E> var( final String pname, final E value )
  {
    return new VarEntry<T, E>()
    {{
      this.name = pname;
      this.selector = ProxyUtils.<T, E>toSelector( value );
    }};
  }

  static final class VarEnvironment
  {
    private ImmutableSortedMap<String, Selector<?, ?>> environmentSelectors =
            ImmutableSortedMap.of();

    public VarEnvironment( VarEntry<?,?>[] varEntryList )
    {
      final ImmutableSortedMap.Builder<String, Selector<?, ?>> builder = ImmutableSortedMap.naturalOrder();
      for ( VarEntry<?,?> varEntry : varEntryList )
        builder.put( varEntry.name, varEntry.selector );

      environmentSelectors = builder.build();
    }

    public Optional<? extends Selector<?, ?>> get( String name )
    {
      return Optional.fromNullable( environmentSelectors.get( name ) );
    }

    public ImmutableSortedSet<String> environmentVars()
    {
      return environmentSelectors.keySet();
    }
  }

  static class VarEntry<T, E>
  {
    String name;
    Selector<T, E> selector;
  }

}
