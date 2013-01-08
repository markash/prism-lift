name := "LiftBasicExample"
 
scalaVersion := "2.9.2"
 
seq(webSettings :_*)


resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
resolvers += "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= {
  val liftVersion = "2.5-M2" 
  Seq(
    "net.liftweb" % "lift-webkit"  % liftVersion % "compile->default"
  )    
}

// Customize any further dependencies as desired
libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-webapp" % "7.3.0.v20110203" % "container,test",
  "org.scala-tools.testing" % "specs_2.9.0" % "1.6.8" % "test", // For specs.org tests
  "junit" % "junit" % "4.8" % "test", // For JUnit 4 testing
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "com.h2database" % "h2" % "1.2.138", // In-process database, useful for development systems
  "ch.qos.logback" % "logback-classic" % "1.0.0" % "compile",
  "org.slf4j" % "jcl-over-slf4j" % "1.6.4" // only used for debugging.
)


