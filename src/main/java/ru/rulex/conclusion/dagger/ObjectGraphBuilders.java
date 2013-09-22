package ru.rulex.conclusion.dagger;

import dagger.ObjectGraph;

import static com.google.common.base.Preconditions.checkNotNull;
import ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.DaggerImmutablePhraseModule;
import ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.DaggerMutablePhraseModule;

public class ObjectGraphBuilders
{

  public interface ObjectGraphBuilder<T>
  {
    ObjectGraph eq(T value);
  }

  public interface DigitObjectGraphBuilder<T extends Comparable<? super T>> extends ObjectGraphBuilder<T>
  {
    ObjectGraph less( final T argument );
    ObjectGraph more( final T argument );
    ObjectGraph lessOrEquals ( T argument );
    ObjectGraph moreOrEquals ( T argument );
  }

  public static final class StringObjectGraphBuilder implements ObjectGraphBuilder<String>
  {
    private final String value;

    StringObjectGraphBuilder( final String values )
    {
      this.value = values;
    }

    @Override
    public ObjectGraph eq( String argument )
    {
      return DaggerImmutablePhraseModule.$eq( value, argument );
    }
  }

  public static final class StringsObjectGraphBuilder implements ObjectGraphBuilder<String>
  {
    private final String[] values;

    StringsObjectGraphBuilder( final String[] values )
    {
      this.values = values;
    }

    @Override
    public ObjectGraph eq( String argument )
    {
      return DaggerImmutablePhraseModule.$eq( values, argument );
    }

    public ObjectGraph equalsAnyOff( String argument )
    {
      return DaggerImmutablePhraseModule.$equalsAnyOff( values, argument );
    }

    public ObjectGraph matchesExp( String argument )
    {
      return DaggerImmutablePhraseModule.$matchesExp( values, argument );
    }
  }

  public static final class NumberObjectGraphBuilder<T extends Number & Comparable<? super T>>
          implements DigitObjectGraphBuilder<T>
  {
    private final T value;
    private ObjectGraph objectGraph;

    public T getArgumentClazz()
    {
      return value;
    }

    private NumberObjectGraphBuilder( T value )
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

  public static final class VariableObjectGraphBuilder<T extends Comparable<? super T>> implements DigitObjectGraphBuilder<T>
  {
    private final String varName;

    public VariableObjectGraphBuilder( String varName )
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

  public static <T extends Number & Comparable<? super T>> NumberObjectGraphBuilder<T> val( final T arg )
  {
    return new NumberObjectGraphBuilder<T>( arg );
  }

  public static StringObjectGraphBuilder val( final String arg )
  {
    return new StringObjectGraphBuilder( arg );
  }

  public static StringsObjectGraphBuilder val( final String... args )
  {
    return new StringsObjectGraphBuilder( args );
  }

  public static VariableObjectGraphBuilder<Integer> varInt( final String varName )
  {
    return new VariableObjectGraphBuilder<Integer>( varName );
  }

  public static VariableObjectGraphBuilder<Float> varFloat( final String varName )
  {
    return new VariableObjectGraphBuilder<Float>( varName );
  }

  public static VariableObjectGraphBuilder<String> varString( final String varName )
  {
    return new VariableObjectGraphBuilder<String>( varName );
  }
}
