package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.sql.Timestamp;
import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private String time;
    private String logMessage;
    private String Blobs;
    private String Parent;

    /** Construct the initial commit object*/
    public Commit() {
        logMessage = "initial commit";
        time = new Date(0).toString();
    }

    /** Construct the commit object based on specific args*/
    public void Commit(String logMessage, String timeStamp) {}

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */

    /** Calculate SHA-1 based on object's content. For initial Commit. */
    public String hash0() {
        return Utils.sha1(time, logMessage);
    }

    /** Write the initial Commit object to a specific file, meanwhile update the branches. */
    public void setupPersistence(File SYSTEM_FOLDER) {
        File COMMIT = Utils.join(SYSTEM_FOLDER, "objects", hash0());
        COMMIT.getParentFile().mkdirs();
        Utils.writeContents(COMMIT, time, logMessage);
        File MASTER = Utils.join(SYSTEM_FOLDER, "refs", "MASTER");
        MASTER.getParentFile().mkdirs();
        Utils.writeContents(MASTER, hash0());
        File HEAD = Utils.join(SYSTEM_FOLDER, "refs", "HEAD");
        HEAD.getParentFile().mkdirs();
        Utils.writeContents(HEAD, hash0());
    }

    /** Calculate SHA-1 based on object's content. For normal Commit. */
    public String hash1() {
        return Utils.sha1(time, logMessage, Blobs, Parent);
    }

    /** Write a normal Commit object to a specific file, meanwhile update the branches. */
    public void setupPersistence(File SYSTEM_FOLDER, boolean match) {
        File COMMIT = Utils.join(SYSTEM_FOLDER, "objects", hash1());
        Utils.writeContents(COMMIT, time, logMessage, Blobs, Parent);
        File MASTER = Utils.join(SYSTEM_FOLDER, "refs", "MASTER");
        Utils.restrictedDelete(MASTER);
        Utils.writeContents(MASTER, hash1());
        // if HEAD matches MASTER, update HEAD as well.
        File HEAD = Utils.join(SYSTEM_FOLDER, "refs", "HEAD");
        if(match) {
            Utils.restrictedDelete(HEAD);
            Utils.writeContents(HEAD, hash1());
        }
    }
}

