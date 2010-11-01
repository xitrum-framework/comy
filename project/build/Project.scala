import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  // Compile options

  override def compileOptions = super.compileOptions ++
    Seq("-deprecation",
        "-Xmigration",
        "-Xcheckinit",
        "-Xwarninit",
        "-encoding", "utf8")
        .map(x => CompileOption(x))

  override def javaCompileOptions = JavaCompileOption("-Xlint:unchecked") :: super.javaCompileOptions.toList

  // Repos ---------------------------------------------------------------------

  override def libraryDependencies = Set(
    "cntt"           %% "xitrum"            % "0.1-SNAPSHOT",
    "ch.qos.logback" %  "logback-classic"   % "0.9.26",
    "org.mongodb"    %  "mongo-java-driver" % "2.3"
  ) ++ super.libraryDependencies

  // Paths ---------------------------------------------------------------------

  override def unmanagedClasspath = super.unmanagedClasspath +++ ("config")

  override def mainClass = Some("comy.Boot")
}
