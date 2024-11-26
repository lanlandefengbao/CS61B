package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    String timeStamp;
    String logMessage;
    Map<File, String> Blobs = new HashMap<>();
    List<String> Parent = new ArrayList<>();

    /** Construct the initial commit object*/
    public Commit() {
        logMessage = "initial commit";
        timeStamp = new Date(0).toString();
    }

    /** Construct the commit object based on specific args*/
    public Commit(String logMessage, String timeStamp) {}

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */

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
        final File INITIAL_COMMIT = Utils.join(Repository.BRANCH_FOLDER, SHA1.substring(0,2), SHA1);
        INITIAL_COMMIT.mkdirs();
        Utils.writeObject(INITIAL_COMMIT, this);
        Utils.writeContents(Repository.MASTER, hash());
        Utils.writeContents(Repository.HEAD, "ref: " + Repository.MASTER.getAbsolutePath());
    }

    /** Make a normal commit (not for merge), meanwhile update the branches.
     * NOTE: Though commits made in detached state may not be accessed again if no branch was made for these commits, they still persist. */
    public void makeCommit(String logMessage) {
        Commit curCommit = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, Utils.readContentsAsString(Repository.HEAD)), Commit.class);

        curCommit.logMessage = logMessage;
        curCommit.timeStamp = new Date().toString();
        curCommit.Parent.addLast(Utils.readContentsAsString(Repository.HEAD));

        StagedFile staged = Utils.readObject(Repository.STAGING_FILE, StagedFile.class);
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
        Utils.writeContents(Repository.STAGING_FILE, "");
        //TODO: save the commit object locally, and update the current branch or create a new branch, and update HEAD commit

    }

    /** Print the commit history backwards along the HEAD commit.
     * If the HEAD commit is on a branch node (i.e. not in detached state), this command will print complete commit history of that branch. */
    public void log() {
        String SHA1 = Utils.readContentsAsString(Repository.HEAD);
        if(SHA1.startsWith("ref: ")) {
            File HEAD_FILE = new File(Utils.readContentsAsString(new File(SHA1.substring(5))));
            SHA1 = Utils.readContentsAsString(HEAD_FILE);
        }
        Commit cur = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, SHA1.substring(0,2), SHA1), Commit.class);
        while(cur != null) {
            System.out.println("===");
            System.out.println("commit " + SHA1);
            if(cur.Parent != null) {
                SHA1 = cur.Parent.getFirst();
                if(cur.Parent.size() > 1) {
                    System.out.println("Merge: " + SHA1.substring(0,7) + " " + cur.Parent.get(1).substring(0,7));
                }
                cur = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, SHA1.substring(0,2), SHA1), Commit.class);
                System.out.println("Date: " + cur.timeStamp);
                System.out.println(cur.logMessage);
                System.out.println("\n");
            } else {
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

    }
}

