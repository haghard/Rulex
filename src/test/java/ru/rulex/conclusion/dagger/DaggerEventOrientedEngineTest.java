package ru.rulex.conclusion.dagger;

import javax.inject.Named;

import org.junit.Test;

import ru.rulex.conclusion.Model;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerImmutableEventPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.DaggerMutableEventPhraseBuilder;

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
import static ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.val;
import static ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.DaggerMutablePhraseModule.$less;
import static ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.DaggerMutablePhraseModule.mutablePhrase;
import static ru.rulex.conclusion.dagger.DaggerObjectGraphBuilders.varInt;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class DaggerEventOrientedEngineTest
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
      val( 19 ).less( callOn( Model.class ).getOtherInteger() ),
      val( 82.89f ).more( callOn( Model.class ).getFloat() )
    ) ).get( DaggerImmutableEventPhrasesBuilder.class );

    assertThat( immutableBuilder.sync( Model.from( 20, 20, 78.1f ) ) ).isTrue();
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
}
