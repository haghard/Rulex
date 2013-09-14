package ru.rulex.conclusion;

import org.junit.Test;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.SimpleEventOrientedPhrasesBuilder;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.RulexMatchersDsl.*;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class TestSimpleEventOrientedPhrasesBuilder
{

  @Test
  public void testSimpleEventOrientedPhrasesBuilder() throws PhraseExecutionException
  {
    final AbstractEventOrientedPhraseBuilder<Model> builder = new SimpleEventOrientedPhrasesBuilder<Model>()
    {
      protected void build()
      {
        as( "fact: [getInteger() == 999]" ).shouldMatch( callOn( Model.class ).getInteger(),
            eq( 999 ) );
      }
    };

    assertThat( builder.async( Model.from( 999 ) ).checkedGet() ).isTrue();
    assertThat( builder.async( Model.from( 9991 ) ).checkedGet() ).isFalse();
    assertThat( builder.sync( Model.from( 999 ) ) ).isTrue();
  }
}