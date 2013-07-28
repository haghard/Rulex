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

import com.google.common.collect.ImmutableList;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InstanceBinding;

import ru.rulex.conclusion.AbstractPhrase;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.FluentConclusionPredicate;
import ru.rulex.conclusion.Phrases;
import ru.rulex.conclusion.Selector;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.GuiceEventOrientedPhrasesBuilder;

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
public abstract class AbstractPhrasesAnalyzerModule extends AbstractModule
{

  protected final ImmutableList<Element> elements;

  protected final AbstractPhrase<?> phrase;

  public static final Key<ConclusionPredicate> OR_KEY = Key.get( ConclusionPredicate.class,
      named( "disjunction" ) );

  protected AbstractPhrasesAnalyzerModule( ImmutableList<Element> elements, AbstractPhrase<?> phrase )
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
    public BindingVisitor asVisitor( final AbstractPhrase<?> phrase )
    {
      return new BindingVisitor()
      {
        @Override
        @SuppressWarnings(
        { "unchecked", "rawtypes" })
        public void visitBinding( final SinglePredicateInjectionRequest binding )
        {
          Injector internalInjector = createInjector( new AbstractModule()
          {
            @Override
            public void configure()
            {
              binding.setBinder( binder() );
              binding.run();
            }
          } );
          ConclusionPredicate<?> conclusionPredicate = internalInjector.getInstance( Key
              .get( GuiceGenericTypes.newGenericType( ConclusionPredicate.class, binding.getLiteral() ) ) );

          phrase.addUnit( new SimpleAssertionUnit( conclusionPredicate, binding.description() ) );
        }

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public void visitBinding( final OrPredicatesInjectionRequest binding )
        {
          Injector internalOrInjector = createInjector( new AbstractModule()
          {
            @Override
            public void configure()
            {
              binding.setBinder( binder() );
              binding.run();
            }
          } );
          ConclusionPredicate<?> conclusionPredicate = internalOrInjector.getInstance( OR_KEY );
          phrase.addUnit( new SimpleAssertionUnit( conclusionPredicate, binding.description() ) );
        }
      };
    }
  };

  /**
   *
   *
   */
  protected abstract void interceptEarlierBinding();

  /**
   * @param modules
   *          {@code Module...}
   * @return {@code Module}
   */
  public static Module $expression( Module... modules )
  {
    return new InternalDslPhrasesBuilderModule( Phrases.ALL_TRUE.withNarrowedType(), getElements( modules ) );
  }

  /**
   * @param phrase
   * @param modules
   * @return subclass {@code AbstractPhrasesAnalyzerModule}
   */
  public static Module $expression( Phrases phrase, Module... modules )
  {
    return new InternalDslPhrasesBuilderModule( phrase.withNarrowedType(), Elements.getElements( modules ) );
  }

  /**
   * @param conditionName
   * @param pvalue
   * @param selector
   * @param <E>
   * @param <T>
   * @return ConclusionPredicateModule<T>
   */
  public static <E, T extends Number & Comparable<? super T>> ConclusionPredicateModule<T> $more(
      final T pvalue, final Selector<E, T> selector, final String conditionName )
  {
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        majority( conditionName, pvalue, selector );
      }
    };
  }

  /**
   * method for use with external language
   * 
   * @param conditionName
   * @param pvalue
   * @param selector
   * @return ConclusionPredicateModule<T>
   */
  public static <E, T extends Number & Comparable<? super T>> ConclusionPredicateModule<T> $more(
      final T pvalue, final Object selector, final String conditionName )
  {
    final Selector<E, T> selector0 = FluentConclusionPredicate.toJavaSelector( selector );
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        majority( conditionName, pvalue, selector0 );
      }
    };
  }

  /**
   * 
   * @param pvalue
   * @param argument
   * @param conditionName
   * @return
   */
  public static <E, T extends Number & Comparable<? super T>> ConclusionPredicateModule<T> $more(
      final T pvalue, final T argument, final String conditionName )
  {
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        majority( conditionName, pvalue, toSelector( argument ) );
      }
    };
  }

  /**
   * 
   * @param conditionName
   * @param pvalue
   * @param selector
   * @param <E>
   * @param <T>
   * @return ConclusionPredicateModule<T>
   */
  public static <E, T extends Number & Comparable<? super T>> ConclusionPredicateModule<T> $less(
      final T pvalue, final Selector<E, T> selector, final String conditionName )
  {
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        minority( conditionName, pvalue, selector );
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
        minority( conditionName, pvalue, toSelector( argument ) );
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
    final Selector<E, T> selector0 = FluentConclusionPredicate.toJavaSelector( selector );
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        minority( conditionName, pvalue, selector0 );
      }
    };
  }

  /**
   * @param conditionName
   * @param pvalue
   * @param selector
   * @param <E>
   * @param <T>
   * @return ConclusionPredicateModule<T>
   */
  public static <E, T extends Comparable<? super T>> ConclusionPredicateModule<T> $eq( final T pvalue,
      final Selector<E, T> selector, final String conditionName )
  {
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        equality( conditionName, pvalue, selector );
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
        equality( conditionName, pvalue, toSelector( argument ) );
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
    final Selector<E, T> selector0 = FluentConclusionPredicate.<E, T> toJavaSelector( selector );
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        equality( conditionName, pvalue, selector0 );
      }
    };
  }

  /**
   * @param <T>
   * @return ConclusionPredicateModule<T>
   */
  public static <T extends Comparable<? super T>> ConclusionPredicateModule<T> $or(
      final String conditionName, final Module... modules )
  {
    return new ConclusionPredicateModule<T>()
    {
      @Override
      protected void bindPredicate()
      {
        disjunction( conditionName, modules );
      }
    };
  }

  /**
   * 
   * TO DO : implement this
   * 
   */
  static final class InternalTokenPhrasesAnalyzerModule extends AbstractPhrasesAnalyzerModule
  {

    protected InternalTokenPhrasesAnalyzerModule( ImmutableList<Element> elements, AbstractPhrase<?> phrase )
    {
      super( elements, phrase );
      // TODO Auto-generated constructor stub
    }

    @Override
    protected void interceptEarlierBinding()
    {

    }

    @Override
    protected void configure()
    {

    }

  }

  static final class InternalDslPhrasesBuilderModule extends AbstractPhrasesAnalyzerModule
  {

    private final AbstractEventOrientedPhrasesBuilder phraseBuilder;

    InternalDslPhrasesBuilderModule( AbstractPhrase<?> phrase0, List<Element> elements )
    {
      super( ImmutableList.copyOf( checkNotNull( elements ) ), phrase0 );
      this.phraseBuilder = new GuiceEventOrientedPhrasesBuilder( phrase0 );
    }

    @Override
    protected void configure()
    {
      // phrase builder class binding
      bind( AbstractEventOrientedPhrasesBuilder.class ).toInstance( phraseBuilder );
      interceptEarlierBinding();
    }

    @Override
    protected void interceptEarlierBinding()
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