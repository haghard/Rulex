package ru.rulex.conclusion.guice;

import groovy.lang.Closure;
import ru.rulex.conclusion.Selector;

import com.google.common.collect.ImmutableSet;

import static ru.rulex.conclusion.delegate.ProxyUtils.toSelector;
import static ru.rulex.external.JvmLanguagesSupport.convertToJavaSelector;
/**
 * 
 * @author haghard
 *
 */
public class PhraseDslBuilders
{
  /**
   * 
   * @author haghard
   *
   * @param <T>
   */
  public interface ModuleBuilder<T extends Comparable<? super T>>
  {
    ConclusionPredicateModule<T> eq( T value );
    ConclusionPredicateModule<T> eq(final Object closure );
  }

  /**
   *
   * @param <T>
   */
  public interface NullableModuleBuilder<T extends Comparable<? super T>>
  {
    ConclusionPredicateModule<T> isNotNull();
    ConclusionPredicateModule<T> isNull();
  }

  @SuppressWarnings("unchecked")
  static final class NullableModuleBuilderDefault<T extends Comparable<? super T>> implements NullableModuleBuilder<T>
  {
    private final Class<T> clazz;
    private Selector<?, T> selector;

    NullableModuleBuilderDefault( Object closure )
    {
      this.clazz = ( Class<T> ) ((Closure<T> )closure).getParameterTypes()[0];
      this.selector = convertToJavaSelector( closure );
    }

    NullableModuleBuilderDefault( T value )
    {
      this.clazz = ( Class<T> ) value.getClass();
      this.selector = toSelector( value );
    }

    @Override
    public ConclusionPredicateModule<T> isNotNull()
    {
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindIsNotNull( "notNull", clazz, selector );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> isNull()
    {
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindIsNull( "isNull", clazz, selector );
        }
      };
    }
  }

  /**
   * 
   * @author haghard
   *
   * @param <T>
   */
  @SuppressWarnings("unchecked")
  static abstract class AbstractModuleBuilder<T extends Comparable<? super T>>
          implements ModuleBuilder<T> , NullableModuleBuilder<T>
  {
    protected final T value;
    protected final Class<T> clazz;

    protected AbstractModuleBuilder( T value )
    {
      this.value = value;
      this.clazz = (Class<T>) value.getClass();
    }

    @Override public ConclusionPredicateModule<T> eq(final T argument )
    {
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindEquality( "equals-predicate", value, toSelector( argument ) );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> eq( Object closure )
    {
      final Selector<?, T> selector = convertToJavaSelector( closure );
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindEquality( "equals-predicate", value, selector );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> isNotNull()
    {
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindIsNotNull( "notNull", clazz, toSelector( value ) );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> isNull()
    {
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindIsNull( "isNull", clazz, toSelector( value ) );
        }
      };
    }
  }

  public interface NumberModuleBuilder<T extends Number & Comparable<? super T>> extends ModuleBuilder<T>
  {
    ConclusionPredicateModule<T> less( final T argument );
    ConclusionPredicateModule<T> less(final Object closure );

    ConclusionPredicateModule<T> more( final T argument );
    ConclusionPredicateModule<T> more(final Object closure );

    ConclusionPredicateModule<T> lessOrEquals ( final T argument );
    ConclusionPredicateModule<T> lessOrEquals ( final Object closure );

    ConclusionPredicateModule<T> moreOrEquals ( final T argument );
    ConclusionPredicateModule<T> moreOrEquals ( final Object closure );
  }

  static final class StringsModuleBuilder implements ModuleBuilder<String>
  {
    private final String[] values;

    StringsModuleBuilder( String[] values )
    {
      this.values = values;
    }

    @Override public ConclusionPredicateModule<String> eq(final String argument )
    {
      return new ConclusionPredicateModule<String>()
      {
        @Override protected void bindPredicate()
        {
          bindEquality( "string-equals-predicate", ImmutableSet.copyOf( values ), toSelector( argument ) );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<String> eq( Object lambdaSelector )
    {
      return null;
    }

    public ConclusionPredicateModule<String> equalsAnyOff(final String argument )
    {
      return new ConclusionPredicateModule<String>()
      {
        @Override protected void bindPredicate()
        {
          bindEqualsAnyOff( "equalsAnyOff-predicate", ImmutableSet.copyOf( values ), toSelector( argument ) );
        }
      };
    }

    public ConclusionPredicateModule<String> regExps(final String argument )
    {
      return new ConclusionPredicateModule<String>()
      {
        @Override protected void bindPredicate()
        {
          bindMultiRegExp( "regexp-predicate", ImmutableSet.copyOf( values ), toSelector( argument ) );
        }
      };
    }
  }
  /**
   * 
   * @author haghard
   *
   */
  public static final class StringExpressionModuleBuilder extends AbstractModuleBuilder<String> implements ModuleBuilder<String>
  {
    StringExpressionModuleBuilder( final String value )
    {
      super( value );
    }

    public ConclusionPredicateModule<String> regExp(final String argument )
    {
      return new ConclusionPredicateModule<String>() 
      {
        @Override protected void bindPredicate() 
        {
          bindRegExp( "regexp-predicate", value, toSelector( argument ) );
        }
      };
    }
  }

  /**
   * 
   * @author haghard
   *
   * @param <T>
   */
  public static final class DigitExpressionModuleBuilder<T extends Number & Comparable<? super T>> extends AbstractModuleBuilder<T>
          implements NumberModuleBuilder<T>
  {
    DigitExpressionModuleBuilder( T value )
    {
      super( value );
    }

    @Override
    public ConclusionPredicateModule<T> less(final T argument )
    {
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindMinority( "less-predicate", value, toSelector( argument ) );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> less(final Object lambdaSelector )
    {
      final Selector<?, T> selector = convertToJavaSelector( lambdaSelector );
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindMinority( "less-predicate", value, selector );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> more(final T argument )
    {
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindMajority( "more-predicate", value, toSelector( argument ) );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> more(final Object lambdaSelector )
    {
      final Selector<?, T> selector = convertToJavaSelector( lambdaSelector );
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindMajority( "more-predicate", value, selector );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> lessOrEquals(final T argument )
    {
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindAtMost( "less-or-equals-predicate", value, toSelector( argument ) );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> lessOrEquals( Object lambdaSelector )
    {
      final Selector<?, T> selector = convertToJavaSelector( lambdaSelector );
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindAtMost( "less-or-equals-predicate", value, selector );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> moreOrEquals(final T argument )
    {
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindAtLeast( "more-or-equals-predicate", value, toSelector( argument ) );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> moreOrEquals( Object lambdaSelector )
    {
      final Selector<?, T> selector = convertToJavaSelector( lambdaSelector );
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindAtLeast( "more-or-equals-predicate", value, selector );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> eq(final  T argument )
    {
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindMajority( "eq-predicate", value, toSelector( argument ) );
        }
      };
    }

    @Override
    public ConclusionPredicateModule<T> eq(final Object lambdaSelector )
    {
      final Selector<?, T> selector = convertToJavaSelector( lambdaSelector );
      return new ConclusionPredicateModule<T>()
      {
        @Override
        protected void bindPredicate()
        {
          bindMajority( "eq-predicate", value, selector );
        }
      };
    }
  }

  public static <T extends Number & Comparable<? super T>> NullableModuleBuilder<T> val( final Object closure )
  {
    return new NullableModuleBuilderDefault<T>( closure );
  }

  public static <T extends Number & Comparable<? super T>> DigitExpressionModuleBuilder<T> val( final T arg )
  {
    return new DigitExpressionModuleBuilder<T>( arg );
  }

  public static StringExpressionModuleBuilder val( final String arg )
  {
    return new StringExpressionModuleBuilder( arg );
  }

  public static StringsModuleBuilder val( final String... args )
  {
    return new StringsModuleBuilder( args );
  }
}
