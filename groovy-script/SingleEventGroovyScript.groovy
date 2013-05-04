import ru.rulex.conclusion.Model;

println "incoming value ${foo}"

def cl = { foo0 -> foo0.getInteger() > 10 } 
def cl0 = { foo0 -> foo0.getFloat() < 89.5f }
def cl1 = { foo0 -> foo0.getString().startsWith("a") }

output = cl(foo) & cl0 (foo) & cl1 (foo)  