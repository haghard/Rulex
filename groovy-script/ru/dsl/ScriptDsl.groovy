package ru.dsl

import groovy.json.JsonBuilder
import groovy.xml.MarkupBuilder

class MemoDsl {

  def toText
  def fromText
  def body
  def List<Map<String, Object>> sections = []

  def static task(Closure closure) {
    def block = closure.clone()
    def sampleDsl = new MemoDsl()
    // any method called in closure will be delegated to the sampleDsl class
    block.resolvingStrategy = Closure.DELEGATE_FIRST
    block.delegate = sampleDsl
    block()
  }

  def to(String toText) { this.toText = toText }

  def from(String fromText) { this.fromText = fromText }

  def body(String bodyText) { this.body = bodyText }

  def methodMissing(String methodName, args) {
    Map<String, Object> section = [title: methodName, body: args[0]]
    sections << section
  }

  def getText() { doText(this) }

  def getXml() { doXml(this) }

  def getHtml() { doHtml(this) }

  def when(boolean condition, Closure closure) {
    if (condition) closure()
  }

  def until(boolean condition, Closure closure) {
    while(!condition) closure()
  }

  def until(Closure condition, Closure closure) {
    while(!condition()) closure()
  }

  private static doXml(MemoDsl memoDsl) {
    def writer = new StringWriter()
    MarkupBuilder xml = new MarkupBuilder(writer)
    xml.memo() {
      to(memoDsl.toText)
      from(memoDsl.fromText)
      body(memoDsl.body)
      // cycle through the stored section objects to create an xml tag
      for (s in memoDsl.sections) {
        "$s.title"(s.body)
      }
    }
    println writer
  }

  private static doHtml(MemoDsl memoDsl) {
    def writer = new StringWriter()
    MarkupBuilder xml = new MarkupBuilder(writer)
    xml.html() {
      head { title("Memo") }
      body {
        h1("Memo")
        h3("To: ${memoDsl.toText}")
        h3("From: ${memoDsl.fromText}")
        p(memoDsl.body)
        // cycle through the stored section objects and create uppercase/bold section with body
        for (s in memoDsl.sections) {
          p {
            b(s.title.toUpperCase())
          }
          p(s.body)
        }
      }
    }
    println writer
  }

  private static doText(MemoDsl memoDsl) {
    String template = "Memo\nTo: ${memoDsl.toText}\nFrom: ${memoDsl.fromText}\n${memoDsl.body}\n"
    def sectionStrings =""
    for (s in memoDsl.sections) {
      sectionStrings += s.title.toUpperCase() + "\n" + s.body + "\n"
    }

    template += sectionStrings
    println template
  }
}

println MemoDsl.task {
  to "Nirav Assar"
  from "Barack Obama"
  body "How are things? We are doing well. Take care"
  idea "The economy is key"
  request "Please vote for me"
  xml
}

println MemoDsl.task {
  to "Nirav Assar1"
  from "Barack Obama1"
  body "How are things? We are doing well. Take care"
  idea "The economy is key"
  request "Please vote for me"
  html
}

println MemoDsl.task {
  to "Nirav Assar1"
  from "Barack Obama1"
  body "How are things? We are doing well. Take care"
  idea "The economy is key"
  request "Please vote for me"
  text
}

class Address {
    int id
    String line
}

def List address = []

address << new Address( id: 1, line: "Street1" )
address << new Address( id: 2, line: "Street2" )
def range = (0..10)

range.each { print it }

def builder = new JsonBuilder()
builder.jsonRuleSchema() {
    name "Rule1"

    "counts" (
            range.collect {
                [id: it, line: "desc for " + it ]
            } )

//    "Address" (
//        address.each { add ->
//            "id" add.id
//            "line" add.line
//        }
//    )
    //addresses( address.collect { Address a -> [id: a.id, title: a.line] } )
}

println builder.toPrettyString()


class Event {
  def eventId
  def kkmNumber
  def price;
}


public interface ListExtractor<T> { List unapply( T source); }

public interface MapExtractor<T> { Map unapply( T source ); }

Closure<Boolean> assertionListClosure = { List list -> list[0] == 6 && list[1] == 56  }

Closure<Boolean> assertionMapClosure = { Map map -> map[1] == 6 && map[2] == 56  }

ListExtractor extractor = { Event event -> [ event.eventId, event.kkmNumber ] } as ListExtractor

MapExtractor mExtractor = { Event event -> [ 1: event.eventId, 2: event.kkmNumber ] } as MapExtractor


//println assertionListClosure.call( extractor.unapply( new Event(eventId: 6, kkmNumber: 56, price: 560f )))

//println assertionMapClosure.call( mExtractor.unapply( new Event(eventId: 6, kkmNumber: 56, price: 560f )))



println( [ new Event(eventId: 1, kkmNumber: 56, price: 560f ), new Event(eventId: 2, kkmNumber: 56, price: 560f )]
        .inject(true) { boolean acc, Event item -> acc & item.eventId == 2 } )

//println ({ Event event -> event.eventId } as ListExtractor).create(new Event(eventId: 6, kkmNumber: 56, price: 560f))
//({ int id -> id > 5 } as Closure).call(


/*
Closure<Boolean> curriedAssertion = closure.curry("-item")

Closure<Boolean> assertionClosure = { it.startsWith('XXX') }
assertionClosure.curry()

println closure { it.startsWith('XXX') }
*/