import static ru.rulex.conclusion.guice.AbstractPhrasesAnalyzerModule.$eq;
import static ru.rulex.conclusion.guice.AbstractPhrasesAnalyzerModule.$expression;
import static ru.rulex.conclusion.guice.AbstractPhrasesAnalyzerModule.$more;

import static com.google.inject.Guice.createInjector;
import ru.rulex.conclusion.*
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder
import ru.rulex.conclusion.PhraseBuildersFacade.EventOrientedPhrasesBuilder

import static ru.rulex.conclusion.FluentConclusionPredicate.fromSelector
import static ru.rulex.conclusion.FluentConclusionPredicate.selector
import static ru.rulex.conclusion.guice.AbstractPhrasesAnalyzerModule.*
import ru.rulex.conclusion.Model;

println new EventOrientedPhrasesBuilder() {
    @Override
    protected void build() {
      through(Model.class, "single-event-predicates-groovy")
        .shouldMatch({ input -> return input.getInteger() }, { argument -> return argument == 11 } );
    }
  }.sync(Model.values(11));


// selector/predicate map
println new EventOrientedPhrasesBuilder() {
  @Override
  protected void build() {
    through(Model.class, "single-event-predicates-groovy")
      .shouldMatch(
        [ selector: { input -> return input.getInteger() }, 
         predicate: { argument -> return argument == 11 } ]);
  }
}.sync(Model.values(11));


def foo = Model.values(91, 100.90f)


// (90 > x.getInt() ) or (56 < x.getInt() )
def injector = createInjector(
  $expression(Phrases.ANY_TRUE,
    $more(90, { foo0 -> return foo0.getInteger() }, "(90 > x.getInt() )"),
    $less(56, { foo0 -> return foo0.getInteger() }, "(56 < x.getInt() )",)))

println "All ${injector.getInstance(PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder.class).async(foo).checkedGet()} "

// do the same
def injector0 = createInjector(
  $expression(
    $or("(90 > x.getInt() ) or (56 < x.getInt() )",
      $more(90, { foo0 -> return foo0.getInteger() }, "90 > x.getInt()"),
      $less(56, { foo0 -> return foo0.getInteger() }, "56 < x.getInt()"))))

println "All ${injector0.getInstance(PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder.class).async(foo).checkedGet()} "