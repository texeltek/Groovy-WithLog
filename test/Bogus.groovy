class Foo {
	static doLog() { log.info("This won't even compile!") }
}
Foo.metaClass.static.log = [info:{ println it }]
