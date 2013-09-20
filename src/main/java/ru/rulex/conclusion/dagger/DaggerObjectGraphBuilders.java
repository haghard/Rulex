package ru.rulex.conclusion.dagger;

import com.google.common.annotations.VisibleForTesting;

import dagger.ObjectGraph;
import ru.rulex.conclusion.*;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerMutableEventPhraseBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerImmutableEventPhrasesBuilder;
import ru.rulex.conclusion.dagger.DaggerPredicateModule.MutableDaggerPredicateModule;
import ru.rulex.conclusion.dagger.DaggerPredicateModule.ImmutableDaggerPredicateModule;
import java.lang.reflect.Array;

import static com.google.common.base.Preconditions.checkNotNull;
import static dagger.ObjectGraph.create;
import static ru.rulex.conclusion.dagger.DaggerPredicateModule.argFor;
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
      return ObjectGraph.create(
              new DaggerPredicateModule( argFor( value ) , LogicOperation.eq ),
              new MutableDaggerPredicateModule( varName ));
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $more( final T value, final String varName )
    {
      return ObjectGraph.create(
              new DaggerPredicateModule( argFor( value ) , LogicOperation.moreThan ),
              new MutableDaggerPredicateModule( varName ));
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $less( final T value, final String varName )
    {
      return ObjectGraph.create(
              new DaggerPredicateModule( argFor( value ) , LogicOperation.lessThan ),
              new MutableDaggerPredicateModule( varName ));
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $lessOrEquals( T value, String varName )
    {
      return ObjectGraph.create(
              new DaggerPredicateModule( argFor( value ) , LogicOperation.lessOrEquals ),
              new MutableDaggerPredicateModule( varName ));
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $moreOrEquals( T value, String varName )
    {
      return ObjectGraph.create(
              new DaggerPredicateModule( argFor( value ) , LogicOperation.moreOrEquals ),
              new MutableDaggerPredicateModule( varName ));
    }

    private static DaggerMutablePhraseModule compose( ObjectGraph... graphs )
    {
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
      ObjectGraph[] array = ( ObjectGraph[] ) Array.newInstance( ObjectGraph.class, graphs.length );
      System.arraycopy( graphs, 0, array, 0, graphs.length );
      return new DaggerImmutablePhraseModule( ImmutableAbstractPhrase.all(), array );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $eq( final T value, final T argument )
    {
      return create(
              new DaggerPredicateModule( argFor( value ), LogicOperation.eq ),
              new ImmutableDaggerPredicateModule( toSelector( argument ) ) );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $less( final T value, final T argument )
    {
      return create(
              new DaggerPredicateModule( argFor( value ), LogicOperation.lessThan ),
              new ImmutableDaggerPredicateModule( toSelector( argument ) ) );
    }

    @VisibleForTesting
    public static <T extends Comparable<? super T>> ObjectGraph $more( final T value, final T argument )
    {
      return create(
              new DaggerPredicateModule( argFor( value ), LogicOperation.moreThan ),
              new ImmutableDaggerPredicateModule( toSelector( argument ) ) );
    }

    @VisibleForTesting
    static <T extends Comparable<? super T>> ObjectGraph $lessOrEquals( final T value, final T argument )
    {
      return create(
              new DaggerPredicateModule( argFor( value ), LogicOperation.lessOrEquals ),
              new ImmutableDaggerPredicateModule( toSelector( argument ) ) );
    }

    @VisibleForTesting
    public static <T extends Comparable<? super T>> ObjectGraph $moreOrEquals( final T value, final T argument )
    {
      return create(
              new DaggerPredicateModule( argFor( value ), LogicOperation.moreOrEquals ),
              new ImmutableDaggerPredicateModule( toSelector( argument ) ) );
    }

    static ObjectGraph $containsAnyOff( final String[] values, String argument )
    {
      return create(
              new DaggerPredicateModule( argFor( values ), LogicOperation.matchAnyOff ),
              new ImmutableDaggerPredicateModule( toSelector( argument ) ) );
    }

    @VisibleForTesting
    static ObjectGraph $matchesExp( String[] values, String argument )
    {
      return null;
    }

    static ObjectGraph $eq( String[] values, String argument )
    {
      return null;
    }

    private DaggerImmutablePhraseModule( ImmutableAbstractPhrase<Object> phrase, ObjectGraph[] graph )
    {
      this.phrase = phrase;
      this.phraseBuilder = new DaggerImmutableEventPhrasesBuilder( phrase );

      for ( ObjectGraph graph0 : graph )
        providePhrases( graph0 );
    }

    @SuppressWarnings("unchecked")
    private void providePhrases( ObjectGraph element )
    {
      phrase.addUnit( element.get( ImmutableAssertionUnit.class ) );
    }

    @dagger.Provides
    DaggerImmutableEventPhrasesBuilder getPhraseBuilder()
    {
      return phraseBuilder;
    }
  }

  /**
   *
   * @param <T>
   */
  public static final class NumberExpressionBuilder<T extends Number & Comparable<? super T>>
          implements DigitBuilderExpression<T>
  {
    private final T value;
    private ObjectGraph objectGraph;

    public T getArgumentClazz()
    {
      return value;
    }

    private NumberExpressionBuilder( T value )
    {
      this.value = checkNotNull( value );
    }

    @Override
    public ObjectGraph less( final T argument )
    {
      return DaggerImmutablePhraseModule.$less( value, argument );
    }

    @Override
    public ObjectGraph more( final T argument )
    {
      return DaggerImmutablePhraseModule.$more( value, argument );
    }

    @Override
    public ObjectGraph lessOrEquals( T argument )
    {
      return DaggerImmutablePhraseModule.$moreOrEquals( value, argument );
    }

    @Override
    public ObjectGraph moreOrEquals( T argument )
    {
      return DaggerImmutablePhraseModule.$lessOrEquals( value, argument );
    }

    @Override
    public ObjectGraph eq( final T argument )
    {
      return DaggerImmutablePhraseModule.$eq( value, argument );
    }
  }

  public interface DigitBuilderExpression<T extends Comparable<? super T>> extends ExpressionBuilder<T>
  {
    ObjectGraph less( final T argument );
    ObjectGraph more( final T argument );
    ObjectGraph lessOrEquals ( T argument );
    ObjectGraph moreOrEquals ( T argument );
  }

  public static final class StringsBuilderExpression implements ExpressionBuilder<String>
  {
    private final String[] values;

    StringsBuilderExpression( final String[] values )
    {
      this.values = values;
    }

    @Override
    public ObjectGraph eq( String argument )
    {
      return DaggerImmutablePhraseModule.$eq( values, argument );
    }

    public ObjectGraph containsAnyOff( String argument )
    {
      return DaggerImmutablePhraseModule.$containsAnyOff( values, argument );
    }

    public ObjectGraph matchesExp( String argument )
    {
      return DaggerImmutablePhraseModule.$matchesExp( values, argument );
    }
  }

  public static final class VariableExpressionBuilder<T extends Comparable<? super T>> implements DigitBuilderExpression<T>
  {
    private final String varName;

    public VariableExpressionBuilder( String varName )
    {
      this.varName = varName;
    }

    @Override
    public ObjectGraph eq( T argument )
    {
      return DaggerMutablePhraseModule.$eq( argument, varName );
    }

    @Override
    public ObjectGraph less( T argument )
    {
      return DaggerMutablePhraseModule.$less( argument, varName );
    }

    @Override
    public ObjectGraph more( T argument )
    {
      return DaggerMutablePhraseModule.$more( argument, varName );
    }

    @Override
    public ObjectGraph lessOrEquals ( T argument )
    {
      return DaggerMutablePhraseModule.$lessOrEquals( argument, varName );
    }

    @Override
    public ObjectGraph moreOrEquals( T argument )
    {
      return DaggerMutablePhraseModule.$moreOrEquals( argument, varName );
    }
  }

  public static <T extends Number & Comparable<? super T>> NumberExpressionBuilder<T> val( final T arg )
  {
    return new NumberExpressionBuilder<T>( arg );
  }

  public static StringsBuilderExpression val( final String... arg )
  {
    return new StringsBuilderExpression( arg );
  }

  public static VariableExpressionBuilder<Integer> varInt( final String varName )
  {
    return new VariableExpressionBuilder<Integer>( varName );
  }

  public static VariableExpressionBuilder<Float> varFloat( final String varName )
  {
    return new VariableExpressionBuilder<Float>( varName );
  }

  public static VariableExpressionBuilder<String> varString( final String varName )
  {
    return new VariableExpressionBuilder<String>( varName );
  }
}