import jline.ConsoleReader
import jline.SimpleCompletor
import java.lang.reflect.Method

/**
 * Provides support for creating line-oriented command interpreters.
 *
 * Commands are implemented in methods named do_xxx(List arguments) in sub-classes
 * Program is started by calling run() on the instance
 *
 * Similar to Python's cmd module: http://docs.python.org/library/cmd.html
 */
class Cmd {
  ConsoleReader reader = new ConsoleReader()
  String prompt = "> "

  def commands() {
     return this.class.methods.findAll{ it.name =~ /^do_[A-z]/ }.collect{ it.name[3..-1] }
  }

  def do_quit(List ignored) {
    System.exit()
  }

  def do_help(List messages) {
     messages.each { println it }            
     println "Available commands"
     commands().sort().each { println it }
   }

  def run() {
     reader.addCompletor(new SimpleCompletor (commands() as String[]))
     while (true) {
       String line = reader.readLine(prompt);
       if (line == null) {
         println ""
         break;
       }
       invoke(line)
     }
  }
  def invoke(line) {
    String[] args = line.split(" ")
    List passed_args = args.size() > 1 ? args[1..-1] : [ null ]
    Method method = this.getClass().getMethods().find{ it.name == 'do_' + args[0] }
    if (method == null)
      do_help(["ERROR Unknown command: " + args[0]])
    else
      method.invoke( this, passed_args )
  }
}
