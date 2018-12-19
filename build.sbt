name := """owlRestAPI"""
organization := "ncsu.csc750"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += "com.github.galigator.openllet" % "openllet-jena" % "2.6.4"

resolvers += "public-jboss" at "http://repository.jboss.org/nexus/content/groups/public-jboss/"
libraryDependencies ++= Seq(
"org.drools" % "drools-core" % "7.3.0.Final",
"org.drools" % "drools-compiler" % "7.3.0.Final")

// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in Compile, compile in Test)

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java           // Java project. Don't expect Scala IDE
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)  // Use .class files instead of generated .scala files for views and routes
