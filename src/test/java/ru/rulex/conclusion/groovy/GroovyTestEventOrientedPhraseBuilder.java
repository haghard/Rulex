package ru.rulex.conclusion.groovy;

import org.junit.Test;
import ru.rulex.conclusion.Model;
import ru.rulex.conclusion.PhraseBuildersFacade;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class GroovyTestEventOrientedPhraseBuilder
{
  @Test
  public void testGroovyPhraseBuilderWithScript()
  {
    final String scriptBody = "import ru.rulex.conclusion.Model\n" +
            "import static ru.rulex.conclusion.delegate.ProxyUtils.callOn\n" +
            "\n" +
            "rule = {\n" +
            "    withEvent event\n" +
            "    from callOn(Model.class).getInteger() more 8\n" +
            "    from callOn(Model.class).getFloat() less 81.7f\n" +
            "    eval()\n" +
            "}";
    try {
      assertThat( new PhraseBuildersFacade.GroovyEventOrientedPhrasesBuilder<Model>() {
        @Override
        protected void build()
        {
          configure( "" )
                  .withScript( scriptBody );
        }
      }.sync( Model.from( 6, 90.1f ) ) ).isTrue();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      fail("testGroovyPhraseBuilderWithScript error !!!");
    }
  }

  @Test
  public void testGroovyPhraseBuilderWithFile()
  {
    try
    {
      assertThat( new PhraseBuildersFacade.GroovyEventOrientedPhrasesBuilder<Model>()
      {
        @Override
        protected void build()
        {
          configure( "simple rule 1" )
                  .withFile( new File( "./groovy-script/ru/GroovyDslRule.groovy" ) );
        }
      }.sync( Model.from( 7, 87.2f ) ) ).isTrue();

      assertThat( new PhraseBuildersFacade.GroovyEventOrientedPhrasesBuilder<Model>()
      {
        @Override
        protected void build()
        {
          configure( "simple rule 2" )
                  .withFile( new File( "./groovy-script/ru/GroovyDslRule.groovy" ) );
        }
      }.sync( Model.from( 9, 87.2f ) ) ).isFalse();

    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      fail("testGroovyPhraseBuilderWithFile error !!!");
    }
  }
}
