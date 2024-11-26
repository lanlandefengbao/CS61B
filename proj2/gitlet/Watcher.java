package gitlet;

import java.io.File;
import java.util.*;


/** When executing Gitlet commands, the underlying system should walk through the historical version of files and interact with files in CWD */

public class Watcher {
    /**
     * files in CWD that neither _tracked_ in current commit nor staged for addition.
     */
    private Map<File, String> untracked1;
    /**
     * files that have been staged for removal, but then re-created in CWD
     */
    private Set<File> untracked21;
    private Map<File, String> untracked22;
    /**
     * Files tracked in current commit, with content changed in CWD but not staged for addition;
     */
    private Map<File, String> changed1;
    /**
     * Files staged for addition, changed in CWD, but (this change) not staged.
     */
    private Set<File> changed21;
    private Map<File, String> changed22;
    /**
     * Files staged for addition, but deleted in the working directory.
     */
    private Set<File> changed31;
    private Set<File> changed32;
    /**
     * Files tracked in current commit, deleted in CWD, but not staged for removal.
     */
    private Set<File> changed4;

    private final StagedFile staged;
    private final List<File> cwdFiles;
    private final Map<File, String> commitedFile = Utils.readObject(Utils.join(Repository.OBJECT_FOLDER, Utils.readContentsAsString(Repository.HEAD)), Commit.class).Blobs;

    private final static File CWD = new File(System.getProperty("user.dir"));

    public Watcher() {
        staged = Utils.readObject(Repository.STAGING_FILE, StagedFile.class);
        cwdFiles = getAbsolutePaths(Repository.PROJECT_FOLDER, new ArrayList<>());
    }

    private List<File> getAbsolutePaths(File CURRENT_PATH, List<File> files) {
        if (CURRENT_PATH.isFile()) {
            files.add(CURRENT_PATH);
        } else {
            for (File f : CURRENT_PATH.listFiles()) {
                getAbsolutePaths(f, files);
            }
        }
        return files;
    }

    /**
     * Tracing untracked files (untracked1, untracked2 of this object)
     */
    public void getUntrackedFile() {
        for (File f : cwdFiles) {
            if (!staged.Addition.containsKey(f)) {
                String contentHash = Utils.sha1((Object) Utils.readContents(f));
                if (!staged.Removal.contains(f)) {
                    untracked1.put(f, contentHash);
                }
                if (contentHash.equals(staged.Addition.get(f))) {
                    untracked21.add(f);
                } else {
                    untracked22.put(f, contentHash);
                }
            }
        }
    }

    /**
     * Tracing changed but not staged files (changed1-4)
     */
    public void getChangedFile() {
        for (File f : staged.Addition.keySet()) {
            if(commitedFile.containsKey(f)) {
                if(!cwdFiles.contains(f)) {
                    changed31.add(f);
                } else {
                    String contentHash = Utils.sha1((Object) Utils.readContents(f));
                    if(contentHash.equals(commitedFile.get(f))) {
                        changed21.add(f);
                    } else {
                        changed22.put(f, contentHash);
                    }
                }
            }
            else {
                if(!cwdFiles.contains(f)) {
                    changed32.add(f);
                } else {
                    String contentHash = Utils.sha1((Object) Utils.readContents(f));
                    if(!contentHash.equals(commitedFile.get(f))) {
                        changed22.put(f, contentHash);
                    }
                }
            }
        }
        for(File f : commitedFile.keySet()) {
            if(!cwdFiles.contains(f)) {
                if(!staged.Removal.contains(f)) {
                    changed4.add(f);
                }
            }
            else {
                String contentHash = Utils.sha1((Object) Utils.readContents(f));
                if(!contentHash.equals(commitedFile.get(f)) && !staged.Addition.containsKey(f)) {
                    changed1.put(f, contentHash);
                }
            }
        }

    }

    /** Take in an absolute path, returns the relative path based on CWD. */
    private String getRelativePath(File f) {
        String absolutePath = f.getAbsolutePath();
        String cwdPath = CWD.getAbsolutePath();
        return absolutePath.substring(cwdPath.length() + 1);
    }

    /** Displays Branches(with current branch marked by *), Staged files, Removed files, Changes not staged, and Untracked files. */
    public void getStatus() {
        getUntrackedFile();
        getChangedFile();
        System.out.println("=== Branches ===");
        //TODO: print branches as required
        System.out.println("=== Staged Files ===");
        for(File f : staged.Addition.keySet()) {
            System.out.println(getRelativePath(f));
        }
        System.out.println("\n");
        System.out.println("=== Removed Files ===");
        for(File f : staged.Removal) {
            System.out.println(getRelativePath(f));
        }
        System.out.println("\n");
        System.out.println("=== Modifications Not Staged For Commit ===");
        for(File f : changed1.keySet()) {
            System.out.println(getRelativePath(f) + " (modified)");
        }
        for(File f : changed21) {
            System.out.println(getRelativePath(f) + " (modified)");
        }
        for(File f : changed22.keySet()) {
            System.out.println(getRelativePath(f) + " (modified)");
        }
        for(File f : changed31) {
            System.out.println(getRelativePath(f) + " (deleted)");
        }
        for(File f : changed32) {
            System.out.println(getRelativePath(f) + " (deleted)");
        }
        for(File f : changed4) {
            System.out.println(getRelativePath(f) + " (deleted)");
        }
        System.out.println("\n");
        System.out.println("=== Untracked Files ===");
        for (File f : untracked1.keySet()) {
            System.out.println(getRelativePath(f));
        }
        for (File f : untracked21) {
            System.out.println(getRelativePath(f));
        }
        for (File f : untracked22.keySet()) {
            System.out.println(getRelativePath(f));
        }
    }

    /** Stage one file to StagingArea's Addition filed, and unstage it from the Removal field
     * And we only pass in the file's relative path of CWD */
    public void addOne(File f) {
        if (!cwdFiles.contains(f)) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String contentHash = Utils.sha1((Object) Utils.readContents(f));
        /** newly add (including recreation) */
        if (!staged.Addition.containsKey(f)) {
            if (staged.Removal.contains(f)) {
                staged.Removal.remove(f);
                if (!contentHash.equals(commitedFile.get(f))) {
                    staged.Addition.put(f, contentHash);
                }
            } else {
                staged.Addition.put(f, contentHash);
            }
        }
        /** modification */
        else {
            if (!staged.Addition.get(f).equals(contentHash)) {
                staged.Addition.replace(f, contentHash);
            }
        }
        /** store current blob into .gitlet/objects folder */
        Blob blob = new Blob(contentHash, Utils.readContents(f));
        File thisBlob = Utils.join(Repository.OBJECT_FOLDER, contentHash);
        if(!thisBlob.exists()) {
            Utils.writeObject(thisBlob, blob);
        }

        Utils.writeObject(Repository.STAGING_FILE, staged);
    }

    public void addAll() {

    }

    /** Unstage the file if it is currently staged for addition, also add it for removal if it is tracked (so the "status" won't get confused)
     * And we only pass in the file's relative path of CWD */
    public void removeOne(File f) {
        if(staged.Addition.containsKey(f)) {
            staged.Addition.remove(f);
            if(commitedFile.containsKey(f)) {
                staged.Removal.add(f);
                cwdFiles.remove(f);
            }
        } else {
            if(commitedFile.containsKey(f)) {
                staged.Removal.add(f);
                cwdFiles.remove(f);
            } else {
                System.out.println("No reason to remove this file. ");
            }
        }

        Utils.writeObject(Repository.STAGING_FILE, staged);
    }

}

