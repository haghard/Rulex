import static ru.rulex.conclusion.guice.GuiceMutableDependencyAnalyzerModule.$expression
import static ru.rulex.conclusion.guice.GuiceMutableDependencyAnalyzerModule.$more
import static ru.rulex.conclusion.guice.GuiceMutableDependencyAnalyzerModule.$less
import static ru.rulex.conclusion.guice.GuiceMutableDependencyAnalyzerModule.$or

import static com.google.inject.Guice.createInjector;
import ru.rulex.conclusion.*
import ru.rulex.conclusion.PhraseBuildersFacade.GuiceEventOrientedPhrasesBuilder
import ru.rulex.conclusion.PhraseBuildersFacade.EventOrientedPhrasesBuilder
import ru.rulex.conclusion.Model;


println new EventOrientedPhrasesBuilder() {
    @Override
    protected void build() {
      configure(Model.class, "single-event-predicates-groovy")
        .shouldMatch({ input -> return input.getInteger() }, { argument -> return argument == 11 } );
    }
  }.sync(Model.from(11));


// selector/predicate map
println new EventOrientedPhrasesBuilder() {
  @Override
  protected void build() {
    configure(Model.class, "single-event-predicates-groovy")
      .shouldMatch(
        [ selector: { input -> return input.getInteger() }, 
         predicate: { argument -> return argument == 11 } ]);
  }
}.sync(Model.from(11));


def foo = Model.from(91, 100.90f)


// (90 > x.getInt() ) or (56 < x.getInt() )
def injector = createInjector(
  $expression(Phrases.ANY_TRUE,
    $more(90, { foo0 -> return foo0.getInteger() }, "(90 > x.getInt() )"),
    $less(56, { foo0 -> return foo0.getInteger() }, "(56 < x.getInt() )",)))

println "All ${injector.getInstance(GuiceEventOrientedPhrasesBuilder.class).async(foo).checkedGet()} "

// do the same
def injector0 = createInjector(
  $expression(
    $or("(90 > x.getInt() ) or (56 < x.getInt() )",
      $more(90, { foo0 -> return foo0.getInteger() }, "90 > x.getInt()"),
      $less(56, { foo0 -> return foo0.getInteger() }, "56 < x.getInt()"))))

println "All ${injector0.getInstance(GuiceEventOrientedPhrasesBuilder.class).async(foo).checkedGet()} "