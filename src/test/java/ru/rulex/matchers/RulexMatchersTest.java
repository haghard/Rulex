package ru.rulex.matchers;

import org.junit.Test;
import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import static ru.rulex.matchers.Rulex.verb;
import org.fest.assertions.api.Assertions;
import org.hamcrest.Matcher;
import com.google.common.collect.ImmutableList;
import ru.rulex.conclusion.Model;
import static ru.rulex.matchers.Rulex.projection;

public class RulexMatchersTest
{
  @Test
  public void testRulexMatcherChainCalls()
  {
    final Model model = Model.values( 4, 56.7f );

    final RulexVerb<Model> verb0 = verb(Model.class, callOn(Model.class).getInteger()).lessThan(5)
            .and(verb(Model.class, callOn(Model.class).getInteger()).lessThan(6));

    assertThat( verb0.matches( model ) ).isTrue();

    final RulexVerb<Model> verb = verb(Model.class, callOn(Model.class).getInteger()).lessThan( 5 )
            .and(verb(Model.class, callOn(Model.class).getFloat()).lessThan( 56.8f ) );

    assertThat( verb.matches( model ) ).isTrue();
  }

  @Test
  public void shouldBeEqualsOnSameFields()
  {
    final RulexVerb<Model> verb = verb(Model.class, callOn(Model.class).getInteger())
        .isEquals( verb(Model.class, callOn(Model.class).getInteger()) );

    final Model model = Model.values( 4, 5 );
    assertThat( verb.matches( model ) ).isTrue();
  }

  @Test
  public void oneIntegerShouldBeLessOtherInteger()
  {
    final Model model = Model.values( 1, 2 );

    //configure specific verb
    final RulexVerb<Model> verb = modelVerb( callOn(Model.class).getInteger()).lessThan(
            modelVerb(callOn(Model.class).getOtherInteger()));

    //processing
    final RulexAnalyzer analyzed = projection( Model.class ).assertThat( verb )
        .on( model );
    analyzed.analyze( new AssertionAwareListener()
    {
      @Override
      public void passed( Object analysedObject, Matcher<?> matcher )
      {
        assertThat( analysedObject ).isSameAs( model );
      }

      @Override
      public void failed( Object analysedObject, Matcher<?> matcher )
      {
        Assertions.fail( "failed" );
      }

      @Override
      public void filtered( Object analysedObject, Matcher<?> matcher )
      {
        Assertions.fail( "failed" );
      }

      @Override
      public void unexpected( Object analysedObject, Exception exception )
      {
        Assertions.fail( "failed" );
      }

      @Override
      public void done()
      {
        assertThat( true ).isTrue();
      }
    } );
  }

  @Test
  public void oneShouldBePassedSecondShouldBeFiltered()
  {
    final Model first = Model.values( 121 );
    final Model second = Model.values( 49 );

    final ImmutableList<Model> list = ImmutableList.of( first, second );

    RulexVerb<Model> filter = modelVerb(callOn(Model.class).getInteger()).lessThan(122);

    RulexVerb<Model> matcher = modelVerb(callOn(Model.class).getInteger()).moreThan(50);

    projection( Model.class ).forEach( filter ).assertThat( matcher ).in( list )
        .analyze( new AssertionAwareListener()
        {
          @Override
          public void passed( Object analysedObject, Matcher<?> matcher )
          {
            assertThat( analysedObject ).isSameAs( first );
          }

          @Override
          public void failed( Object analysedObject, Matcher<?> matcher )
          {
            assertThat( analysedObject ).isSameAs( second );
          }

          @Override
          public void filtered( Object analysedObject, Matcher<?> matcher )
          {
            Assertions.fail( "unexpected" );
          }

          @Override
          public void unexpected( Object analysedObject, Exception exception )
          {
            Assertions.fail( "unexpected" );
          }

          @Override
          public void done()
          {
            assertThat( true ).isTrue();
          }
        } );
  }

  /**
   * Method for creation custom builder with specific type parameter
   * 
   * @param arg
   * @return
   */
  public static <E extends Comparable<? super E>> RulexMatchersBuilder<Model> modelVerb(
          final E arg)
  {
    return verb(Model.class, arg);
  }
}
