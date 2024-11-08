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

    private String timeStamp;
    private String logMessage;
    private String[] Blobs;
    private String Parent;

    /** Construct the initial commit object*/
    public Commit() {
        logMessage = "initial commit";
        timeStamp = new Date(0).toString();
    }

    /** Construct the commit object based on specific args*/
    public void Commit(String logMessage, String timeStamp) {}

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */

    /** Calculate SHA-1 for a normal Commit object. */
    public String hash() {
        StringBuilder cString = new StringBuilder();
        cString.append(timeStamp);
        cString.append(logMessage);
        cString.append(Parent);
        for(String s : Blobs) {
            cString.append(s);
        }
        return Utils.sha1(cString.toString());
    }

    /** Write the Commit object to current gitlet system, meanwhile update the branches. */
    public void makeCommit() {
        if(!Repository.INITIAL_COMMIT.exists()) {
            Utils.writeContents(Repository.INITIAL_COMMIT, new Date(0).toString(), "initial commit");
            Utils.writeContents(Repository.MASTER, Utils.sha1(new Date(0).toString(), "initial commit"));
            Utils.writeContents(Repository.HEAD, Utils.sha1(new Date(0).toString(), "initial commit"));
        }
        else {
            File COMMIT = Utils.join(Repository.COMMIT_FOLDER, hash());
            Utils.writeContents(COMMIT, timeStamp, logMessage, Blobs, Parent);
            Utils.restrictedDelete(Repository.MASTER);
            Utils.writeContents(Repository.MASTER, hash());
            if(Utils.readContentsAsString(Repository.HEAD).equals(Utils.readContentsAsString(Repository.MASTER))) {
                Utils.restrictedDelete(Repository.HEAD);
                Utils.writeContents(Repository.HEAD, hash());
            }
        }
    }
}

