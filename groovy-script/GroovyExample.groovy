
import ru.rulex.conclusion.Phrases
import ru.rulex.conclusion.PhraseBuildersFacade.GuiceEventOrientedPhrasesBuilder
import static ru.rulex.conclusion.guice.GuiceMutableDependencyAnalyzerModule.*
import static com.google.inject.Guice.createInjector

println " incoming value ${foo}"

def injector = createInjector($expression(Phrases.ANY_TRUE,
   $more(90, { foo0 -> return foo0.getInteger() }, "int condition" ),
	 $less(56, { foo0 -> return foo0.getInteger() }, "other int condition")))

output = injector.getInstance(GuiceEventOrientedPhrasesBuilder.class).async(foo).checkedGet()