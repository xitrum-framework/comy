import sbt._
import Keys._

object MyBuild extends Build {
  val mySettings = Defaults.defaultSettings ++ Seq(
    organization := "tv.cntt",
    name         := "comy",
    version      := "1.3-SNAPSHOT",
    scalaVersion := "2.9.0-1"
  )

  val myResolvers = Seq(
    // For Xitrum
    "Sonatype Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots",

    // For Netty 4, remove this when Netty 4 is released
    "Local Maven Repository"       at "file://" + Path.userHome.absolutePath + "/.m2/repository"
  )

  val myLibraryDependencies = Seq(
    "tv.cntt"        %% "xitrum"            % "1.1-SNAPSHOT",
    "ch.qos.logback" %  "logback-classic"   % "0.9.28",
    "org.mongodb"    %  "mongo-java-driver" % "2.5.3"
  )

  lazy val project = Project (
    "project",
    file ("."),
    settings = mySettings ++ Seq(
      resolvers           := myResolvers,
      libraryDependencies := myLibraryDependencies,

      mainClass           := Some("comy.Boot"),
      distTask,
      unmanagedBase in Runtime <<= baseDirectory { base => base / "config" }
    )
  )

  // Task "dist" ---------------------------------------------------------------

  val dist = TaskKey[Unit]("dist", "Prepare target/dist directory ready for production distribution")

  lazy val distTask = dist <<= (externalDependencyClasspath in Runtime, baseDirectory, target, scalaVersion) map { (libs, baseDir, target, scalaVersion) =>
    val distDir = new File(target,  "dist")

    // Copy bin directory
    val binDir1 = new File(baseDir, "bin")
    val binDir2 = new File(distDir, "bin")
    IO.copyDirectory(binDir1, binDir2)
    for (file <- binDir2.listFiles)
      if (file.isFile && file.name.endsWith("sh"))
        file.setExecutable(true)

    // Copy config directory
    val configDir1 = new File(baseDir, "config")
    val configDir2 = new File(distDir, "config")
    IO.copyDirectory(configDir1, configDir2)

    // Copy public directory
    val publicDir1 = new File(baseDir, "public")
    val publicDir2 = new File(distDir, "public")
    IO.copyDirectory(publicDir1, publicDir2)

    // Copy lib directory
    val libDir = new File(distDir, "lib")

    // Copy dependencies
    libs.foreach { lib => IO.copyFile(lib.data, new File(libDir + "/%s".format(lib.data.getName))) }

    // Copy .jar files are created after running "sbt package"
    val jarDir = new File(target, "scala-" + scalaVersion.replace('-', '.'))
    for (file <- jarDir.listFiles)
      if (file.isFile && file.name.endsWith("jar"))
        IO.copyFile(file, new File(libDir + "/%s".format(file.getName)))
  }
}
