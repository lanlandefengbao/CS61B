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
   Storing the latest __Blobs__ that are _staged_ (confirmed) and ready for _commit_. 
   Its contents should be wiped out immediately after _commit_.
2. #### Commits
   Contains all historical `Commit` objects, each version involves `metadata` (_log message_, _timestamp_) and specific _Blobs_.
3. #### Blobs
   Contains all historical `Blob` objects, each one of which is a snapshot of a specific file (Mapping of file's name and its content). 
   Every single `commit` object, except the initial one, 
   would have several `Blob` objects representing the status of files at that _commit_ version.
   Whenever files in CWD is different with `HEAD branch`, new `Blob` object(s) is temporarily formed (untracked)
   and will only persist if _committed_.


### Commit
The name of each `Commit` object is the SHA1 computed based on the values of its instance variables

#### Fields

[//]: # (1. #### Head)

[//]: # ()
[//]: # (2. #### Master)
3. #### `private String logMessage`
4. #### `private Date timestamp`
5. #### `private String Blobs`
   Represents the `Blob` objects at that _commit_ version.
   However, for the same reason as `Parent`, it should not be a `Blob` object.
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
Automatically create a _commit_ while initializing `.gitlet`, this _commit_ is shared by all repositories (same __UID__ and __timestamp__).
When making new _commit_, start by copying the `HEAD` _branch_ and modifying it base on the `StagingArea`, the result will then be stored into `.gitlet` and the _branches_ shall be updated.

## Persistence

