package ru.rulex.conclusion;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import static ru.rulex.matchers.RulexObjectMatcher.selector;

import org.hamcrest.Matcher;
import org.junit.Test;

import ru.rulex.matchers.AssertionAwareListener;
import ru.rulex.matchers.RulexObjectMatcher;
import ru.rulex.matchers.RulexAnalyzer;
import ru.rulex.matchers.RulexMatcher;
import ru.rulex.matchers.RulexMatchersBuilder;

public class TestRulexApi
{

  @Test
  public void testRulexMatcherChainCalls()
  {
    Model model = Model.values( 4, 56.7f );
    RulexMatcher<Model> matcher = selector( Model.class, callOn( Model.class ).getInteger() )
        .lessThan( 3 ).and(
            selector( Model.class, callOn( Model.class ).getFloat() ).lessThan( 56.6f ) );

    assertThat( matcher.matches( model ) ).isTrue();
  }

  @Test
  public void shouldBeEqualsOnSameFields()
  {
    RulexMatcher<Model> matcher = selector( Model.class, callOn( Model.class ).getInteger() )
        .isEquals( selector( Model.class, callOn( Model.class ).getInteger() ) );

    Model model = Model.values( 4, 5 );
    assertThat( matcher.matches( model ) ).isTrue();
  }

  @Test
  public void shouldBeLessIntegerComparison()
  {
    RulexMatcher<Model> matcher = modelSelector( callOn( Model.class ).getInteger() ).lessThan(
        modelSelector( callOn( Model.class ).getOtherInteger() ) );

    Model model = Model.values( 1, 2 );

    // separate configuration from processing
    RulexAnalyzer analyzed = RulexObjectMatcher.projection( Model.class ).assertThat( matcher ).on( model );
    analyzed.analyze( new AssertionAwareListener<Model>()
    {
      @Override
      public void passed( Model analysedObject, Matcher<?> matcher )
      {
        assertThat( true ).isTrue();
      }

      @Override
      public void failed( Model analysedObject, Matcher<?> matcher )
      {
        System.out.println( "failed" );
      }

      @Override
      public void filtered( Model analysedObject, Matcher<?> matcher )
      {
        System.out.println( "filtered" );
      }

      @Override
      public void unexpected( Model analysedObject, Exception exception )
      {
        System.out.println( "unexpected" );
      }

      @Override
      public void done()
      {
        System.out.println( "done" );
      }
    } );
  }

  /**
   * Method for creation custom builder with specific type parameter
   * 
   * @param arg
   * @return
   */
  public static <E extends Comparable<? super E>> RulexMatchersBuilder<Model> modelSelector(
      final E arg )
  {
    return selector( Model.class, arg );
  }
}
