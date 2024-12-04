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

    public static void main(String[] args) {

        final File CWD = new File(System.getProperty("user.dir"));

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
                if(args.length != 2) {
                    if(args.length == 1) {
                        System.out.println("Please enter a file name.");
                        System.exit(0);
                    } else {
                        System.out.println("Wrong number of arguments.");
                        System.exit(0);
                    }

                }
                if(args[1].equals(".")) {
                    new Watcher().updateAll();
                } else {
                    File f = new File(args[1]).getAbsoluteFile(); /** For file object that represents relative path, getAbsoluteFile will complete it based on CWD. */
                    if (f.exists() && f.isFile()) {
                        new Watcher().addOne(f);
                    }
                }
                break;
            case "commit":
                if(args.length != 2) {
                    if(args.length == 1) {
                        System.out.println("Please enter a commit message.");
                        System.exit(0);
                    } else {
                        System.out.println("Wrong number of arguments.");
                        System.exit(0);
                    }
                }
                new Commit().makeCommit(args[1]);
                break;
            case "status":
                new Watcher().getStatus();
                break;
            // TODO: FILL THE REST IN
        }
    }

}
