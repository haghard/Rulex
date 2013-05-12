package ru.dsl;

class Person {
  String name
}

class GroupGreeter {
  def persons = []
  String lang;

  def greetings = [
    english: ["Hi", "and"],
    french: ["Salut", "et"],
    german: ["Hallo", "und"],
    spanish: ["Hola", "y"],
    italian: ["Ciao", "e"],
    portugese: ["Oi", "e"]]

  def greetAll() {
    def greeting
    if (persons.size == 1) {
      greeting = "${greetings[lang][0]} ${persons[0].name}"
    } else {
      greeting = greetings[lang][0]
      def counter = 1
      def sep     = ","

      persons.each {
        sep = (counter++ < persons.size) ? "," : " ${greetings[lang][1]}"
        greeting = greeting + "$sep ${it.name}"
      }
    }

    println greeting
  }
}

class GroupGreetingNode extends AbstractFactory {
  def groupGreeter;
  public Object newInstance(FactoryBuilderSupport builder, Object nodeName, Object nodeArgs, Map nodeAttribs) {
    groupGreeter = new GroupGreeter("lang": nodeArgs)
    return groupGreeter
  }

  public boolean isLeaf() {
    return false
  }
}

class GreetingNode extends AbstractFactory {
  public Object newInstance(FactoryBuilderSupport builder, Object nodeName, Object nodeArgs, Map nodeAttribs) {
    return new Person("name": nodeArgs)
  }

  public void setParent(FactoryBuilderSupport builder, Object parentNode, Object childNode) {
    parentNode.persons.add(childNode)
  }

  public boolean isLeaf() {
    return true
  }
}

class GreetingBuilder extends FactoryBuilderSupport {
  GroupGreetingNode grpGreetingNode

  def GreetingBuilder() {
    registerFactories()
  }

  def registerFactories() {
    grpGreetingNode = new GroupGreetingNode()
    registerFactory "using", grpGreetingNode
    registerFactory "greet", new GreetingNode()
  }

  def build(){
    grpGreetingNode.groupGreeter.greetAll()
  }
}


GreetingBuilder bldr = new GreetingBuilder()
bldr.using("german") {
  greet("Jon")
  greet("David")
  greet("Ella")
}

bldr.build()

bldr.using("french") {  greet("Mireille")  }

bldr.build()
