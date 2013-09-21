package ru.rulex.conclusion.dagger;

import javax.inject.Named;

import com.google.common.base.CharMatcher;
import org.junit.Test;

import ru.rulex.conclusion.Model;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerImmutableEventPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerMutableEventPhraseBuilder;

import java.math.BigDecimal;

import static dagger.ObjectGraph.create;
import static junit.framework.TestCase.assertTrue;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static ru.rulex.conclusion.PhraseBuildersFacade.environment;
import static ru.rulex.conclusion.PhraseBuildersFacade.var;
import static ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.DaggerImmutablePhraseModule.$less;
import static ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.DaggerImmutablePhraseModule.$more;
import static ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.DaggerImmutablePhraseModule.immutablePhrase;
import static ru.rulex.conclusion.dagger.ObjectGraphBuilders.val;
import static ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.DaggerMutablePhraseModule.$less;
import static ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.DaggerMutablePhraseModule.mutablePhrase;
import static ru.rulex.conclusion.dagger.ObjectGraphBuilders.varInt;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class DaggerEventOrientedPhraseBuilderTest
{

  @Test
  public void testImmutableDeprecatedApiForDaggerBuilder()
  {
    final DaggerImmutableEventPhrasesBuilder builder = create(
      immutablePhrase(
        $less( 19, callOn( Model.class ).getInteger() ),
        $less( 19, callOn( Model.class ).getOtherInteger() ),
        $more( 56.78f, callOn( Model.class ).getFloat() ) ) )
      .get( DaggerImmutableEventPhrasesBuilder.class );

    assertThat( builder.sync( Model.from( 20, 78 ) ) ).isTrue();
  }

  @Test
  public void testMutableDeprecatedDaggerBuilder()
  {
    final String val1 = "x1";
    final String val2 = "x2";

    final DaggerMutableEventPhraseBuilder mutableBuilder = create(
      mutablePhrase(
        $less( 12, val1 ),
        $less( 13, val2 )
      )
    ).get( DaggerMutableEventPhraseBuilder.class );

    final boolean result = mutableBuilder.populateFrom(
      environment(
        var( val1, callOn( Model.class ).getInteger() ),
        var( val2, callOn( Model.class ).getInteger() )
      )
    ).sync( Model.from( 20, 78 ) );

    assertTrue( result );
  }

  @Test
  public void testMutableDaggerBuilderWithMissedVar()
  {
    final DaggerMutableEventPhraseBuilder mutableBuilder = create(
      mutablePhrase(
        $less( 64, "a" ),
        $less( 678, "b" ),
        $less( 6755656, "c" )
      )
    ).get( DaggerMutableEventPhraseBuilder.class );

    try
    {
      mutableBuilder.populateFrom(
        environment(
          var( "a", callOn( Model.class ).getInteger() )
        )
      ).sync( Model.from( 20, 78 ) );
    }
    catch ( IllegalStateException ex )
    {
      assertEquals( ex.getMessage(), "Undefined variables was found: b,c" );
    }
    catch ( Exception e )
    {
      fail();
    }
  }

  @Test
  public void testImmutableSimpleDaggerBuilder()
  {
    final DaggerImmutableEventPhrasesBuilder immutableBuilder = create(
    immutablePhrase(
      val( 3 ).less( callOn( Model.class ).getInteger() ),
      val( 82.89f ).more( callOn( Model.class ).getFloat() ),
      val("milk", "bread", "link").containsAnyOff( callOn( Model.class ).getString() ),
      val( "bread" ).eq( callOn( Model.class ).getString() )
    ) ).get( DaggerImmutableEventPhrasesBuilder.class );

    assertThat( immutableBuilder.sync( Model.from( 20, 78.1f, "bread" ) ) ).isTrue();
  }

  @Test
  public void testMutableDaggerArgumentBaseApi()
  {
    final DaggerMutableEventPhraseBuilder mutableBuilder0 = create(
      mutablePhrase(
        varInt( "a" ).less( 19 )
      )
    ).get( DaggerMutableEventPhraseBuilder.class );

    final boolean result = mutableBuilder0.populateFrom(
      environment(
        var( "a", callOn( Model.class ).getInteger() )
      )
    ).sync( Model.from( 20, 78 ) );

    assertTrue( result );
  }

  @Test
  public void testCharMatchers()
  {
    System.out.println(
            CharMatcher.anyOf( "m12" ).matchesAnyOf( "vvvvm 1vvvvv 2bbbbb" ) );


    //Predicates.contains( Pattern.compile("") ).apply(  )
    /*
    System.out.println(
            CharMatcher.anyOf( "milk" ).matchesAnyOf( "some milk long" ));

    System.out.println(
      CharMatcher.forPredicate(Predicates.equalTo( 'm' ))
              .matchesAnyOf( "2346234623472347" ));
     */
  }
   /*
  //TODO: implement like this API , with AnnotationProcessor
  static class Model1
  {

    Object[] v = { callOn( Model.class ).getFloat(), callOn( Model.class ).getFloat() };

    public Integer intOperation() { return callOn( ru.rulex.conclusion.Model.class ).getInteger(); }

    public String strOperation() { return callOn( ru.rulex.conclusion.Model.class ).getString(); }

    @Query(
      value = {
              @Operation( value = intOperation() ),
              @Operation( value = intOperation() ) } )
    public void method()
    {

    }
  }
  */

  @interface QueryLine
  {
    String value();
  }

  @interface Operation
  {
    int value();
  }

  @interface Query
  {
    public Operation[] value() default { };
  }

  interface HaghardExpression
  {
    //May be just add groovy script
    @QueryLine( "(Model.getInt() > {x}) and (Model.getFloat >= {y})" )
    <T, E> boolean eval( @Named( "x" ) E value0, @Named( "y" ) T value1 );
  }

  interface HaghardExpression1
  {
    Object[] arguments = {
            callOn( Model.class ).getFloat(),
            callOn( Model.class ).getFloat() };

    @QueryLine( "(Model.getInt() > x) and (Model.getFloat >= y)" )
    <T, E> boolean eval( @Named( "x" ) E value0, @Named( "y" ) T value1 );
  }

  interface IQuery<T, E>
  {
  }

  static class Query1 implements IQuery<Integer, Float>
  {
    Integer a = callOn( Model.class ).getInteger();
    Float b = callOn( Model.class ).getFloat();
  }
}
