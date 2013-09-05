
import ru.rulex.conclusion.Phrases
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhraseBuilder

import static ru.rulex.conclusion.guice.AbstractPhrasesAnalyzerModule.*
import static com.google.inject.Guice.createInjector

println " incoming value ${foo}"

def injector = createInjector($expression(Phrases.ANY_TRUE,
   $more(90, { foo0 -> return foo0.getInteger() }, "int condition" ),
	 $less(56, { foo0 -> return foo0.getInteger() }, "other int condition")))

output = injector.getInstance(AbstractEventOrientedPhraseBuilder.class).async(foo).checkedGet()