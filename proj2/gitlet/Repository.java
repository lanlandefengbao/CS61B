package gitlet;

import java.io.File;
import java.util.Date;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author B Li
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The root folder of current project */
    public static final File PROJECT_FOLDER = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_SYSTEM = Utils.join(PROJECT_FOLDER, ".gitlet");

    /** Stores all commit objects AND Blob objects */
    public static final File OBJECT_FOLDER = Utils.join(GITLET_SYSTEM, "objects");

    /** Stores the reference of the current HEAD commit.
     * If HEAD is pointing to a branch, the .git/HEAD file will contain a reference to that branch, like ".gitlet/refs/head/master"
     * If in detached Head state (not on any branch, but on a specific commit), the .git/HEAD file will contain the commit hash directly. */
    public static final File HEAD = Utils.join(OBJECT_FOLDER, "HEAD");

    /** Stores pointers of different branches */
    public static final File BRANCH_FOLDER = Utils.join(GITLET_SYSTEM, "refs");
    public static final File LOCAL_BRANCH = Utils.join(BRANCH_FOLDER, "heads");
    public static final File MASTER = Utils.join(LOCAL_BRANCH, "MASTER");

    /** StagingArea */
    public static final File STAGING_FILE = Utils.join(GITLET_SYSTEM, "index");
}
