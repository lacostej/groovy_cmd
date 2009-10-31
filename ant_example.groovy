/** Example of having an interactive ant process **/
class MyProject extends Cmd {
  def ant = new AntBuilder()

  @Option(description="Hello world")
  def do_run() {
    ant.echo("hello")
  }
}

new MyProject().run()
