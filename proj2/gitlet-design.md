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
   Storing the current temporal `Blob` objects that are _staged_ (confirmed) for _commit_. 
   These objects should be wiped out immediately after _commiting_.
2. #### Commits
   Contains all historical `Commit` objects, each version involves `metadata` (_log message_, _timestamp_), `UID of Parent Commit` and `UID of Blobs objects`.
3. #### Blobs
   Contains all historical `Blob` objects, each one of which is a snapshot of a specific file (Mapping of file's name and its content). 
   Every single `commit` object, except the initial one, would link with several `Blob` objects.
   Version control starting with forming __temporal__ `Blob` object(s) by comparing files in CWD with `HEAD branch`, 
   which then be used in _staging_ and _commiting_ process. A `Blob` object should only persist after _commiting_.


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

#### Fields

1. #### `private String UID`
2. #### {file_name : content}

## Algorithms

### GitRepository


### Commit
Automatically create a `Initial Commit` object while initializing `.gitlet`, this `Commit` is shared by all repositories .
When making new `Commit`, start with copying the `HEAD` _branch_ and modifying it based on what's in the `StagingArea`, then the _branches_ shall be updated.

## Persistence
All objects, regardless of their type (commit, blob...), 
are stored in `.gitlet/objects` in subdirectories named with the first two characters of their SHA-1 hash.