// Scalate template engine config for Xitrum
// "import" must be at top of build.sbt, or SBT will complain
import ScalateKeys._

// Precompile Scalate
seq(scalateSettings:_*)

scalateTemplateConfig in Compile := Seq(TemplateConfig(
  file("src") / "main" / "scalate",
  Seq(),
  Seq(Binding("helper", "xitrum.Action", true))
))

libraryDependencies += "tv.cntt" %% "xitrum-scalate" % "1.9"

//------------------------------------------------------------------------------

organization := "tv.cntt"

name         := "comy"

version      := "1.7-SNAPSHOT"

scalaVersion := "2.11.0"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// Xitrum requires Java 7
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

//------------------------------------------------------------------------------

// Most Scala projects are published to Sonatype, but Sonatype is not default
// and it takes several hours to sync from Sonatype to Maven Central
resolvers += "SonatypeReleases" at "http://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "tv.cntt" %% "xitrum" % "3.11"

// An implementation of SLF4J must be provided for Xitrum
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "org.mongodb" %  "mongo-java-driver" % "2.11.3"

// xgettext i18n translation key string extractor is a compiler plugin ---------

autoCompilerPlugins := true

addCompilerPlugin("tv.cntt" %% "xgettext" % "1.0")

scalacOptions += "-P:xgettext:xitrum.I18n"

// Put config directory in classpath for easier development --------------------

// For "sbt console"
unmanagedClasspath in Compile <+= (baseDirectory) map { bd => Attributed.blank(bd / "config") }

// For "sbt run"
unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "config") }

// Copy these to target/xitrum when sbt/sbt xitrum-package is run
XitrumPackage.copy("config", "public", "script")
