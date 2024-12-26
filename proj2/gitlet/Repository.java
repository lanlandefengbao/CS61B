package gitlet;

import java.io.File;
import java.util.Date;

import static gitlet.Utils.*;

/** An object of this class represents a repository with Gitlet system.
 *  The instance variables necessary files for a Gitlet system.
 *
 *  @author B Li
 */
public class Repository {

    /** The root folder of current project */
    public File PROJECT_FOLDER = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public final File GITLET_SYSTEM = Utils.join(PROJECT_FOLDER, ".gitlet");

    /** Stores all commit objects AND Blob objects */
    public final File OBJECT_FOLDER = Utils.join(GITLET_SYSTEM, "objects");

    /** Stores the reference of the current HEAD commit.
     * If HEAD is pointing to a branch, the .git/HEAD file will contain a reference to that branch, like ".gitlet/refs/head/master"
     * If in detached Head state (not on any branch, but on a specific commit), the .git/HEAD file will contain the commit hash directly. */
    public final File HEAD = Utils.join(GITLET_SYSTEM, "HEAD");

    /** Stores pointers of different branches */
    public final File REF_FOLDER = Utils.join(GITLET_SYSTEM, "refs");
    public final File LOCAL_BRANCH_FOLDER = Utils.join(REF_FOLDER, "heads");
    public final File MASTER = Utils.join(LOCAL_BRANCH_FOLDER, "master");

    /** StagingArea */
    public final File STAGING_FILE = Utils.join(GITLET_SYSTEM, "index");

    /** Stores remote repositories */
    public final File REMOTE_REPO_FOLDER = Utils.join(REF_FOLDER, "remotes");

    /** Constructor */
    /** By default, that is, without naming a project folder, running Gitlet system on CWD */
    public Repository() {
    }

    /** Running Gitlet system under a specific project folder */
    public Repository(File projectFolder) {
        PROJECT_FOLDER = projectFolder;
    }
}
