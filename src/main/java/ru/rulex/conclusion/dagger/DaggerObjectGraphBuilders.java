package ru.rulex.conclusion.dagger;

import com.google.common.annotations.VisibleForTesting;

import dagger.ObjectGraph;
import ru.rulex.conclusion.*;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerMutableEventPhraseBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerImmutableEventPhrasesBuilder;
import java.lang.reflect.Array;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.rulex.conclusion.dagger.BindingBuilder.argFor;
import static ru.rulex.conclusion.delegate.ProxyUtils.toSelector;


public final class DaggerObjectGraphBuilders
{

  @dagger.Module(injects =  { DaggerMutableEventPhraseBuilder.class }, library = true )
  public static class DaggerMutablePhraseModule
  {
    private final MutableAbstractPhrase<?> phrase;
    private final DaggerMutableEventPhraseBuilder phraseBuilder;

    public static DaggerMutablePhraseModule mutablePhrase( ObjectGraph graph )
    {
      return compose( graph );
    }

    public static DaggerMutablePhraseModule mutablePhrase( ObjectGraph module0, ObjectGraph module1 )
    {
      return compose( module0, module1 );
    }

    public static DaggerMutablePhraseModule mutablePhrase( ObjectGraph module0, ObjectGraph module1,
                                                           ObjectGraph module2 )
    {
      return compose( module0, module1, module2 );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $eq( final T value, final String varName )
    {
      return BindingBuilder.mutableGraph( argFor( value ), LogicOperation.eq, varName );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $more( final T value, final String varName )
    {
      return BindingBuilder.mutableGraph( argFor( value ), LogicOperation.moreThan, varName );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $less( final T value, final String varName )
    {
      return BindingBuilder.mutableGraph( argFor( value ), LogicOperation.lessThan, varName );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $lessOrEquals( T value, String varName )
    {
      return BindingBuilder.mutableGraph( argFor( value ), LogicOperation.lessOrEquals, varName );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $moreOrEquals( T value, String varName )
    {
      return BindingBuilder.mutableGraph( argFor( value ), LogicOperation.moreOrEquals, varName );
    }

    private static DaggerMutablePhraseModule compose( ObjectGraph... graphs )
    {
      checkNotNull( graphs );
      ObjectGraph[] array = ( ObjectGraph[] ) Array.newInstance( ObjectGraph.class, graphs.length );
      System.arraycopy( graphs, 0, array, 0, graphs.length );
      return new DaggerMutablePhraseModule( MutableAbstractPhrase.all(), array );
    }

    private DaggerMutablePhraseModule( MutableAbstractPhrase<Object> phrase, ObjectGraph[] array )
    {
      this.phrase = phrase;
      this.phraseBuilder = new DaggerMutableEventPhraseBuilder( phrase );

      for ( ObjectGraph module : array )
        providePhrases( module );
    }

    @SuppressWarnings("unchecked")
    private void providePhrases( ObjectGraph graph )
    {
      phrase.addUnit( graph.get( MutableAssertionUnit.class ) );
    }

    @dagger.Provides
    DaggerMutableEventPhraseBuilder providePhraseBuilder()
    {
      return phraseBuilder;
    }
  }

  /**
   *
   *
   */
  @dagger.Module(injects = DaggerImmutableEventPhrasesBuilder.class, library = true )
  public static class DaggerImmutablePhraseModule
  {
    private final ImmutableAbstractPhrase<?> phrase;
    private final DaggerImmutableEventPhrasesBuilder phraseBuilder;

    public static DaggerImmutablePhraseModule immutablePhrase( ObjectGraph module )
    {
      return compose( module );
    }

    public static DaggerImmutablePhraseModule immutablePhrase( ObjectGraph module0, ObjectGraph module1 )
    {
      return compose( module0, module1 );
    }

    public static DaggerImmutablePhraseModule immutablePhrase( ObjectGraph module0, ObjectGraph module1,
                                                               ObjectGraph module2 )
    {
      return compose( module0, module1, module2 );
    }

    public static DaggerImmutablePhraseModule immutablePhrase( ObjectGraph module0, ObjectGraph module1,
                                                               ObjectGraph module2, ObjectGraph module3 )
    {
      return compose( module0, module1, module2, module3 );
    }

    public static DaggerImmutablePhraseModule immutablePhrase( ObjectGraph module0, ObjectGraph module1,
                                                               ObjectGraph module2, ObjectGraph module3,
                                                               ObjectGraph module4)
    {
      return compose( module0, module1, module2, module3, module4 );
    }

    private static DaggerImmutablePhraseModule compose( ObjectGraph... graphs )
    {
      checkNotNull( graphs );
      ObjectGraph[] array = ( ObjectGraph[] ) Array.newInstance( ObjectGraph.class, graphs.length );
      System.arraycopy( graphs, 0, array, 0, graphs.length );
      return new DaggerImmutablePhraseModule( ImmutableAbstractPhrase.all(), array );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $eq( final T value, final T argument )
    {
      return BindingBuilder.immutableGraph( argFor( value ), LogicOperation.eq, toSelector( argument ) );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $less( final T value, final T argument )
    {
      return BindingBuilder.immutableGraph( argFor( value ), LogicOperation.lessThan, toSelector( argument ) );
    }

    @VisibleForTesting
    public static <T extends Comparable<? super T>> ObjectGraph $more( final T value, final T argument )
    {
      return BindingBuilder.immutableGraph( argFor( value ), LogicOperation.moreThan, toSelector( argument ) );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $lessOrEquals( final T value, final T argument )
    {
      return BindingBuilder.immutableGraph( argFor( value ), LogicOperation.lessOrEquals, toSelector( argument ) );
    }

    @VisibleForTesting
    public static <T extends Comparable<? super T>> ObjectGraph $moreOrEquals( final T value, final T argument )
    {
      return BindingBuilder.immutableGraph( argFor( value ), LogicOperation.moreOrEquals, toSelector( argument ) );
    }

    @VisibleForTesting
    static ObjectGraph $containsAnyOff( final String[] values, String argument )
    {
      return BindingBuilder.immutableGraph( argFor( values ), LogicOperation.matchAnyOff, toSelector( argument ) );
    }

    @VisibleForTesting
    static ObjectGraph $matchesExp( String[] values, String argument )
    {
      return null;
    }

    @VisibleForTesting
    static ObjectGraph $eq( String[] values, String argument )
    {
      return BindingBuilder.immutableGraph( argFor( values ), LogicOperation.eq, toSelector( argument ) );
    }

    private DaggerImmutablePhraseModule( ImmutableAbstractPhrase<Object> phrase, ObjectGraph[] graphs )
    {
      this.phrase = phrase;
      this.phraseBuilder = new DaggerImmutableEventPhrasesBuilder( phrase );

      for ( ObjectGraph graph : graphs )
        injectPhrases( graph );
    }

    @SuppressWarnings("unchecked")
    private void injectPhrases( ObjectGraph element )
    {
      phrase.addUnit( element.get( ImmutableAssertionUnit.class ) );
    }

    @dagger.Provides
    DaggerImmutableEventPhrasesBuilder getPhraseBuilder()
    {
      return phraseBuilder;
    }
  }
}