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
//    static final File CWD = new File(System.getProperty("user.dir"));

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Must have at least one argument");
            System.exit(0);
        }
        String firstArg = args[0];
        File CWD = new File(System.getProperty("user.dir"));
        switch(firstArg) {
            case "init":
                File GITLET_SYSTEM = Utils.join(CWD, ".gitlet");
                if(GITLET_SYSTEM.exists()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    System.exit(0);
                }
                // Construct a new Gitlet version-control system in the current directory, and write the initial Commit object to a file in the Gitlet system.
                Commit c = new Commit();
                c.setupPersistence(GITLET_SYSTEM);
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
        }
    }

    /** set up the directory for gitlet system. */
    private void setupPersistence() {

    }
}
