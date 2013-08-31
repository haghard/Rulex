package ru.rulex.conclusion.dagger;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import dagger.Module;
import dagger.Provides;
import org.junit.Test;
import ru.rulex.conclusion.ConclusionPredicate;
import ru.rulex.conclusion.Model;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.ValEnvironment;
import ru.rulex.conclusion.guice.InjectableConclusionPredicates;

import javax.inject.Inject;
import javax.inject.Named;

import static dagger.ObjectGraph.create;
import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.PhraseBuildersFacade.environment;
import static ru.rulex.conclusion.PhraseBuildersFacade.var;
import static ru.rulex.conclusion.dagger.DaggerDependencyAnalyzerModule.*;
import static ru.rulex.conclusion.dagger.DaggerEventOrientedEngineTest.PredicateModule.predicate;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class DaggerEventOrientedEngineTest
{
  @Test
  public void testDaggerBuilderWithDifferentTypes()
  {
    final AbstractEventOrientedPhrasesBuilder builder = create(
      $expression(
        $less( 19, callOn( Model.class ).getInteger() ),
        $less( 19, callOn( Model.class ).getOtherInteger() ),
        $more( 56.78f, callOn( Model.class ).getFloat() ) ) )
      .get( AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();
  }

  @Test
  public void testDaggerBuilderWithSameType()
  {
    final AbstractEventOrientedPhrasesBuilder builder = create(
      $expression(
        $less( 19, callOn( Model.class ).getInteger() ),
        $more( 79, callOn( Model.class ).getOtherInteger() ),
        $moreOrEquals( 56.78f, callOn( Model.class ).getFloat() ) ) )
      .get( AbstractEventOrientedPhrasesBuilder.class );

    assertThat( builder.sync( Model.values( 20, 78 ) ) ).isTrue();

    final AbstractEventOrientedPhrasesBuilder lazyBuilder = create(
      $lazyExpression(
        $less( 12, "someNumber" ),
        $more( 56.78, "someFNumber" )
      )
    ).get( AbstractEventOrientedPhrasesBuilder.class );

    var( "someNumber", callOn( Model.class ).getInteger() );

    lazyBuilder.eval(
      environment(
        var( "someNumber", callOn( Model.class ).getInteger() ),
        var( "someFNumber", callOn( Model.class ).getFloat() ))
    ).async( Model.values( 20, 78 ) );

  }

  interface Heater
  {}

  static class HeaterImpl implements Heater
  {}

  static class HeaterImpl2 implements Heater
  {}

  static class Application {
    final Heater heater;

    @Inject
    Application(Heater heater)
    {
      this.heater = heater;
    }

    Heater getHeater()
    {
      return heater;
    }
  }

  @dagger.Module(
    injects = Application.class
  )
  static class ApplicationEntryPoint
  {
    @Provides
    Heater provideHeater() { return new HeaterImpl(); }
  }

  @dagger.Module(
    injects = Application.class
  )
  static class ApplicationEntryPointWithConstructor
  {
    final Heater heater;
    ApplicationEntryPointWithConstructor(Heater heater) {
      this.heater = heater;
    }

    @Provides
    Heater provideHeater() { return heater; }
  }

  @dagger.Module( library = true,
          injects = ConclusionPredicate.class)
  static class PredicateModule
  {
    final Optional<?> value;
    final LogicOperation op;

    PredicateModule(Optional<?> value, LogicOperation op) {
      this.value = value;
      this.op = op;
    }

    static <T extends Comparable<? super T>> PredicateModule predicate(T value, LogicOperation op){
      return new PredicateModule( Optional.of( value ), op);
    }

    @dagger.Provides
    ConclusionPredicate providePredicate() {
      final Comparable<?> v = ( Comparable<?> ) value.get();
      switch ( op ) {
        case lessThan:
          return new InjectableConclusionPredicates.InjectableLessConclusionPredicate( v );
        case moreThan:
          return new InjectableConclusionPredicates.InjectableMoreConclusionPredicate( v );

        default: return null;
      }
    }
  }

  static class NumVal<T> {
    final T value;
    private NumVal(T value) {
      this.value = value;
    }

    static <T> NumVal<T> num( T value ) { return new NumVal<T>( value ); }
  }

  static class ValExp {
    final NumVal val;
    @Inject ValExp( NumVal val ) {
      this.val = val;
    }
  }

  /*
  @Module(
    injects = NumVal.class, complete = false , library = true
  )
  static class ValModule
  {
    private final NumVal val;
    ValModule( NumVal val ) {
      this.val = val;
    }

    @Provides NumVal provideVal() { return val; }
  }

  @Module(addsTo = ValModule.class, injects = { ValExp.class })
  static class ExtensionModule { }
  */

  @Module( injects = { ValExp.class }, complete = false , library = true)
  static class ExpressionModule {
    private final NumVal val;
    ExpressionModule( NumVal val ) {
      this.val = val;
    }

    @Provides ValExp provide() { return new ValExp( val ); }
  }

  @Test
  public void test() {
    //NumVal val = create( new ValModule( NumVal.num( 5 ) ) ).plus( new ExtensionModule() ).get( ValExp.class ).val;

    NumVal val1 = create ( new ExpressionModule( NumVal.num( 6 ) )).get( ValExp.class ).val;

    ConclusionPredicate<Integer> p =
            create( predicate( 5, LogicOperation.lessThan ) )
              .get( ConclusionPredicate.class );

    assertThat( p.apply( 6 ) ).isTrue();
    assertThat( p.apply( 4 ) ).isFalse();


    Application app = create( new ApplicationEntryPoint() ).get( Application.class );
    assertThat( app.getHeater() ).isExactlyInstanceOf( HeaterImpl.class );

    Application app2 = create( new ApplicationEntryPointWithConstructor(new HeaterImpl()) ).get( Application.class );
    assertThat( app2.getHeater() ).isExactlyInstanceOf( HeaterImpl.class );
  }

  /*
  TODO: implement like this API , with AnnotationProcessor
  public void testAnnotationApi() {
    //@Module(addsTo = RootModule.class, injects = { C.class, D.class })
    @Expression( onClass = Model.class,
        conditions = { callOn( Model.class ).getFloat(), callOn( Model.class ).getFloat() } )

  }
  */

  interface Expression
  {
    @Less( "{value}" )
    void someValue1( @Named( "value" ) Integer value );

    @More( "{value}" )
    void someValue2( @Named( "value" ) Integer value0 );
  }

  interface AssertionGramExpr<T> {
    T eval(Function<String, T> func);
  }

  static class NumExpr<T> implements AssertionGramExpr<T> {
    private final T value;
    NumExpr( T value ) { this.value = value; }
    @Override public T eval( Function<String, T> func ) { return value; }
  }

  static class ValExpr<T> implements AssertionGramExpr<T> {
    private final String valName;
    ValExpr(String valName) { this.valName = valName; }
    @Override public T eval( Function<String, T> func ) { return func.apply( valName ); }
  }

  static class Assertion<T> implements AssertionGramExpr<T> {
    final AssertionGramExpr<T> left;
    final AssertionGramExpr<T> right;
    final LogicOperation op;
    Assertion(AssertionGramExpr<T> left, AssertionGramExpr<T> right, LogicOperation op) {
      this.left = left;
      this.right = right;
      this.op = op;
    }

    @Override
    public T eval( Function<String, T> func ) {
      return op.eval(left.eval( func ) - right.eval( func ));
    }
  }


}
