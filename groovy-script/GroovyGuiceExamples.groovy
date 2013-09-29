import ru.rulex.conclusion.PhraseBuildersFacade.GuiceImmutablePhrasesBuilder
import static ru.rulex.conclusion.guice.AbstractGuiceImmutablePhraseModule.*
import static com.google.inject.Guice.createInjector
import static ru.rulex.conclusion.guice.PhraseDslBuilders.val;

println " incoming value ${foo}"

def injector = createInjector( immutablePhrase(
   val( 90 ).more( { foo0 -> return foo0.getInteger() } ),
   val( 56 ).less( { foo0 -> return foo0.getInteger() } ),
   val( { foo0 -> return foo0.getInteger() } ).isNotNull()
   ))

output = injector.getInstance(GuiceImmutablePhrasesBuilder.class).async(foo).checkedGet()