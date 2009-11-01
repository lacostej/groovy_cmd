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

  def mc = [
   compare: {a, b -> a[0].equals(b[0]) ? 0 : a[0] < b[0] ? -1 : 1 }
  ] as Comparator

  def commandMethods() {
    return this.class.methods.findAll{ it.name =~ /^do_[A-z]/ }
  }

  def commandMethod(String name) {
    return commandMethods().find{ commandName(it) == name }
  }

  def commandName(Method m) {
     return m.name[3..-1]
  }

  def commandDescription(Method m) {
     Option option = m.getAnnotation(Option.class)
     if (option == null)
       return null
     return option.description()
  }


  def commandNames() {
     return commandMethods().collect{ commandName(it) }
  }

  def methodHelp(Method m) {
    String desc = commandDescription(m)
    if (desc == null)
      return commandName(m)
     else
      return commandName(m) + ": " + desc
   }

  @Option(description="Terminates interpreter")
  def do_quit(List ignored) {
    System.exit(0)
  }

  @Option(description="Display global help or per command help")
  def do_help(List args) {
    if (args != null && args.size() > 0) {
      Method m = commandMethod(args[0])
      if (m == null)
        println "ERROR Unknown command: " + args[0]
      else
        println methodHelp(m)
      return
    }
    println "Available commands:"
    commandMethods().collect{ [commandName(it), methodHelp(it)]}.sort(mc).each {
      println it[1]
    }
  }

  def run() {
     reader.addCompletor(new SimpleCompletor (commandNames() as String[]))
     while (true) {
       String line = reader.readLine(prompt);
       if (line.size() == 0) {
         continue
       }
       if (line == null) {
         println ""
         break;
       }
       invoke(line)
     }
  }

  def argTypesAreString(Method method) {
    return method.getParameterTypes().find{ it != String.class } == null
  }

  def invoke(line) {
    String[] args = line.split(" ")
    List passed_args = args.size() > 1 ? args[1..-1] : [ ]
    Method method = commandMethod(args[0])
    if (method == null) {
      println "ERROR Unknown command: " + args[0] + ". Use help for a detailed list of available commands"
    } else {
      try {
        if (method.getParameterTypes().size() == 0) {
          method.invoke( this )
        } else if (method.getParameterTypes()[0] == List.class) {
          method.invoke( this, passed_args )
        } else if (argTypesAreString(method)) {
          method.invoke( this, passed_args.toArray() )
        } else {
          println "ERROR: " + method.name + " has non expected parameter type: " + method.getParameterTypes().find{ it != String.class }
        }
      } catch (Exception e) {
        println "ERROR " + e.getMessage()
        //e.printStackTrace()
      }
    }
  }
}
