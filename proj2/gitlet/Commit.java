package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author B Li
 */
public class Commit implements Serializable {

    /** All instance variables of a Commit object */
    String timeStamp;
    String logMessage;
    Map<File, String> Blobs = new HashMap<>();
    List<String> Parent = new ArrayList<>();

    /** Construct the initial commit object */
    public Commit() {
        logMessage = "initial commit";
        timeStamp = new Date(0).toString();
    }

    /** Construct the commit object based on specific args */
    public Commit(String logMessage, String timeStamp) {}

    /** The message of this Commit. */
    private String message;

    /** Calculate SHA-1 for a normal Commit object. */
    public String hash() {
        return Utils.sha1((Object) Utils.serialize(this));
    }


    /** Set up the initial gitlet system if we don't have one yet. */
    public void setupPersistence() {
        if(Repository.GITLET_SYSTEM.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        Repository.OBJECT_FOLDER.mkdirs();
        Repository.LOCAL_BRANCH.mkdirs();
        String SHA1 = Utils.sha1(new Date(0).toString(), "initial commit");
        final File INITIAL_COMMIT_FOLDER = Utils.join(Repository.OBJECT_FOLDER, SHA1.substring(0,2));
        INITIAL_COMMIT_FOLDER.mkdirs();
        Utils.writeObject(Utils.join(INITIAL_COMMIT_FOLDER, SHA1.substring(2)), this);
        Utils.writeContents(Repository.MASTER, SHA1);
        Utils.writeContents(Repository.HEAD, "ref: " + Repository.MASTER.getAbsolutePath());
        Utils.writeObject(Repository.STAGING_FILE, new StagedFile());
    }

    /** Get the HEAD commit. */
    public static Commit getHeadCommit() {
        String HEAD_FILE = Utils.readContentsAsString(Repository.HEAD);
        if(HEAD_FILE.startsWith("ref: ")) {
            HEAD_FILE = Utils.readContentsAsString(new File(HEAD_FILE.substring(5)));
        }
        return Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, HEAD_FILE.substring(0,2), HEAD_FILE.substring(2)), Commit.class);
    }

    /** Detect detached state (if HEAD is not pointing to a branch). */
    public static boolean isDetached() {
        return !Utils.readContentsAsString(Repository.HEAD).startsWith("ref: ");
    }

    /** Make a normal commit (not for merge), meanwhile update the branches.
     * NOTE: Though commits made in detached state may not be accessed again if no branch was made for these commits, they still persist. */
    public void makeCommit(String logMessage) {

        /** Clone the current HEAD commit to be the initial version of upcoming commit */
        Commit curCommit = getHeadCommit();
        String SHA1 = Utils.sha1((Object) Utils.serialize(curCommit));
        /** Update the current commit */
        curCommit.logMessage = logMessage;
        curCommit.timeStamp = new Date().toString();
        curCommit.Parent.clear();
        curCommit.Parent.add(SHA1);

        StagedFile staged = Utils.readObject(Repository.STAGING_FILE, StagedFile.class);
        /** if no change compare with HEAD commit, abort */
        if (staged.Addition.isEmpty() && staged.Removal.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        for(File f : staged.Addition.keySet()) {
            curCommit.Blobs.put(f, staged.Addition.get(f));
        }
        for(File f : staged.Removal) {
            curCommit.Blobs.remove(f);
        }
        /** clear the StagingArea */
        staged.Addition.clear();
        staged.Removal.clear();
        Utils.writeObject(Repository.STAGING_FILE, staged);
        /** Save the new commit object locally */
        String newSHA1 = Utils.sha1((Object) Utils.serialize(curCommit));
        File newCommitFolder = Utils.join(Repository.OBJECT_FOLDER, newSHA1.substring(0,2));
        newCommitFolder.mkdir();
        Utils.writeObject(Utils.join(newCommitFolder, newSHA1.substring(2)), curCommit);
        /** Update Pointers of HEAD commit or Branch according to whether in detached state */
        if(!isDetached()) {
            Utils.writeContents(new File(Utils.readContentsAsString(Repository.HEAD).substring(5)), newSHA1);
        } else {
            Utils.writeContents(Repository.HEAD, newSHA1);
        }

    }

    /** Print the commit history backwards along the HEAD commit.
     * If the HEAD commit is on a branch node (i.e. not in detached state), this command will print complete commit history of that branch. */
    public void log() {
        Commit cur = getHeadCommit();
        String SHA1 = Utils.sha1((Object) Utils.serialize(cur));
        while(cur != null) {
            System.out.println("===");
            System.out.println("commit " + SHA1);
            if(cur.Parent != null) {
                SHA1 = cur.Parent.getFirst();
                if(cur.Parent.size() > 1) {
                    System.out.println("Merge: " + SHA1.substring(0,7) + " " + cur.Parent.get(1).substring(0,7));
                }
                System.out.println("Date: " + cur.timeStamp);
                System.out.println(cur.logMessage);
                System.out.println("\n");
                cur = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, SHA1.substring(0,2), SHA1.substring(2)), Commit.class);
            } else {
                System.out.println("Date: " + cur.timeStamp);
                System.out.println(cur.logMessage);
                System.out.println("\n");
                cur = null;
            }
        }
    }

    /** Print information of all commits ever made, including commits on multiple branches and experimental commits (commits on unspecified branch), the order doesn't matter. */
    public void logGlobal() {
        for(File f : Repository.OBJECT_FOLDER.listFiles()) {
            for(String fileName : Utils.plainFilenamesIn(f)) {
                String SHA1 = f.getName() + fileName;
                File commitFile = Utils.join(Repository.OBJECT_FOLDER.getPath(), f.getName(), fileName);
                Serializable obj = Utils.readObject(commitFile,Serializable.class);
                if(obj instanceof Commit) {
                    Commit cur = (Commit) obj;
                    System.out.println("===");
                    System.out.println("commit " + SHA1);
                    if(cur.Parent.size() > 1) {
                        System.out.println("Merge: " + cur.Parent.getFirst().substring(0,7) + " " + cur.Parent.get(1).substring(0,7));
                    }
                    System.out.println("Date: " + cur.timeStamp);
                    System.out.println(cur.logMessage);
                    System.out.println("\n");
                }
            }
        }
    }

    /** Print all commit ids that have the given log message */
    public void find(String logMessage) {
        int cnt = 0;
        for(File f : Repository.OBJECT_FOLDER.listFiles()) {
            for(String fileName : Utils.plainFilenamesIn(f)) {
                String SHA1 = f.getName() + fileName;
                Commit cur = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, f.getName(), fileName), Commit.class);
                if(cur.logMessage.equals(logMessage)) {
                    cnt += 1;
                    System.out.println(SHA1);
                }
            }
        }
        if(cnt == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Create a new branch (a pointer) on HEAD commit, but not switch to it. */
    public void makeBranch(String Name) {
        File newBranch = Utils.join(Repository.LOCAL_BRANCH, Name);
        if(newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        String HEAD_SHA1 = Utils.readContentsAsString(Repository.HEAD);
        Utils.writeContents(newBranch, HEAD_SHA1);
    }

    /** Remove the branch with the given name (just the pointer, not all commits on it). */
    public void rmBranch(String Name) {
        File BRANCH_FILE = Utils.join(Repository.LOCAL_BRANCH, Name);
        if(!BRANCH_FILE.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if(!Commit.isDetached()) {
            File HEAD = new File(Utils.readContentsAsString(Repository.HEAD).substring(5));
            if(HEAD.equals(BRANCH_FILE)) {
                System.out.println("Cannot remove the current branch.");
                System.exit(0);
            }
        }
        Utils.restrictedDelete(BRANCH_FILE);
    }

    /** Checkouts for Gitlet.
     * 1. checkoutBranch: switch to a specific branch, put all and only its files to working directory.
     * 2. checkoutFile: takes the version of the file as it exists in the head commit and puts it in the working directory.
     * 3. checkoutCommitFile: takes the version of the file as it exists in the commit with the given id, and puts it in the working directory.
     * NOTE: Not like real Git, we don't have the "checkout -- [commit id]" command, which means we can't switch HEAD to an arbitrary commit other than a branch.
     * Thus, there's no "detached state" in Gitlet.
     *
     * The following implementation of checkouts has been taken "detached state" into account, which can be simplified. */

    public void checkoutBranch(String Name) {
        Watcher w = new Watcher();
        /** make sure that we won't lose any uncommited changes due to this switch operation. */
        isChangeCleared(w);
        /** make sure that the target branch exist and it's not the current branch */
        File BRANCH_FILE = Utils.join(Repository.LOCAL_BRANCH, Name);
        if(!BRANCH_FILE.exists()) {
           System.out.println("No such branch exists.");
           System.exit(0);
        }
        if(!isDetached() && Utils.readContentsAsString(Repository.HEAD).substring(5).equals(BRANCH_FILE.getPath())) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        /** update the HEAD file and the working directory */
        Utils.writeContents(Repository.HEAD, "ref: " + BRANCH_FILE.getAbsolutePath());
        updateCWDFiles(w);
    }

    /** The input should be an absolute pathname, which is initially a relative pathname as a command line argument, see "add". */
    public void checkoutFile(String PATHNAME) {
        Commit cur = getHeadCommit();
        File TARGET_FILE = new File(PATHNAME);
        if(!cur.Blobs.containsKey(TARGET_FILE)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String contentHash = cur.Blobs.get(TARGET_FILE);
        byte[] content = Utils.readContents(Utils.join(Repository.OBJECT_FOLDER, contentHash.substring(0,2), contentHash.substring(2)));
        Utils.writeContents(TARGET_FILE, (Object) content);
    }

    public void checkoutCommitFile(String SHA1, String PATHNAME) {
        File COMMIT_FILE = Utils.join(Repository.OBJECT_FOLDER, SHA1.substring(0,2), SHA1.substring(2));
        if(!COMMIT_FILE.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File TARGET_FILE = new File(PATHNAME).getAbsoluteFile();
        Commit TARGET_COMMIT = Utils.readObject(COMMIT_FILE, Commit.class);
        if(!TARGET_COMMIT.Blobs.containsKey(TARGET_FILE)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String contentHash = TARGET_COMMIT.Blobs.get(TARGET_FILE);
        byte[] content = Utils.readContents(Utils.join(Repository.OBJECT_FOLDER, contentHash.substring(0,2), contentHash.substring(2)));
        Utils.writeContents(TARGET_FILE, (Object) content);
    }

    /** Update files in CWD as the result of switching between commits. */
    private void updateCWDFiles(Watcher w) {
        Commit cur = getHeadCommit();
        for(File f : w.getCWDFiles()) {
            if(cur.Blobs.containsKey(f)) {
                String contentHash = cur.Blobs.get(f);
                byte[] content = Utils.readContents(Utils.join(Repository.OBJECT_FOLDER, contentHash.substring(0,2), contentHash.substring(2)));
                Utils.writeContents(f, (Object) content);
            } else {
                Utils.restrictedDelete(f);
            }
        }
    }

    /** Exam whether current uncommited changes exist.
     * If so, abort the program. */
     private void isChangeCleared(Watcher w) {
         if(w.getUntrackedFile() || w.getChangedFile() || !w.isStagedEmpty()) {
             System.out.println("There is an untracked file in the way; delete it or add and commit it first.");
             System.exit(0);
         }
     }

    /** Switch HEAD to a specific commit, put all and only its contents to CWD.
     * Do so by moving the current branch head back to this commit to align with Gitlet's feature that HEAD must also be a branch, which implicitly moves the HEAD pointer. */
    public void reset(String SHA1) {
        Watcher w = new Watcher();
        /** make sure that we won't lose any uncommited changes due to this switch operation. */
        isChangeCleared(w);
        /** make sure that the target commit exist */
        File COMMIT_FILE = Utils.join(Repository.OBJECT_FOLDER, SHA1.substring(0,2), SHA1.substring(2));
        if(!COMMIT_FILE.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        /** update the current branch's SHA1, and update the working directory if necessary. */
        String HEAD_SHA1 = Utils.readContentsAsString(Repository.HEAD).substring(5);
        if(!HEAD_SHA1.equals(SHA1)) {
            Utils.writeContents(new File(Utils.readContentsAsString(Repository.HEAD).substring(5)), SHA1);
            updateCWDFiles(w);
        }
    }
}

