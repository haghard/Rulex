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
