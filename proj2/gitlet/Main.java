package gitlet;

import java.io.File;
import java.util.List;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author B Li
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    static final File CWD = new File(System.getProperty("user.dir"));

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Must have at least one argument");
            System.exit(0);
        }
        String firstArg = args[0];

        switch(firstArg) {
            case "init":
                Commit c = new Commit();
                c.setupPersistence();
                break;
            case "add":
                File f = new File(args[1]).getAbsoluteFile(); /** For file object that represents relative path, getAbsoluteFile will complete it based on CWD. */
                if(f.exists() && f.isFile()) {
                    new Watcher().addOne(f);
                }
                break;
            case "commit":
                if(args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
            case "rm":
            case "log":
            case "global_log":
            case "find":
            case "status":
            case "branch":
            case "rm-branch":
            case "checkout":
                break;
            // TODO: FILL THE REST IN
        }
    }

}
