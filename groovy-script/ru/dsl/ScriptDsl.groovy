package ru.dsl

import groovy.xml.MarkupBuilder

import groovy.transform.CompileStatic

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.*

//@CompileStatic
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
/*
def binding = new Binding([receiver: 'Nirav Assar', sender: 'Barack Obama'])

def shell = new GroovyShell(binding)

shell.evaluate '''
    import static ru.dsl.MemoDsl.task;

    MemoDsl.task {
      to receiver
      from sender
      body "How are things? We are doing well. Take care"
      idea "The economy is key"
      request "Please vote for me"
      text
    }
    '''
*/
