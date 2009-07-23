gant clean
gant makeJar
cd test
groovy -cp '../WithLog.jar:../lib/log4j-1.2.9.jar' ExerciseScript.groovy
