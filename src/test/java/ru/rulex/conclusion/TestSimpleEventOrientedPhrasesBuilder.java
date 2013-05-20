package ru.rulex.conclusion;

import org.junit.Test;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.SimpleEventOrientedPhrasesBuilder;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.RulexMatchersDsl.*;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

public class TestSimpleEventOrientedPhrasesBuilder
{

  @Test
  public void testSimpleEventOrientedPhrasesBuilder() throws PhraseExecutionException
  {
    final AbstractEventOrientedPhrasesBuilder builder = new SimpleEventOrientedPhrasesBuilder()
    {
      protected void build()
      {
        as( "fact: [getInteger() == 999]" ).shouldMatch( callOn( Model.class ).getInteger(),
            eq( 999 ) );
      }
    };

    assertThat( builder.async( Model.values( 999 ) ).checkedGet() ).isTrue();
    assertThat( builder.async( Model.values( 9991 ) ).checkedGet() ).isFalse();
    assertThat( builder.sync( Model.values( 999 ) ) ).isTrue();
  }
}
