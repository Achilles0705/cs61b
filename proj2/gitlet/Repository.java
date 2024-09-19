package gitlet;

import java.io.File;
import java.util.HashMap;
import java.security.MessageDigest;
import java.util.HashSet;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File OBJECTS_DIR = join(GITLET_DIR, ".objects");

    public static final File COMMITS_DIR = join(GITLET_DIR, ".commits");

    //public static String CURRENT_BRANCH = null;

    //public static final String initialCommitId = "0000000000000000000000000000000000000000";

    //分支用哈希表存
    public static HashMap<String, String> branches = new HashMap<>();

    //暂存区
    public static HashMap<String, String> addStage = new HashMap<>();

    public static HashSet<String> removeStage = new HashSet<>();

    //public static String parameter2;

    public static String parent1_SHA1;

    public static String parent2_SHA1;

    public static HashMap<String, String> rmMap = new HashMap<>();

    /* TODO: fill in the rest of this class. */
}
