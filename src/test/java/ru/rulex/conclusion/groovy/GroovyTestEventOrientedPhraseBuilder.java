package ru.rulex.conclusion.groovy;

import org.junit.Test;
import ru.rulex.conclusion.Model;

import java.io.File;
import java.math.BigDecimal;

import ru.rulex.conclusion.PhraseBuildersFacade.GroovyEventOrientedPhrasesBuilder;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class GroovyTestEventOrientedPhraseBuilder
{
  @Test
  public void testGroovyPhraseBuilderWithScript()
  {
    final String scriptBody = "rule2 = {\n" +
            "    onEvent event\n" +
            "    $ objectId more 7\n" +
            "    $ eventType atMost 2\n" +
            "    $ objectPrice atMost 80.99\n" +
            "    $ objectName equalsAnyOff( [\"snickers\", \"mars\", \"picnic\"] )\n" +
            "    eval()\n" +
            "}";
    try
    {
      assertThat( new GroovyEventOrientedPhrasesBuilder<TradeEvent>()
      {
        @Override
        protected void build()
        {
          configure( "script 1" )
                  .withScript( scriptBody );
        }
      }.sync( new TradeEvent( 6, 9, new BigDecimal( 90.1f), "mars") ) ).isTrue();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      fail( "testGroovyPhraseBuilderWithScript error !!!" );
    }
  }

  @Test
  public void testGroovyPhraseBuilderWithFile()
  {
    try
    {
      assertThat( new GroovyEventOrientedPhrasesBuilder<TradeEvent>()
      {
        @Override
        protected void build()
        {
          configure( "simple rule 1" )
                  .withFile( new File( "./groovy-script/ru/GroovyDslImmutableRule.groovy" ) );
        }
      }.sync( new TradeEvent(1, 3, new BigDecimal("89.45"), "snickers" )) ).isTrue();

      assertThat( new GroovyEventOrientedPhrasesBuilder<TradeEvent>()
      {
        @Override
        protected void build()
        {
          configure( "simple rule 2" )
                  .withFile( new File( "./groovy-script/ru/GroovyDslImmutableRule.groovy" ) );
        }
      }.sync( new TradeEvent(11, 13, new BigDecimal("89.45"), "mars" ) ) ).isFalse();

    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      fail( "testGroovyPhraseBuilderWithFile error !!!" );
    }
  }
}
