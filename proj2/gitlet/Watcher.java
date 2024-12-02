package gitlet;

import java.io.File;
import java.util.*;


/** The specific class of objects through which we can capture project files of different states, including "staged/removed", "untracked" and "changed but not staged", see DESIGN DOCUMENT for details. */

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
    private final Map<File, String> commitedFile = Commit.getHeadCommit().Blobs;

    private final static File CWD = new File(System.getProperty("user.dir"));

    public Watcher() {
        staged = Utils.readObject(Repository.STAGING_FILE, StagedFile.class);
        cwdFiles = getAbsolutePaths(Repository.PROJECT_FOLDER, new ArrayList<>());
    }

    private List<File> getAbsolutePaths(File CURRENT_PATH, List<File> files) {
        if (CURRENT_PATH.isFile()) {
            files.add(CURRENT_PATH);
        }
        else {
            for (File f : CURRENT_PATH.listFiles()) {
                if(f.getName().equals(".gitlet")) {
                    continue;
                }
                getAbsolutePaths(f, files);
            }
        }
        return files;
    }

    /**
     * Tracing untracked files (untracked1, untracked2 of this object)
     */
    public Boolean getUntrackedFile() {
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
        return !untracked1.isEmpty() && !untracked21.isEmpty() && !untracked22.isEmpty();
    }

    /**
     * Tracing changed but not staged files (changed1-4)
     */
    public Boolean getChangedFile() {
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
        return !changed1.isEmpty() && !changed21.isEmpty() && !changed22.isEmpty() && !changed31.isEmpty() && !changed32.isEmpty() && !changed4.isEmpty();
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
        if(Commit.isDetached()) {
            String SHA1 = Utils.readContentsAsString(Repository.HEAD);
            System.out.println("*" + "(HEAD detached at " + SHA1.substring(0,7) + ")");
            for(File f : Repository.LOCAL_BRANCH.listFiles()) {
                System.out.println(f.getName());
            }
        } else {
            File HEAD = new File(Utils.readContentsAsString(Repository.HEAD).substring(5));
            for(File f : Repository.LOCAL_BRANCH.listFiles()) {
                if(f.equals(HEAD)) {
                    System.out.println("*" + f.getName());
                } else {
                    System.out.println(f.getName());
                }
            }
        }
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
     * And this method only takes in the file's ABSOLUTE path */
    public void addOne(File f) {
        if (!cwdFiles.contains(f)) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String contentHash = Utils.sha1((Object) Utils.readContents(f));
        /** newly added (including recreation) & modified */
        if (!staged.Addition.containsKey(f)) {
            if (staged.Removal.contains(f)) {
                staged.Removal.remove(f);
                if (!contentHash.equals(commitedFile.get(f))) {
                    staged.Addition.put(f, contentHash);
                }
            } else {
                if(!commitedFile.containsKey(f)) {
                    staged.Addition.put(f, contentHash);
                } else {
                    if(!contentHash.equals(commitedFile.get(f))) {
                        staged.Addition.put(f, contentHash);
                    }
                }
            }
        }
        else {
            if (!staged.Addition.get(f).equals(contentHash)) {
                staged.Addition.replace(f, contentHash);
            }
        }
        /** store the staged file with new version of contents into .gitlet/objects folder, so we should have the right contents when commiting even though the file was deleted/modified in CWD. */
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

    public List<File> getCWDFiles() {
        return cwdFiles;
    }

    public boolean isStagedEmpty() {
        return staged.Addition.isEmpty() && staged.Removal.isEmpty();
    }
}

