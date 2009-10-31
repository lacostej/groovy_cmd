class Example extends Cmd {

  @Option(description="Hello world")
  def do_hello(List argList) {
    print "Hello "
    for (i in 0..<argList.size()) {
      if (i > 0)
        print ", "
      print argList[i]
    }
    println ""
  }

  @Option(description="This method uses String arguments")
  def do_hello2(String arg1, String arg2) {
    def list = []
    list << arg1 << arg2
    do_hello(list)
  }

  @Option(description="This method has unsupported argument types")
  def do_unsupportedArgs(String arg1, int arg2) {
  }
}

Example ex = new Example()

ex.do_hello(["world", "jean"])
ex.do_help()
ex.run()
