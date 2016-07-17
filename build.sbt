organization := "tv.cntt"
name         := "comy"
version      := "1.7-SNAPSHOT"

scalaVersion := "2.11.8"
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// Xitrum requires Java 8
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

//------------------------------------------------------------------------------

libraryDependencies += "tv.cntt" %% "xitrum" % "3.27.0"

// An implementation of SLF4J must be provided for Xitrum
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"

// For writing condition in logback.xml
libraryDependencies += "org.codehaus.janino" % "janino" % "2.7.8"

libraryDependencies += "org.mongodb" %  "mongo-java-driver" % "3.2.2"

// Scalate template engine config for Xitrum -----------------------------------

libraryDependencies += "tv.cntt" %% "xitrum-scalate" % "2.6.0"

// Precompile Scalate templates
Seq(scalateSettings:_*)
ScalateKeys.scalateTemplateConfig in Compile := Seq(TemplateConfig(
  baseDirectory.value / "src" / "main" / "scalate",
  Seq.empty,
  Seq(Binding("helper", "xitrum.Action", importMembers = true))
))

// xgettext i18n translation key string extractor is a compiler plugin ---------

autoCompilerPlugins := true
addCompilerPlugin("tv.cntt" %% "xgettext" % "1.3")
scalacOptions += "-P:xgettext:xitrum.I18n"

// Put config directory in classpath for easier development --------------------

// For "sbt console"
unmanagedClasspath in Compile <+= baseDirectory map { bd => Attributed.blank(bd / "config") }

// For "sbt run"
unmanagedClasspath in Runtime <+= baseDirectory map { bd => Attributed.blank(bd / "config") }

// Copy these to target/xitrum when sbt/sbt xitrum-package is run
XitrumPackage.copy("config", "public", "script")
