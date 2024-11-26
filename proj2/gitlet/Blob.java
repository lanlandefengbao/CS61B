package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/** A Blob object contains SHA1 and byte[], which are different forms of the same content.
  For any commited file, we represent it by Map<filaname, Blob.UID>. */

public class Blob implements Serializable {

    private String UID;
    private byte[] Content;

    public Blob(String SHA1, byte[] CONTENT) {
        UID = SHA1;
        Content = CONTENT;
    }
}
