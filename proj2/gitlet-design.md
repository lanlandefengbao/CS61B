# Gitlet Design Document

**Name**: B Li

## Classes and Data Structures

### Main
This is the entry point of _Gitlet_. 
It has a __Main__ method which takes arguments from the command line and 
then executes the corresponding logic that is specified as methods in __GitletRepository__.
And maybe some helper methods for logic delegation.
#### Fields

None

### GitletRepository

#### Fields

1. #### StagingArea
   Storing the current temporal `Blob` objects that are _staged_ for _commit_.
   It should have two __subareas__: `Addition` and `Removal`.
   `Addition` tracks ___files staged for addition___ (newly added files and files with modified content), `Removal` tracks newly removed file.
   These `Blob` objects should be wiped out immediately after _commiting_.
2. #### Commits
   Contains all historical `Commit` objects, each version involves `metadata` (_log message_, _timestamp_), `UID of Parent Commit` and `UID of Blobs objects`.
3. #### Blobs
   Contains all historical `Blob` objects, each one of which is a snapshot of a specific file (Mapping of file's name and its content). 
   Every single `commit` object, except the initial one, would link with several `Blob` objects.
   


### Commit
The name of each `Commit` object is the SHA1 computed based on the values of its instance variables

#### Fields

[//]: # (1. #### Head)

[//]: # ()
[//]: # (2. #### Master)
3. #### `private String logMessage`
4. #### `private String timestamp`
5. #### `private String[] Blobs`
   Represents the `Blob` objects at that _commit_ version.
   However, for the same reason as below, it should not be a `Blob` object.
6. #### `private String Parent`
   Represents the `Commit` object that the current `Commit` is inherited from. 
   Itself should not be a `Commit` object since JAVA _Serialization_ automatically follows pointers, 
   namely any `Commit` object pointed by the current one would be serialized if it's the case.

### Blob
A `Blob` object represents a snapshot of a specific file, with its name stored in `UID` with the form of SHA1 that generated from its content
and its content stored in `CONTENT`. It should be permanently stored in `.gitlet/objects` folder after `gitlet.add`, this will very likely let to unused files be stored in the `.gitlet/objects` folder, we can delete them through `gitlet.gc`.
Every single `Commit` object, except the initial one, contains one or more `Blob` object's UID(s), which links them to the corresponding file(s) content(s).
So we can reuse existing files in version control, thus avoid redundancy.

#### Fields

1. #### `private String UID`
2. #### `private byte[] CONTENT`

## Algorithms

### GitRepository


### Commit
Automatically create a `Initial Commit` object while initializing `.gitlet`, this `Commit` is shared by all repositories .
When making new `Commit`, start with copying the `HEAD` _branch_ and modifying it based on what's in the `StagingArea`, then the _branches_ shall be updated.

## Persistence
Both `Commit` and `Blob`are stored in folders under `.gitlet/objects`, 
where the folders are named by the first two characters of their SHA-1 hash.
`StagingArea` is the file `.gitlet/index`.

Like in real Git, here we utilize the first two characters of a SHA-1 hash as a directory and the remaining characters as the file name.
This approach functions similarly to a hashing mechanism, 
which effectively narrows the search scope from all SHA-1 hashes in `.gitlet/objects` to those with a specific prefix, akin to the general principles of hashing

### Files in different states
1. #### Untracked files
   ##### Newly added files that waiting for confirmation:
   1. files in CWD that neither being staged for addition nor tracked in current commit.
   2. files staged for removal, but then re-created in CWD. The files could be _1_.the same as they were in current commit; or _2_.with different contents.
2. #### Files Changed but not staged for commit
   ##### Newly removed files or files with changed contents that waiting for confirmation:
   1. Files tracked in current commit, with content changed in CWD but not staged for addition;
   2. Files staged for addition, changed in CWD, but (this change) not staged. Including two cases:
   either _1._ files are tracked, and the updated contents are the same as the current commit version; 
   or _2._ files not tracked / tracked but with different contents.
   3. Files staged for addition, but deleted in the working directory. Including two cases:
   _1_. files tracked in current commit; _2_. files not tracked in current commit.
   4. Files tracked in current commit but not staged for addition, deleted in CWD, but not staged for removal.
3. #### Staged files
   ##### The confirmed files from state 1 and 2, plus untouched files form current commit:
   1. The `add` command should enrich the `Addition` field with newly added files and files with updated contents, which could either from _1.1, 1.2.2, 2.1 or 2.2.2_.
   2. The `rm` command will enrich the `Removal` field with files newly confirmed to be removed, which is from _2.3.1, 2.4_
   3. Files may also be withdrawn from `Addition` or `Removal` when operating `add` / `rm`, which could either be the case of _1.2, 2.2.1 or 2.3_.
4. #### Removed files
   ##### Files be deleted from the current commit