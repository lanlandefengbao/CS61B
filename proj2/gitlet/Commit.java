package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/** Represents a gitlet commit object. Also, it contains all the commands that are commit-related.
 *
 *  @author B Li
 */
public class Commit implements Serializable, Dumpable {

    @Override
    public void dump() {
        System.out.printf("Time Stamp: %s%nLog Message: %s%nBlobs: %s%nParent: %s%n", timeStamp, logMessage, Blobs, Parent);
    }

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

    /** Construct a normal commit object based on current commit */
    public Commit(String Message) {
        logMessage = Message;
        timeStamp = new Date().toString();
        Commit cur = getHeadCommit();
        Parent.add(cur.hash());
        Blobs = cur.Blobs;
    }

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
//        Create the initial commit
        Commit INITIAL_COMMIT = new Commit();
        String SHA1 = INITIAL_COMMIT.hash();
//        /** Store the initial commit */
        final File INITIAL_COMMIT_FOLDER = Utils.join(Repository.OBJECT_FOLDER, SHA1.substring(0,2));
        INITIAL_COMMIT_FOLDER.mkdirs();
        Utils.writeObject(Utils.join(INITIAL_COMMIT_FOLDER, SHA1.substring(2)), this);
//        /** Set up the HEAD pointer */
        Utils.writeContents(Repository.MASTER, SHA1);
        Utils.writeContents(Repository.HEAD, "ref: " + Repository.MASTER.getAbsolutePath());
        Utils.writeObject(Repository.STAGING_FILE, new StagedFile());
    }

    /** Get the HEAD commit. */
    public static Commit getHeadCommit() {
        String HEAD_SHA1 = Utils.readContentsAsString(Repository.HEAD);
        if(HEAD_SHA1.startsWith("ref: ")) {
            HEAD_SHA1 = Utils.readContentsAsString(new File(HEAD_SHA1.substring(5)));
        }
        return Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, HEAD_SHA1.substring(0,2), HEAD_SHA1.substring(2)), Commit.class);
    }

    /** Get parents of the current commit. */
    public Commit[] getParents() {
        Commit[] res = new Commit[Parent.size()];
        for(int i = 0; i < Parent.size(); i++) {
            res[i] = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, Parent.get(i).substring(0,2), Parent.get(i).substring(2)), Commit.class);
        }
        return res;
    }

    /** Detect detached state (if HEAD is not pointing to a branch). */
    public static boolean isDetached() {
        return !Utils.readContentsAsString(Repository.HEAD).startsWith("ref: ");
    }

    /** Make a normal/merged commit, meanwhile update the branches.
     * The only thing different between a normal commit and a merged commit is that the latter has multiple parents.
     * If the argument "GIVEN_COMMIT" IS NOT NULL, we need to add the second parent to the new commit (in Gitlet, we only have at most two parents for a commit).
     * NOTE: Though commits made in detached state may not be accessed again if no branch was made for these commits, they still persist. */
    public void makeCommit(String logMessage, Commit GIVEN_COMMIT) {

        // Clone the current HEAD commit to be the initial version of upcoming commit
        Commit newCommit = new Commit(logMessage);
        // If it's a merged commit, add the second parent (in Gitlet, we only have 2 parents for a merged commit)
        if(GIVEN_COMMIT != null) {
            newCommit.Parent.addLast(GIVEN_COMMIT.hash());
        }
        StagedFile staged = Utils.readObject(Repository.STAGING_FILE, StagedFile.class);
        // if no change compare with HEAD commit, abort
        if (staged.Addition.isEmpty() && staged.Removal.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        for(File f : staged.Addition.keySet()) {
            newCommit.Blobs.put(f, staged.Addition.get(f));
        }
        for(File f : staged.Removal) {
            newCommit.Blobs.remove(f);
        }
        // clear the StagingArea
        staged.Addition.clear();
        staged.Removal.clear();
        Utils.writeObject(Repository.STAGING_FILE, staged);
        // Save the new commit object locally
        String newSHA1 = Utils.sha1((Object) Utils.serialize(newCommit));
        File COMMIT_FOLDER = Utils.join(Repository.OBJECT_FOLDER, newSHA1.substring(0,2));
        COMMIT_FOLDER.mkdir();
        Utils.writeObject(Utils.join(COMMIT_FOLDER, newSHA1.substring(2)), newCommit);
        // Update Pointers of HEAD commit or Branch according to whether in detached state
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
        String SHA1 = cur.hash();
        while(cur != null) {
            System.out.println("===");
            System.out.println("commit " + SHA1);
            if(!cur.Parent.isEmpty()) {
                if(cur.Parent.size() > 1) {
                    System.out.println("Merge: " + cur.Parent.get(0).substring(0,7) + " " + cur.Parent.get(1).substring(0,7));
                }
                System.out.println("Date: " + cur.timeStamp);
                System.out.println(cur.logMessage);
                System.out.println("\n");
                SHA1 = cur.Parent.get(0);
                File COMMIT_FILE = Utils.join(Repository.OBJECT_FOLDER, SHA1.substring(0,2), SHA1.substring(2));
                cur = Utils.readObject(COMMIT_FILE, Commit.class);
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
                // a file in .gitlet/object could either be a commit object or a blob object
                Serializable cur = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, f.getName(), fileName), Serializable.class);
                if(cur instanceof Commit) {
                    if(((Commit) cur).logMessage.equals(logMessage)) {
                        cnt += 1;
                        System.out.println(SHA1);
                    }
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
        Commit HEAD = getHeadCommit();
        String HEAD_SHA1 = HEAD.hash();
        Utils.writeContents(newBranch, HEAD_SHA1);
    }

    /** Remove the branch with the given name (just the pointer, not all commits on it). */
    public void rmBranch(String Name) {
        File BRANCH_FILE = Utils.join(Repository.LOCAL_BRANCH, Name);
        List<String> branches = Utils.plainFilenamesIn(Repository.LOCAL_BRANCH);
        boolean exist = false;
        for(String b : branches) {
            if(b.equals(Name)) {
                exist = true;
                break;
            }
        }
        if(!exist) {
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
        BRANCH_FILE.delete();
    }

    /** Checkouts for Gitlet.
     * 1. checkoutBranch: switch to a specific branch, put all and only its files to working directory.
     * 2. checkoutFile: takes the version of the file as it exists in the head commit and puts it in the working directory.
     * 3. checkoutCommitFile: takes the version of the file as it exists in the commit with the given id, and puts it in the working directory.
     * NOTE: Not like real Git, we don't have the "checkout -- [commit id]" command, which means we can't switch HEAD to an arbitrary commit other than a branch.
     * Thus, there's no "detached state" in Gitlet.

     * The following implementation of checkouts has been taken "detached state" into account, which can be simplified. */

    public void checkoutBranch(String BRANCH_NAME) {
        Watcher w = new Watcher();
//        /** make sure that we won't lose any uncommited changes due to this switch operation. */
        isChangeCleared(w);
//        /** make sure that the target branch exist and it's not the current branch */
        File BRANCH_FILE = Utils.join(Repository.LOCAL_BRANCH, BRANCH_NAME);
        if(!BRANCH_FILE.exists()) {
           System.out.println("No such branch exists.");
           System.exit(0);
        }
        if(!isDetached() && Utils.readContentsAsString(Repository.HEAD).substring(5).equals(BRANCH_FILE.getPath())) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
//        /** update the HEAD file and the working directory */
        Commit cur = getHeadCommit();
        String CHECKOUT_ID = Utils.readContentsAsString(BRANCH_FILE);
        Commit CHECKOUT_COMMIT = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, CHECKOUT_ID.substring(0,2), CHECKOUT_ID.substring(2)), Commit.class);
        updateCWDFiles(cur, CHECKOUT_COMMIT);
        Utils.writeContents(Repository.HEAD, "ref: " + BRANCH_FILE.getAbsolutePath());
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
        File BLOB_FILE = Utils.join(Repository.OBJECT_FOLDER, contentHash.substring(0, 2), contentHash.substring(2));
        Blob blob = Utils.readObject(BLOB_FILE, Blob.class);
        Utils.writeObject(TARGET_FILE, blob);
        // Unstage the file if it's staged
        Watcher w = new Watcher();
        w.getStaged().Addition.remove(TARGET_FILE);
        w.getStaged().Removal.remove(TARGET_FILE);
        Utils.writeObject(Repository.STAGING_FILE, w.getStaged());
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
        Blob blob = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, contentHash.substring(0,2), contentHash.substring(2)), Blob.class);
        Utils.writeObject(TARGET_FILE, blob);
        // Unstage the file if it's staged
        Watcher w = new Watcher();
        w.getStaged().Addition.remove(TARGET_FILE);
        w.getStaged().Removal.remove(TARGET_FILE);
        Utils.writeObject(Repository.STAGING_FILE, w.getStaged());
    }

    /** Update files in CWD as the result of switching between commits.
     * Here we suppose CWDFiles are identical with the current commit's blobs. */
    private void updateCWDFiles(Commit CURRENT_COMMIT, Commit CHECKOUT_COMMIT) {
        for(File f : CHECKOUT_COMMIT.Blobs.keySet()) {
            if(CURRENT_COMMIT.Blobs.containsKey(f)) {
                if(!CURRENT_COMMIT.Blobs.get(f).equals(CHECKOUT_COMMIT.Blobs.get(f))) {
                    Blob blob = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, CHECKOUT_COMMIT.Blobs.get(f).substring(0,2), CHECKOUT_COMMIT.Blobs.get(f).substring(2)), Blob.class);
                    Utils.writeObject(f, blob);
                }
            } else {
                Blob blob = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, CHECKOUT_COMMIT.Blobs.get(f).substring(0,2), CHECKOUT_COMMIT.Blobs.get(f).substring(2)), Blob.class);
                Utils.writeObject(f, blob);
            }
        }
        for(File f : CURRENT_COMMIT.Blobs.keySet()) {
            if(!CHECKOUT_COMMIT.Blobs.containsKey(f)) {
                f.delete();
            }
        }

    }

    /** Exam whether untracked files exist in current commit. By 'untracked files', we mean any file that is modified/deleted/added and haven't being commited.
     * If so, we shall lose changes to the current branch due to "checkout branch", so abort the program to prevent this. */
     private void isChangeCleared(Watcher w) {
         if(w.getUntrackedFile()) {
             System.out.println("There is an untracked file in the way; delete it or add and commit it first.");
             System.exit(0);
         }
         if(w.getChangedFile()) {
             System.out.println("You have unstaged changes; undo or stage and commit it.");
             System.exit(0);
         }
         if(!w.isStagedEmpty()) {
             System.out.println("You have uncommitted changes.");
             System.exit(0);
         }
     }

    /** Switch HEAD to a specific commit, put all and only its contents to CWD.
     * Also moving the current branch head back to this commit to align with Gitlet's feature that NO DETACHED STATE ALLOWED. */
    public void reset(String SHA1) {
        Watcher w = new Watcher();
//        /** make sure that we won't lose any uncommited changes due to this switch operation. */
        isChangeCleared(w);
//        /** make sure that the target commit exist */
        File COMMIT_FILE = Utils.join(Repository.OBJECT_FOLDER, SHA1.substring(0,2), SHA1.substring(2));
        if(!COMMIT_FILE.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
//      update the working directory and move the current branch head back to this commit
        Commit cur = getHeadCommit();
        Commit target = Utils.readObject(COMMIT_FILE, Commit.class);
        updateCWDFiles(cur, target);
        File CURRENT_BRANCH = new File(Utils.readContentsAsString(Repository.HEAD).substring(5));
        Utils.writeContents(CURRENT_BRANCH, SHA1);
    }

    /** Merge the given branch into the current branch
     * The major rule is that: if a file is modified(deleted or changed in content) since split point in only one branch, confirm this modification;
     * if it's modified in both branch differently, then it's a CONFLICT where Gitlet can't automatically decide which version to use. */
    public void merge(String branchName) {
        // make sure we won't lose any uncommited changes due to this merge operation
        Watcher w = new Watcher();
        isChangeCleared(w);
        // make sure the target branch exists
        File BRANCH_FILE = Utils.join(Repository.LOCAL_BRANCH, branchName);
        if(!BRANCH_FILE.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String COMMIT_ID = Utils.readContentsAsString(BRANCH_FILE);
        Commit target = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, COMMIT_ID.substring(0,2), COMMIT_ID.substring(2)), Commit.class);
        // make sure not to merge a branch with itself
        Commit cur = getHeadCommit();
        if(cur.hash().equals(target.hash())) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        // Merge in various cases
        Commit sp = splitPoint(cur, target);
        List<String> messageBox = new ArrayList<>(); // stores the messages that will be printed in terminal, for now we only have one potential message: "Encountered a merge conflict.\n"
        if(cur.hash().equals(sp.hash())) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        if(target.hash().equals(sp.hash())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else {
            for (File f : sp.Blobs.keySet()) {
                if(!cur.Blobs.containsKey(f) && !target.Blobs.containsKey(f)) {
                    continue; //3
                }
                else if(!cur.Blobs.containsKey(f) && target.Blobs.containsKey(f)) {
                    if(target.Blobs.get(f).equals(sp.Blobs.get(f))) {
                        continue; //7
                    } else {
                        conflict(cur, target, f, w, messageBox); //8.2
                    }
                }
                else if(cur.Blobs.containsKey(f) && !target.Blobs.containsKey(f)) {
                    if(cur.Blobs.get(f).equals(sp.Blobs.get(f))) {
                        w.removeOne(f); //6
                    } else {
                        conflict(cur, target, f, w, messageBox); //8.2
                    }
                }
                else {
                    if(cur.Blobs.get(f).equals(target.Blobs.get(f))) {
                        continue; //3
                    }
                    else {
                        if(cur.Blobs.get(f).equals(sp.Blobs.get(f))) {
                            checkoutCommitFile(target.hash(), f.getPath());
                            w.addOne(f); //1
                        }
                        else if(target.Blobs.get(f).equals(sp.Blobs.get(f))) {
                            continue; //2
                        }
                        else {
                            conflict(cur, target, f, w, messageBox); //8.1
                        }
                    }
                }
            }
            for(File f : cur.Blobs.keySet()) {
                if(!sp.Blobs.containsKey(f)) {
                    if(!target.Blobs.containsKey(f)) {
                        continue; //4
                    } else {
                        if(target.Blobs.get(f).equals(cur.Blobs.get(f))) {
                            continue; //3
                        } else {
                            conflict(cur, target, f, w, messageBox); //8.3
                        }
                    }
                }
            }
            for(File f : target.Blobs.keySet()) {
                if(!sp.Blobs.containsKey(f)) {
                    if(!cur.Blobs.containsKey(f)) {
                        checkoutCommitFile(target.hash(), f.getPath());
                        w.addOne(f); //5
                    } else {
                        if(cur.Blobs.get(f).equals(target.Blobs.get(f))) {
                            continue; //3
                        } else {
                            conflict(cur, target, f, w, messageBox); //8.3
                        }
                    }
                }
            }
        }
        makeCommit("Merged " + branchName + " into " + Utils.readContentsAsString(Repository.HEAD).substring(5 + Repository.LOCAL_BRANCH.getAbsolutePath().length() + 1) + ".\n", target);
        if(!messageBox.isEmpty()) {
            System.out.println(messageBox.get(0));
        }
    }

    /** Find the split point of current branch and given branch. (Graph traverse) */
    private Commit splitPoint(Commit current, Commit target) {
        CommitTree currentTree = new CommitTree(current);
        CommitTree targetTree = new CommitTree(target);
        HashMap<String, Integer> currentDistance = currentTree.getDistanceTable();
        HashMap<String, Integer> targetDistance = targetTree.getDistanceTable();
        Commit res = null;
        int minDistance = Integer.MAX_VALUE;
        for(String SHA1 : currentDistance.keySet()) {
            if(targetDistance.containsKey(SHA1)) {
                int minDistanceNew = Math.min(currentDistance.get(SHA1), minDistance);
                if(minDistanceNew != minDistance) {
                    res = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, SHA1.substring(0,2), SHA1.substring(2)), Commit.class);
                    minDistance = minDistanceNew;
                }
            }
        }
        return res;
    }

    /** Generate the conflicted file (without solving it) and put it in CWD */
    private void conflict(Commit CURRENT_COMMIT, Commit TARGET_COMMIT, File f, Watcher w, List<String> messageBox) {
        // Get the specified file from the target commit
        byte[] contentCurrent;
        if(CURRENT_COMMIT.Blobs.get(f) == null) {
            contentCurrent = new byte[]{};
        } else {
            contentCurrent = Utils.readContents(Utils.join(Repository.OBJECT_FOLDER,CURRENT_COMMIT.Blobs.get(f).substring(0,2), CURRENT_COMMIT.Blobs.get(f).substring(2)));
        }
        // Get the specified file from the target commit
        byte[] contentTarget;
        if(TARGET_COMMIT.Blobs.get(f) == null) {
            contentTarget = new byte[]{};
        } else {
            contentTarget = Utils.readContents(Utils.join(Repository.OBJECT_FOLDER,TARGET_COMMIT.Blobs.get(f).substring(0,2), TARGET_COMMIT.Blobs.get(f).substring(2)));
        }
        // Generate the conflicted file
        String header = "<<<<<<< HEAD\n";
        String splitter = "=======\n";
        String footer = ">>>>>>>\n";
        Utils.writeContents(f, header, contentCurrent, splitter, contentTarget, footer);
        w.addOne(f);
        // Enrich the messageBox
        if(messageBox.isEmpty()) {
            messageBox.add("Encountered a merge conflict.\n");
        }
    }

}

