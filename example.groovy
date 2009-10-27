class Example extends Cmd {

  def do_hello(List argList) {
    print "Hello "
    for (i in 0..<argList.size()) {
      if (i > 0)
        print ", "
      print argList[i]
    }
    println ""
  }
}

Example ex = new Example()

ex.do_hello(["world", "jean"])
ex.do_help()
ex.run()
