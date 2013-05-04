import com.google.inject.Guice
import ru.rulex.conclusion.*
import ru.rulex.conclusion.Model
import ru.rulex.conclusion.PhraseBuildersFacade.AbstractEventOrientedPhrasesBuilder
import static ru.rulex.conclusion.FluentConclusionPredicate.selector
import static ru.rulex.conclusion.guice.AbstractPhrasesAnalyzerModule.*
import static com.google.inject.Guice.createInjector;

println " incoming value ${foo}"

def injector = createInjector($expression(Phrases.ANY_TRUE,
   $more(90, { foo0 -> return foo0.getInteger() }, "int condition" ),
	 $less(56, { foo0 -> return foo0.getInteger() }, "other int condition")))

output = injector.getInstance(AbstractEventOrientedPhrasesBuilder.class).async(foo).checkedGet()