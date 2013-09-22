import ru.rulex.conclusion.Model
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn

rule = {
    onEvent event
    operation callOn(Model.class).getInteger() more 8
    operation callOn(Model.class).getFloat() less 81.7f
    operation callOn(Model.class).getOtherInteger() atLeast 9
    operation callOn(Model.class).getFloat() atMost 9.1f
    eval()
}