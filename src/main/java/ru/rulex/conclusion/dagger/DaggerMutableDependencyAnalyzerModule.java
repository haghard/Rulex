package ru.rulex.conclusion.dagger;

import dagger.ObjectGraph;
import ru.rulex.conclusion.*;

import java.lang.reflect.Array;

import ru.rulex.conclusion.PhraseBuildersFacade.DaggerMutableEventPhraseBuilder;
import ru.rulex.conclusion.dagger.DaggerPredicateModule.MutableDaggerPredicateModule;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractMutableEventOrientedPhraseBuilder;
/**
 * 
 * @author haghard
 *
 */
@dagger.Module(
        injects = DaggerMutableEventPhraseBuilder.class,
        library = true )
public class DaggerMutableDependencyAnalyzerModule
{
  private final MutableAbstractPhrase<?> phrase;
  private final DaggerMutableEventPhraseBuilder phraseBuilder;

  public static <T> DaggerMutableDependencyAnalyzerModule $mutableExpression( ObjectGraph graph )
  {
    return compose( graph );
  }

  public static <T> DaggerMutableDependencyAnalyzerModule $mutableExpression( ObjectGraph module0, ObjectGraph module1 )
  {
    return compose( module0, module1 );
  }

  public static <T> DaggerMutableDependencyAnalyzerModule $mutableExpression( ObjectGraph module0, ObjectGraph module1,
                                                                              ObjectGraph module2)
  {
    return compose( module0, module1, module2 );
  }

  /**
   *
   * @param value
   * @param varName
   * @return ObjectGraph
   */
  public static <T extends Comparable<? super T>> ObjectGraph $more( final T value, final String varName )
  {
    return ObjectGraph.create(
            new DaggerPredicateModule( value , LogicOperation.moreThan ),
            new MutableDaggerPredicateModule( varName )
    );
  }
  /**
   * 
   * @param value
   * @param varName
   * @return ObjectGraph
   */
  public static <T extends Comparable<? super T>> ObjectGraph $less( final T value, final String varName )
  {
    return ObjectGraph.create(
            new DaggerPredicateModule( value , LogicOperation.lessThan ),
            new MutableDaggerPredicateModule( varName )
    );
  }

  private static <T> DaggerMutableDependencyAnalyzerModule compose( Object... modules )
  {
    ObjectGraph[] array = ( ObjectGraph[] ) Array.newInstance( ObjectGraph.class, modules.length );
    System.arraycopy( modules, 0, array, 0, modules.length );
    return new DaggerMutableDependencyAnalyzerModule( MutableAbstractPhrase.all(), array );
  }

  private DaggerMutableDependencyAnalyzerModule( MutableAbstractPhrase<Object> phrase, ObjectGraph[] array )
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

  public static final class VariableExpressionBuilder<T extends Comparable<? super T>> implements ExpressionBuilder<T>
  {
    private final String varName;

    public VariableExpressionBuilder( String varName )
    {
      this.varName = varName;
    }

    @Override
    public ObjectGraph $eq( T argument )
    {
      return null;
    }

    @Override
    public ObjectGraph $less( T argument )
    {
      return DaggerMutableDependencyAnalyzerModule.$less( argument, varName );
    }

    @Override
    public ObjectGraph $more( T argument )
    {
      return DaggerMutableDependencyAnalyzerModule.$more( argument, varName );
    }
  }

  public static ExpressionBuilder<Integer> varInt( final String varName )
  {
    return new VariableExpressionBuilder<Integer>( varName );
  }

  public static ExpressionBuilder<Float> varFloat( final String varName )
  {
    return new VariableExpressionBuilder<Float>( varName );
  }

  public static ExpressionBuilder<String> varString( final String varName )
  {
    return new VariableExpressionBuilder<String>( varName );
  }

  
}