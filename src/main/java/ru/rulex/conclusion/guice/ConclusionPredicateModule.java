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

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.*;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Named;
import com.google.inject.spi.*;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.Selector;

import java.lang.reflect.Method;
import java.util.List;

import static com.google.inject.name.Names.named;
import static ru.rulex.conclusion.guice.GuiceGenericTypes.*;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableAnyOffConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableAtMostConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableEqualsConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableLessConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMoreConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableAtLeastConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMatchAnyOffPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableRegexpPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableMultiRegexpPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableAlwaysFalsePredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableAlwaysTruePredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableNotNullConclusionPredicate;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates.InjectableIsNullConclusionPredicate;

@SuppressWarnings("unchecked")
abstract class ConclusionPredicateModule<T extends Comparable<? super T>> extends AbstractModule
{
  /**
   * 
   */
  protected abstract void bindPredicate();

  protected void configure()
  {
    bindPredicate();
  }

  protected void bindAlwaysFalse()
  {
    bindAlwaysRequest( "alwaysFalse", false , InjectableAlwaysFalsePredicate.class );
  }

  protected void bindAlwaysTrue()
  {
    bindAlwaysRequest( "alwaysTrue", true , InjectableAlwaysTruePredicate.class );
  }

  /**
   *
   * @param conditionName
   * @param selector
   */
  protected void bindIsNotNull( final String conditionName, final Class<T> clazz, final Selector<?, T> selector )
  {
    bindNullableRequest( conditionName, clazz, selector, InjectableNotNullConclusionPredicate.class );
  }

  protected void bindIsNull( final String conditionName, final Class<T> clazz, final Selector<?, T> selector )
  {
    bindNullableRequest( conditionName, clazz, selector, InjectableIsNullConclusionPredicate.class );
  }

  /**
   * @param conditionName
   * @param value
   * @param selector
   * @param <E>
   */
  protected void bindEquality( final String conditionName, final T value, final Selector<?, T> selector )
  {
    bindPredicateRequest( conditionName, value, selector, InjectableEqualsConclusionPredicate.class );
  }

  /**
   * 
   * @param conditionName
   * @param values
   * @param selector
   */
  protected void bindEquality( final String conditionName, final ImmutableSet<String> values, final Selector<?, T> selector )
  {
    bindPredicateRequest( conditionName, values, selector, InjectableEqualsConclusionPredicate.class );
  }

  /**
   *
   * @param conditionName
   * @param values
   * @param selector
   */
  protected void bindEqualsAnyOff( final String conditionName, final ImmutableSet<String> values, final Selector<?, T> selector )
  {
    bindPredicateRequest(  conditionName, values, selector, InjectableMatchAnyOffPredicate.class );
  }

  /**
   *
   * @param conditionName
   * @param values
   * @param selector
   */
  protected void bindMultiRegExp( final String conditionName, final ImmutableSet<String> values, final Selector<?, T> selector )
  {
    bindPredicateRequest(  conditionName, values, selector, InjectableMultiRegexpPredicate.class );
  }

  protected void bindRegExp( final String conditionName, final T value, final Selector<?, T> selector )
  {
    bindPredicateRequest(  conditionName, value, selector, InjectableRegexpPredicate.class );
  }

  /**
   * @param conditionName
   * @param value
   * @param selector
   * @param <E>
   */
  protected void bindMajority( final String conditionName, final T value, final Selector<?, T> selector )
  {
    bindPredicateRequest( conditionName, value, selector, InjectableMoreConclusionPredicate.class );
  }

  /**
   * MoreOrEquals
   * @param conditionName
   * @param value
   * @param selector
   */
  protected void bindAtLeast( final String conditionName, final T value, final Selector<?, T> selector )
  {
    bindPredicateRequest( conditionName, value, selector, InjectableAtLeastConclusionPredicate.class );
  }

  /**
   * LessOrEquals
   * @param conditionName
   * @param value
   * @param selector
   */
  protected void bindAtMost( final String conditionName, final T value, final Selector<?, T> selector )
  {
    bindPredicateRequest( conditionName, value, selector, InjectableAtMostConclusionPredicate.class );
  }
  /**
   * @param conditionName
   * @param value
   * @param selector
   * @param <E>
   */
  protected void bindMinority( final String conditionName, final T value, final Selector<?, T> selector )
  {
    bindPredicateRequest( conditionName, value, selector, InjectableLessConclusionPredicate.class );
  }

  /**
   * @param conditionName
   * @param modules
   */
  protected void bindDisjunction( String conditionName, Module... modules )
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

    final ImmutableList<ConclusionPredicate> disjunctionPredicatesList = disjunctionPredicates.build();
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
            binder.bind( immutableListOf( ConclusionPredicate.class ) ).toInstance( disjunctionPredicatesList );
            binder.bind( AbstractGuiceImmutablePhraseModule.OR_KEY ).to( InjectableAnyOffConclusionPredicate.class );
          }
        } );
  }

  private <U, E extends ConclusionPredicate<T>> void bindNullableRequest( final String conditionName,
                                                                          final Class<T> clazz,
                                                                          final Selector<U, T> selector0,
                                                                          final Class<E> predicateClass0 )
  {
    bind( ElementInjectionRequest.class ).annotatedWith( named( conditionName ) ).toInstance(
      new SinglePredicateInjectionRequest()
      {
        private Binder binder;
        private final Class<E> predicateClass = predicateClass0;
        private TypeLiteral<T> literal = TypeLiteral.get( clazz );

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
        public Matcher<Object> matcher()
        {
          return Matchers.only( newEnclosedGenericType( predicateClass0, literal ) );
        }

        @Override
        public String description()
        {
          return conditionName;
        }

        @Override
        public void run()
        {
          binder.bind( newGenericType( ConclusionPredicate.class, literal ) )
                  .to( newEnclosedGenericType( predicateClass, literal ) );
          binder.bindListener( matcher(), new GuiceSelectorBasedPredicateTypeListener<U, T>( selector0 ) );
        }
      });
  }

  private <U, E extends ConclusionPredicate<T>> void bindPredicateRequest( final String conditionName,
      final ImmutableSet<String> values0, final Selector<U, T> selector0, final Class<E> predicateClass0 )
  {
    bind( ElementInjectionRequest.class ).annotatedWith( named( conditionName ) ).toInstance(
        new SinglePredicateInjectionRequest()
        {
          private Binder binder;
          private final Selector<U, T> selector = selector0;
          private final Class<E> predicateClazz = predicateClass0;
          private final ImmutableSet<String> values = values0;
          private TypeLiteral<String> literal = TypeLiteral.get( String.class );

          @Override
          public void setBinder( Binder binder )
          {
            this.binder = binder;
          }

          @Override
          public TypeLiteral<?> getLiteral()
          {
            return TypeLiteral.get( String.class );
          }

          @Override
          public Matcher<Object> matcher()
          {
            return Matchers.only( newEnclosedGenericType( predicateClazz, literal ) );
          }

          @Override
          public String description()
          {
            return conditionName;
          }

          @Override
          public void run()
          {
            binder.bind( new TypeLiteral<ImmutableSet<String>>() {} ).toInstance( values );
            binder.bind( newGenericType( ConclusionPredicate.class, literal ) ).to(
                    newEnclosedGenericType( predicateClazz, literal ) );
            binder.bindListener( matcher(), new GuiceSelectorBasedPredicateTypeListener<U, T>( selector ) );
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
  private <U, E extends ConclusionPredicate<T>> void bindPredicateRequest( final String conditionName,
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
            binder.bindListener( matcher(), new GuiceSelectorBasedPredicateTypeListener<U, T>( selector ) );
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

  private <U, E extends ConclusionPredicate<T>> void bindAlwaysRequest(final String conditionName, final boolean value0, final Class<E> predicateClass0 )
  {
    bind( ElementInjectionRequest.class ).annotatedWith( named( conditionName ) ).toInstance(
      new SinglePredicateInjectionRequest()
      {
        private Binder binder;
        private final boolean value = value0;
        private final TypeLiteral<Boolean> literal = new TypeLiteral<Boolean>() {};

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
        public Matcher<Object> matcher()
        {
          return Matchers.only( newEnclosedGenericType( predicateClass0, literal ) );
        }

        @Override
        public String description()
        {
          return conditionName;
        }

        @Override
        public void run()
        {
          binder.bind(literal).toInstance(value);
          binder.bindListener(matcher(), new GuiceBasedPredicateTypeListener());
          binder.bind( newGenericType( ConclusionPredicate.class, literal ) )
                  .to( newEnclosedGenericType( predicateClass0, literal ) );
        }
      });
  }

  private static abstract class AbstractGuicePredicateTypeListener implements TypeListener
  {
    private static final String INTERCEPTOR_METHOD = "apply";
    private static final String TO_STRING_METHOD = "toString";

    protected <T> Optional<Method> findToStringMethod( Class<T> clazz )
    {
      for ( Method method : clazz.getDeclaredMethods() )
      {
        Matcher<Method> predicate = Matchers.returns( Matchers.only( String.class ) );
        if ( predicate.matches( method ) && method.getName().equals( TO_STRING_METHOD ) )
        {
          return Optional.fromNullable( method );
        }
      }
      return Optional.absent();
    }

    protected boolean isApplyMethod( Method method )
    {
      return method.getName().equals( INTERCEPTOR_METHOD ) && (method.getParameterTypes()[0] == Object.class);
    }
  }

  private static final class GuiceBasedPredicateTypeListener extends AbstractGuicePredicateTypeListener
  {
    @Override public <I> void hear( TypeLiteral<I> literal, TypeEncounter<I> encounter )
    {
      Class<? super I> clazz = literal.getRawType();
      Optional<Method> toStringMethod = findToStringMethod(clazz);
      Preconditions.checkNotNull( toStringMethod.get(),
              "Can't find toString method in " + clazz.getSimpleName() );
      for (Method method : clazz.getMethods())
      {
        if ( isApplyMethod( method ) )
        {
          encounter.bindInterceptor(Matchers.only(method),
                  new PredicateBooleanApplyMethodInterceptor( toStringMethod.get() ));
        }
      }
    }
  }

  private static final class GuiceSelectorBasedPredicateTypeListener<U, T> extends
          AbstractGuicePredicateTypeListener
  {
    private final Selector<U, T> selector;

    private GuiceSelectorBasedPredicateTypeListener( Selector<U, T> selector )
    {
      this.selector = selector;
    }

    @Override
    public <U> void hear( TypeLiteral<U> literal, TypeEncounter<U> encounter )
    {
      final Class<? super U> clazz = literal.getRawType();
      Optional<Method> toStringMethod = findToStringMethod( clazz );
      Preconditions.checkNotNull( toStringMethod.get(), "Can't find toString method in " + clazz.getSimpleName() );

      for ( Method method : clazz.getDeclaredMethods() )
      {
        if ( isApplyMethod( method ) )
        {
          PredicateApplyMethodInterceptor<U, T> interceptor = new PredicateApplyMethodInterceptor<U, T>(
              toStringMethod.get(), (Selector<U, T>) selector );
          encounter.bindInterceptor( Matchers.only( method ), interceptor );
          return;
        }
      }
    }
  }

  private  static final class PredicateBooleanApplyMethodInterceptor implements MethodInterceptor
  {
    private final Method toStringMethod;
    private static final Logger logger = Logger.getLogger( PredicateBooleanApplyMethodInterceptor.class );

    private PredicateBooleanApplyMethodInterceptor(Method toStringMethod)
    {
      this.toStringMethod = toStringMethod;
    }

    @Override public Object invoke(MethodInvocation invocation) throws Throwable
    {
      invocation.getArguments()[0] = Boolean.FALSE;
      logger.debug(String.format("%s", toStringMethod.invoke(invocation.getThis(), new Object[]{})));
      return invocation.proceed();
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
      logger.debug( String.format( "%s %s", toStringMethod.invoke( invocation.getThis(), new Object[] {} ), value ) );
      invocation.getArguments()[0] = value;
      return invocation.proceed();
    }
  }
}
