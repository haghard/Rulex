/*
 *
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
 *
 */
package ru.rulex.conclusion.guice;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.common.collect.ImmutableList;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.InstanceBinding;

import ru.rulex.conclusion.*;
import ru.rulex.conclusion.PhraseBuildersFacade.GuiceImmutablePhrasesBuilder;
import ru.rulex.external.JvmLanguagesSupport;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.name.Names.named;
import static com.google.inject.spi.Elements.getElements;
import static com.google.inject.Guice.*;
import static ru.rulex.conclusion.delegate.ProxyUtils.toSelector;

/**
 * 
 * @author haghard
 * 
 */
public abstract class AbstractGuiceImmutablePhraseModule<T> extends AbstractModule
{
  protected final ImmutableList<Element> elements;

  protected final ImmutableAbstractPhrase<T> phrase;

  public static final Key<ConclusionPredicate> OR_KEY = Key.get( ConclusionPredicate.class, named( "disjunction" ) );

  protected AbstractGuiceImmutablePhraseModule( ImmutableList<Element> elements, ImmutableAbstractPhrase<T> phrase )
  {
    this.elements = elements;
    this.phrase = phrase;
  }

  /**
   * 
   * Visitor which intercept binded predicate injection for
   * {@code SinglePredicateInjectionRequest} or
   * {@code OrPredicatesInjectionRequest} injection, create internal injector
   * and use it for getting generic predicate instance in isolated way for
   * adding it in {@code AbstractPhrase}
   * 
   */
  private static final BindingVisitorAdapter<BindingVisitor> PREDICATE_INJECTION_INTERCEPTOR = new BindingVisitorAdapter<BindingVisitor>()
  {
    @Override
    public BindingVisitor asVisitor( final ImmutableAbstractPhrase<?> phrase )
    {
      return new BindingVisitor()
      {
        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public void visitBinding( final SinglePredicateInjectionRequest binding )
        {
          final Injector internalInjector = createInjector( new AbstractModule()
          {
            @Override
            public void configure()
            {
              binding.setBinder( binder() );
              binding.run();
            }
          } );
          final ConclusionPredicate<?> conclusionPredicate = internalInjector.getInstance( Key
              .get( GuiceGenericTypes.newGenericType( ConclusionPredicate.class, binding.getLiteral() ) ) );

          phrase.addUnit( new PredicateImmutableAssertionUnit( conclusionPredicate, binding.description() ) );
        }

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public void visitBinding( final OrPredicatesInjectionRequest binding )
        {
          final Injector internalOrInjector = createInjector( new AbstractModule()
          {
            @Override public void configure()
            {
              binding.setBinder( binder() );
              binding.run();
            }
          } );
          final ConclusionPredicate<?> conclusionPredicate = internalOrInjector.getInstance( OR_KEY );
          phrase.addUnit( new PredicateImmutableAssertionUnit( conclusionPredicate, binding.description() ) );
        }
      };
    }
  };

  /**
   *
   *
   */
  protected abstract void injectBinding();

  /**
   * @param modules
   * {@code Module...}
   * @return {@code Module}
   */
  public static <T> Module immutablePhrase( ConclusionPredicateModule<?>... modules )
  {
    return new GuiceDslImmutablePhraseModule<T>( ImmutableAbstractPhrase.<T>all(), getElements( modules ) );
  }

  /**
   * @param phrase
   * @param modules
   * @return subclass {@code AbstractPhrasesAnalyzerModule}
   */
  public static <T> Module immutablePhrase( Phrases phrase, ConclusionPredicateModule<?>... modules )
  {
    return new GuiceDslImmutablePhraseModule<T>( phrase.<T>getImmutableConclusionPhrase() , getElements( modules ) );
  }

  /**
   * method for use with external language
   * 
   * @param conditionName
   * @param value
   * @param selector
   * @return ConclusionPredicateModule<T>
   */
  public static <E, T extends Number & Comparable<? super T>> ConclusionPredicateModule<T> $more(
      final T value, final Object selector, final String conditionName )
  {
    final Selector<E, T> selector0 = JvmLanguagesSupport.convertToJavaSelector( selector );
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        bindMajority( conditionName, value, selector0 );
      }
    };
  }

  /**
   * 
   * @param value
   * @param argument
   * @param conditionName
   * @return
   */
  public static <E, T extends Number & Comparable<? super T>> ConclusionPredicateModule<T> $more(
      final T value, final T argument, final String conditionName )
  {
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        bindMajority( conditionName, value, toSelector( argument ) );
      }
    };
  }

  public static <E, T extends Number & Comparable<? super T>> ConclusionPredicateModule<T> $less(
      final T pvalue, final T argument, final String conditionName )
  {
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        bindMinority( conditionName, pvalue, toSelector( argument ) );
      }
    };
  }

  /**
   * method for use with external language
   * 
   * @param conditionName
   * @param pvalue
   * @param selector
   * @return
   */
  public static <E, T extends Number & Comparable<? super T>> ConclusionPredicateModule<T> $less(
      final T pvalue, final Object selector, final String conditionName )
  {
    final Selector<E, T> selector0 = JvmLanguagesSupport.convertToJavaSelector( selector );
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        bindMinority( conditionName, pvalue, selector0 );
      }
    };
  }

  public static <E, T extends Comparable<? super T>> ConclusionPredicateModule<T> $eq( final T pvalue,
      final T argument, final String conditionName )
  {
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        bindEquality( conditionName, pvalue, toSelector( argument ) );
      }
    };
  }

  /**
   * method for use with external language
   * 
   * @param conditionName
   * @param pvalue
   * @param selector
   * @return
   */
  public static <E, T extends Comparable<? super T>> ConclusionPredicateModule<T> $eq( final T pvalue,
      final Object selector, final String conditionName )
  {
    final Selector<E, T> selector0 = JvmLanguagesSupport.<E, T>convertToJavaSelector( selector );
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        bindEquality( conditionName, pvalue, selector0 );
      }
    };
  }

  /**
   * @param <T>
   * @return ConclusionPredicateModule<T>
   */
  public static <T extends Comparable<? super T>> ConclusionPredicateModule<T> or(
          final String conditionName, final ConclusionPredicateModule<?>... modules )
  {
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        bindDisjunction( conditionName, modules );
      }
    };
  }

  /**
   * 
   * TO DO : implement this
   * 
   */
  static final class GuiceExpressionImmutablePhraseModule<T> extends AbstractGuiceImmutablePhraseModule<T>
  {

    protected GuiceExpressionImmutablePhraseModule( ImmutableList<Element> elements, ImmutableAbstractPhrase<T> phrase )
    {
      super( elements, phrase );
      // TODO Auto-generated constructor stub
    }

    @Override
    protected void injectBinding()
    {

    }

    @Override
    protected void configure()
    {

    }

  }

  static final class GuiceDslImmutablePhraseModule<T> extends AbstractGuiceImmutablePhraseModule<T>
  {
    private final GuiceImmutablePhrasesBuilder phraseBuilder;

    GuiceDslImmutablePhraseModule( ImmutableAbstractPhrase<T> phrase0, List<Element> elements )
    {
      super( ImmutableList.<Element>copyOf( checkNotNull( elements ) ), phrase0 );
      this.phraseBuilder = new GuiceImmutablePhrasesBuilder( phrase0 );
    }

    @Override
    protected void configure()
    {
      // phrase builder class binding
      bind( GuiceImmutablePhrasesBuilder.class ).toInstance( phraseBuilder );
      injectBinding();
    }

    @Override
    protected void injectBinding()
    {
      final BindingVisitor visitor = PREDICATE_INJECTION_INTERCEPTOR.asVisitor( phrase );
      // element as a
      // SinglePredicateInjectionRequest/OrPredicatesInjectionRequest instances
      for ( Element element : elements )
      {
        element.acceptVisitor( new DefaultElementVisitor<Void>()
        {
          @Override
          public <T> Void visit( Binding<T> binding )
          {
            Key<?> bindingKey = binding.getKey();
            if ( binding instanceof InstanceBinding && bindingKey.getAnnotation() != null
                && (Named.class.isAssignableFrom( bindingKey.getAnnotationType() )) )
            {
              InstanceBinding<?> requestBinding = (InstanceBinding<?>) binding;

              if ( requestBinding.getInstance() instanceof SinglePredicateInjectionRequest )
                visitor.visitBinding( (SinglePredicateInjectionRequest) requestBinding.getInstance() );

              if ( requestBinding.getInstance() instanceof OrPredicatesInjectionRequest )
                visitor.visitBinding( (OrPredicatesInjectionRequest) requestBinding.getInstance() );
            }
            return super.visit( binding );
          }
        } );
      }
    }
  }
}