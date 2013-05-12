package ru.rulex.conclusion;

import org.junit.Test;
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder;
import ru.rulex.conclusion.PhraseBuildersFacade.SimpleEventOrientedPhrasesBuilder;
import ru.rulex.matchers.RulexMatcher;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.rulex.conclusion.RulexMatchersDsl.*;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;
import static ru.rulex.matchers.SelectorsRulexDsl.*;

public class TestSimpleEventOrientedPhrasesBuilder {

  @Test
  public void testSimpleEventOrientedPhrasesBuilder()
      throws PhraseExecutionException {
    final AbstractEventOrientedPhrasesBuilder builder = new SimpleEventOrientedPhrasesBuilder() {
      protected void build() {
        as("fact: [getInteger() == 999]").shouldMatch(
            callOn(Model.class).getInteger(), eq(999));
      }
    };

    assertThat(builder.async(Model.values(999)).checkedGet()).isTrue();
    assertThat(builder.async(Model.values(9991)).checkedGet()).isFalse();

    assertThat(builder.sync(Model.values(999))).isTrue();
  }

  @Test
  public void testRulexMatcherChainCalls() {
    Model model = Model.values(4, 56.7f);
    RulexMatcher<Model> matcher = 
        selectors(Model.class, callOn(Model.class).getInteger()).lessThan(3)
        .and(selectors(Model.class, callOn(Model.class).getFloat()).lessThan(56.6f));

    assertThat(matcher.matches(model)).isTrue();
  }
}
