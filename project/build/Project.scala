import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) with assembly.AssemblyBuilder {
  // Compile options

  override def compileOptions = super.compileOptions ++
    Seq("-deprecation",
        "-Xmigration",
        "-Xcheckinit",
        "-encoding", "utf8")
        .map(x => CompileOption(x))

  override def javaCompileOptions = JavaCompileOption("-Xlint:unchecked") :: super.javaCompileOptions.toList

  // Repos ---------------------------------------------------------------------

  // For xitrum during development on local machine
  val localMaven       = "Local Maven"       at "file://"+ Path.userHome + "/.m2/repository"

  // For xitrum during development on different machines
  val sonatypeSnapshot = "Sonatype Snapshot" at "https://oss.sonatype.org/content/repositories/snapshots"

  // For netty
  val jboss            = "JBoss"             at "https://repository.jboss.org/nexus/content/groups/public/"

  override def libraryDependencies = Set(
    "tv.cntt"        %% "xitrum"            % "1.1-SNAPSHOT",
    "ch.qos.logback" %  "logback-classic"   % "0.9.28",
    "org.mongodb"    %  "mongo-java-driver" % "2.5.3"
  ) ++ super.libraryDependencies

  // Paths ---------------------------------------------------------------------

  override def unmanagedClasspath = super.unmanagedClasspath +++ ("config")

  override def mainClass = Some("comy.Boot")
}
