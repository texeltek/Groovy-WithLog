@WithLog
class Foo {
	static doLog() { 
		println("Foo sez: I have a ${log.getClass().name}, and it is named ${log.name}")
		log.info("This is logged!")
	}
}
Foo.doLog()

@com.smokejumperit.sublog.WithLog
class Bar {
	static doLog() { 
		println("Bar sez: I have a ${log.getClass().name}, and it is named ${log.name}")
		log.info("This is logged, too!") 
	}
}
Bar.doLog()

@WithLog
class Baz {
	def doLog() { 
		println("Baz sez: I have a ${log.getClass().name}, and it is named ${log.name}")
		log.info("And this!  Wow!") 
	}
}
new Baz().doLog()
