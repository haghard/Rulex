package ru.rulex.conclusion;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static ru.rulex.conclusion.FluentConclusionPredicate.number;
import static ru.rulex.conclusion.FluentConclusionPredicate.typeSafeQuery;
import static ru.rulex.conclusion.RulexMatchersDsl.eq;

import java.util.concurrent.TimeUnit;

import com.google.inject.*;
import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.rulex.conclusion.PhraseBuildersFacade.EventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder;

import com.google.common.base.Joiner;

@RunWith(OnamiRunner.class)
public class TestSingleEventRules
{

  final static String NAME1 = "testEventOrientedPhrasesBuilderWithTypeSafeSelector";

  @Inject
  AbstractEventOrientedPhraseBuilder<Model> builder1;

  @GuiceProvidedModules
  public static Module createEventOrientedPhrasesBuilderWithProxy()
  {
    return new AbstractModule()
    {
      @Override
      protected void configure()
      {
        bind( Key.get( new TypeLiteral<AbstractEventOrientedPhraseBuilder<Model>>(){} ) ).toInstance( new EventOrientedPhrasesBuilder<Model>()
        {
          @Override
          protected void build()
          {
            configure( Model.class, "fact: [getInteger() == 11]" ).shouldMatch(
                typeSafeQuery( number( Model.class, Integer.class, Model.INT_ACCESSOR ), eq( 11 ) ) );
          }
        } );
      }
    };

  }

  @Test
  public void testEventOrientedPhrasesBuilderWithTypeSafeSelector()
  {
    final String errorMessage = Joiner.on( "" ).join( NAME1, " error !!!" );
    try
    {
      assertThat( builder1.async( Model.from( 11 ) ).checkedGet( 1, TimeUnit.SECONDS ) ).isTrue().as(
          errorMessage );

      assertThat( builder1.async( Model.from( 12 ) ).checkedGet( 1, TimeUnit.SECONDS ) ).isFalse().as(
          errorMessage );

      assertThat( builder1.async( Model.from( 11 ) ).checkedGet( 1, TimeUnit.SECONDS ) ).isTrue().as(
          errorMessage );
    }
    catch (Exception ex)
    {
      fail( errorMessage );
    }
  }

}
