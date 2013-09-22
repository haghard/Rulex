import ru.rulex.conclusion.Model
import static ru.rulex.conclusion.delegate.ProxyUtils.callOn

rule = {
    onEvent event
    from callOn(Model.class).getInteger() more 8
    from callOn(Model.class).getFloat() less 81.7f
    from callOn(Model.class).getOtherInteger() atLeast 9
    from callOn(Model.class).getFloat() atMost 9.1f
    eval()
}