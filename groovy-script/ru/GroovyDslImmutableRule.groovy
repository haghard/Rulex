rule = {
    onEvent event
    operation callOn(Model.class).getInteger() more 8
    operation callOn(Model.class).getFloat() less 81.7f
    operation callOn(Model.class).getOtherInteger() atLeast 9
    operation callOn(Model.class).getFloat() atMost 9.1f
    eval()
}

rule2 = {
    onEvent event
    $ objectId more 7
    $ eventType atMost 2
    $ objectPrice atMost 80.99
    $ objectName equalsAnyOff( ["snickers", "mars", "picnic"] )
    eval()
}
