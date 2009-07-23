import com.smokejumperit.logging.*

@WithLog
class Foo {
	static doLog() { log.info("This is logged!") }
}
Foo.doLog()

@com.smokejumperit.logging.WithLog
class Bar {
	static doLog() { log.info("This is logged, too!") }
}
Bar.doLog()

@WithLog
class Baz {
	def doLog() { log.info("And this!  Wow!") }
}
new Baz().doLog()
