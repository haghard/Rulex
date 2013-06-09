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
 * 
 */
package ru.rulex.conclusion.guice;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.*;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Named;
import com.google.inject.spi.*;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableAnyOffConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableEqualsConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableLessConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMoreConclusionPredicate;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.Selector;

import java.lang.reflect.Method;
import java.util.List;

import static com.google.inject.name.Names.named;
import static ru.rulex.conclusion.guice.GuiceGenericTypes.*;

@SuppressWarnings("unchecked")
public abstract class ConclusionPredicateModule<T extends Comparable<? super T>> extends AbstractModule
{

  protected abstract void bindPredicate();

  protected void configure()
  {
    bindPredicate();
  }

  /**
   * @param conditionName
   * @param value0
   * @param selector
   * @param <E>
   */
  protected void equality( final String conditionName, final T value0, final Selector<?, T> selector )
  {
    bindSinglePredicateRequest( conditionName, value0, selector, InjectableEqualsConclusionPredicate.class );
  }

  /**
   * @param conditionName
   * @param pvalue
   * @param selector
   * @param <E>
   */
  protected void majority( final String conditionName, final T pvalue, final Selector<?, T> selector )
  {
    bindSinglePredicateRequest( conditionName, pvalue, selector, InjectableMoreConclusionPredicate.class );
  }

  /**
   * @param conditionName
   * @param pvalue
   * @param selector
   * @param <E>
   */
  protected void minority( final String conditionName, final T pvalue, final Selector<?, T> selector )
  {
    bindSinglePredicateRequest( conditionName, pvalue, selector, InjectableLessConclusionPredicate.class );
  }

  /**
   * @param conditionName
   * @param modules
   */
  protected void disjunction( String conditionName, Module... modules )
  {
    bindDisjunctionRequest( conditionName, modules );
  }

  /**
   * @param conditionName
   * @param modules
   */
  private void bindDisjunctionRequest( String conditionName, Module... modules )
  {
    final ImmutableList.Builder<ElementInjectionRequest> disjunctionRequestRequests = ImmutableList.builder();
    final ImmutableList.Builder<ConclusionPredicate> disjunctionPredicates = ImmutableList.builder();
    final List<Element> elements = Elements.getElements( modules );
    for ( Element element : elements )
    {
      element.acceptVisitor( new DefaultElementVisitor<Void>()
      {
        public <T> Void visit( Binding<T> binding )
        {
          Key<?> bindingKey = binding.getKey();
          if ( binding instanceof InstanceBinding && bindingKey.getAnnotation() != null
              && (Named.class.isAssignableFrom( bindingKey.getAnnotationType() )) )
          {
            InstanceBinding<?> requestBinding = (InstanceBinding<?>) binding;
            if ( requestBinding.getInstance() instanceof SinglePredicateInjectionRequest )
            {
              disjunctionRequestRequests.add( ((ElementInjectionRequest) requestBinding.getInstance()) );
            }
          }
          return super.visit( binding );
        }
      } );
    }

    for ( ElementInjectionRequest binding : disjunctionRequestRequests.build() )
    {
      disjunctionPredicates.add( bindEarlierInjectedPredicates( binding ) );
    }

    ImmutableList<ConclusionPredicate> disjunctionPredicatesList = disjunctionPredicates.build();
    if ( disjunctionPredicatesList.size() > 0 )
    {
      bindDisjunction( conditionName, InjectableAnyOffConclusionPredicate.class, disjunctionPredicatesList );
    }
  }

  /**
   * @param binding
   * @return ConclusionPredicate<?>
   */
  private ConclusionPredicate<?> bindEarlierInjectedPredicates( final ElementInjectionRequest binding )
  {
    Injector internalInjector = Guice.createInjector( new AbstractModule()
    {
      @Override
      protected void configure()
      {
        binding.setBinder( binder() );
        binding.run();
      }
    } );
    return internalInjector.getInstance( Key.get( newGenericType( ConclusionPredicate.class,
        binding.getLiteral() ) ) );
  }

  private <E extends ConclusionPredicate<T>> void bindDisjunction( final String conditionName,
      final Class<E> predicateClass0, final ImmutableList<ConclusionPredicate> disjunctionPredicatesList )
  {
    bind( ElementInjectionRequest.class ).annotatedWith( named( conditionName ) ).toInstance(
        new OrPredicatesInjectionRequest()
        {
          private Binder binder;
          private final Class<E> predicateClass = predicateClass0;

          @Override
          public void setBinder( Binder binder )
          {
            this.binder = binder;
          }

          @Override
          public TypeLiteral<?> getLiteral()
          {
            return TypeLiteral.get( Void.class );
          }

          @Override
          public Matcher<Object> matcher()
          {
            return Matchers.any();
          }

          @Override
          public String description()
          {
            return conditionName;
          }

          @Override
          public void run()
          {
            binder.bind( immutableListOf( ConclusionPredicate.class ) )
                .toInstance( disjunctionPredicatesList );
            binder.bind( AbstractPhrasesAnalyzerModule.OR_KEY ).to( InjectableAnyOffConclusionPredicate.class );
          }
        } );
  }

  /**
   * @param conditionName
   * @param value0
   * @param selector0
   * @param predicateClass0
   * @param <E>
   */
  private <U, E extends ConclusionPredicate<T>> void bindSinglePredicateRequest( final String conditionName,
      final T value0, final Selector<U, T> selector0, final Class<E> predicateClass0 )
  {
    bind( ElementInjectionRequest.class ).annotatedWith( named( conditionName ) ).toInstance(
        new SinglePredicateInjectionRequest()
        {
          private Binder binder;
          private final T value = value0;
          private final Class<E> predicateClass = predicateClass0;
          private final Selector<U, T> selector = selector0;
          private final TypeLiteral<T> literal = (TypeLiteral<T>) TypeLiteral.get( value0.getClass() );

          @Override
          public void setBinder( Binder binder )
          {
            this.binder = binder;
          }

          @Override
          public TypeLiteral<?> getLiteral()
          {
            return literal;
          }

          @Override
          public void run()
          {
            binder.bind( literal ).toInstance( value );
            binder.bind( newGenericType( ConclusionPredicate.class, literal ) ).to(
                newEnclosedGenericType( predicateClass, literal ) );
            // may be use bindInterceptor(Matchers.any(),
            // Matchers.annotatedWith(Cached.class), cacheInterceptor);
            binder.bindListener( matcher(), new GuicefyDefaultAtomicPredicateTypeListener<U, T>( selector ) );
          }

          @Override
          public Matcher<Object> matcher()
          {
            return Matchers.only( newEnclosedGenericType( predicateClass, literal ) );
          }

          @Override
          public String description()
          {
            return conditionName;
          }
        } );
  }

  private static abstract class AbstractGuicefyAtomicPredicateTypeListener implements TypeListener
  {
    private static final String TO_STRING_METHOD = "toString";

    private static final String INTERCEPTOR_METHOD = "apply";

    protected <T> Method findToStringMethod( Class<T> klass )
    {
      for ( Method method : klass.getDeclaredMethods() )
      {
        Matcher<Method> predicate = Matchers.returns( Matchers.only( String.class ) );
        if ( predicate.matches( method ) && method.getName().equals( TO_STRING_METHOD ) )
        {
          return method;
        }
      }
      return null;
    }

    protected boolean isApplyMethod( Method method )
    {
      return method.getName().equals( INTERCEPTOR_METHOD ) && (method.getParameterTypes()[0] == Object.class);
    }
  }

  private static final class GuicefyDefaultAtomicPredicateTypeListener<U, T> extends
      AbstractGuicefyAtomicPredicateTypeListener
  {

    private final Selector<U, T> selector;

    private GuicefyDefaultAtomicPredicateTypeListener( Selector<U, T> selector )
    {
      this.selector = selector;
    }

    @Override
    public <U> void hear( TypeLiteral<U> literal, TypeEncounter<U> encounter )
    {
      final Class<? super U> klass = literal.getRawType();
      Method toStringMethod = findToStringMethod( klass );
      Preconditions.checkNotNull( toStringMethod, "Can't find toString method in " + klass.getSimpleName() );

      for ( Method method : klass.getDeclaredMethods() )
      {
        if ( isApplyMethod( method ) )
        {
          PredicateApplyMethodInterceptor<U, T> interceptor = new PredicateApplyMethodInterceptor<U, T>(
              toStringMethod, (Selector<U, T>) selector );
          encounter.bindInterceptor( Matchers.only( method ), interceptor );
          return;
        }
      }
    }
  }

  private static final class PredicateApplyMethodInterceptor<U, T> implements MethodInterceptor
  {
    private final Method toStringMethod;

    private final Selector<U, T> selector;

    private static final Logger logger = Logger.getLogger( PredicateApplyMethodInterceptor.class );

    private PredicateApplyMethodInterceptor( Method toStringMethod, Selector<U, T> selector )
    {
      this.toStringMethod = toStringMethod;
      this.selector = selector;
    }

    /**
     * intercept {@code GuicefyConclusionPredicates} nested static classes
     * apply(Object value) method
     */
    @Override
    public Object invoke( MethodInvocation invocation ) throws Throwable
    {
      U argument = (U) invocation.getArguments()[0];
      Comparable<U> value = (Comparable<U>) selector.select( argument );
      logger.debug( String.format( "%s %s", toStringMethod.invoke( invocation.getThis(), new Object[]
      {} ), value ) );
      invocation.getArguments()[0] = value;
      return invocation.proceed();
    }
  }
}
