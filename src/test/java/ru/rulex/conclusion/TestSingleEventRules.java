package ru.rulex.conclusion;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static ru.rulex.conclusion.FluentConclusionPredicate.number;
import static ru.rulex.conclusion.FluentConclusionPredicate.query;
import static ru.rulex.conclusion.FluentConclusionPredicate.typeSafeQuery;
import static ru.rulex.conclusion.RulexMatchersDsl.eq;
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn;

import java.util.concurrent.TimeUnit;

import org.apache.onami.test.OnamiRunner;
import org.apache.onami.test.annotation.GuiceProvidedModules;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.rulex.conclusion.PhraseBuildersFacade.EventOrientedPhrasesBuilder;

import com.google.common.base.Joiner;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;

import static com.google.inject.name.Names.named;

@RunWith(OnamiRunner.class)
public class TestSingleEventRules {
	final static String NAME = "testEventOrientedPhrasesBuilderWithProxy";
	final static String NAME1 = "testEventOrientedPhrasesBuilderWithTypeSafeSelector";

	@Inject @Named( NAME )
  PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder builder;

	@Inject @Named( NAME1 )
  PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder builder1;
	
	@GuiceProvidedModules
	public static Module createEventOrientedPhrasesBuilderWithProxy() {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder.class).annotatedWith(named(NAME)).toInstance(
					new EventOrientedPhrasesBuilder() 
					{
					  @Override
					  protected void build() 
				      {
					    through(Model.class, "fact: [getInteger() == 211]")
						  .shouldMatch(query(callOn(Model.class).getInteger(), eq(211), Model.class));
					  }
					});

				bind(PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder.class).annotatedWith(named(NAME1)).toInstance(
				  new EventOrientedPhrasesBuilder()
				  {
					@Override
					protected void build()
					{
					  through( Model.class, "fact: [getInteger() == 11]" ).shouldMatch(
					            typeSafeQuery( number( Model.class, Integer.class, Model.INT_ACCESSOR ), eq( 11 ) ) );
					}
			      });
			}
		};
	}

	@Test
	public void testEventOrientedPhrasesBuilderWithProxy() {
		final String errorMessage = Joiner.on("").join(NAME," error !!!");
		try
	    {
	      assertThat( builder.async( Model.values( 211 ) ).checkedGet( 1, TimeUnit.SECONDS ) )
	          .isTrue().as( errorMessage );
	    }
	    catch (Exception ex)
	    {
	      fail( errorMessage );
	    }
	}

	@Test
	public void testEventOrientedPhrasesBuilderWithTypeSafeSelector() {
		final String errorMessage = Joiner.on("").join(NAME1 ," error !!!" );
		try
	    {
	      assertThat( builder1.async( Model.values( 11 ) ).checkedGet( 1, TimeUnit.SECONDS ) ).isTrue()
	          .as( errorMessage );

	      assertThat( builder1.async( Model.values( 12 ) ).checkedGet( 1, TimeUnit.SECONDS ) ).isFalse()
	          .as( errorMessage );

	      assertThat( builder1.async( Model.values( 11 ) ).checkedGet( 1, TimeUnit.SECONDS ) ).isTrue()
	          .as( errorMessage );
	    }
	    catch (Exception ex)
	    {
	      fail( errorMessage );
	    }
	}
}
