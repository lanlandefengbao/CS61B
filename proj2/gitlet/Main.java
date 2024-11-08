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
        if(args.length == 0) {
            System.out.println("Must have at least one argument");
            System.exit(0);
        }
        String firstArg = args[0];

        switch(firstArg) {
            case "init":
                setupPersistence();
                Commit c = new Commit();
                c.makeCommit();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
        }
    }

    /** set up the directory for gitlet system. */
    private static void setupPersistence() {
        if(Repository.GITLET_SYSTEM.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        Repository.COMMIT_FOLDER.mkdirs();
        Repository.BRANCH_FOLDER.mkdirs();
    }
}
