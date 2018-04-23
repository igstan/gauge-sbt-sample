import scala.sys.process._
import sbt._
import sbt.Keys._

object Gauge extends AutoPlugin {
  object autoImport {
    val gauge = taskKey[Unit]("Run Gauge specifications")
    val gaugePath = settingKey[String]("Gauge executable path")
    val gaugeVersion = settingKey[String]("Gauge version to use")
    val gaugeRunOptions = settingKey[Seq[String]]("Gauge options for its `run` command")
    val gaugeSpecsDir = settingKey[String]("Gauge path to specifications directory")
  }

  override val trigger = PluginTrigger.AllRequirements

  val gaugeTask: Def.Initialize[Task[Int]] =
    Def.task {
      import autoImport._

      val _ = (Test / compile).value
      val log = streams.value.log
      val classpath = (Test / fullClasspath).value.files.absString
      val cmd = Seq(gaugePath.value, "run") ++ gaugeRunOptions.value ++ Seq(gaugeSpecsDir.value)
      val cwd = baseDirectory.value
      val env = "gauge_custom_classpath" -> classpath

      // Enable using: `gauge / logLevel := Level.Debug`.
      log.debug(s"Gauge cmd: $cmd")
      log.debug(s"Gauge cwd: $cwd")
      log.debug(s"Gauge env: $env")

      log.info("Running Gauge...")
      Process(cmd, cwd, env) ! log
    }

  override val projectSettings: Seq[Def.Setting[_]] = {
    import autoImport._

    Seq(
      gauge           := gaugeTask.value,
      gaugePath       := "gauge",
      gaugeVersion    := "0.6.6",
      gaugeSpecsDir   := "specs",
      gaugeRunOptions := Seq.empty,
      libraryDependencies +=
        "com.thoughtworks.gauge" % "gauge-java" % gaugeVersion.value % "test",
    )
  }
}
