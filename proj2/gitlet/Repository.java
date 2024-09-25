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

    public static final File BRANCH_DIR = join(GITLET_DIR, ".branch");

    public static final File STAGE_FILE = join(GITLET_DIR, "stagingArea");

    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    //分支用哈希表存
    //public static HashMap<String, String> branches = new HashMap<>();   //key是SHA1值，value是名字，通过SHA1找名字

    //暂存区
    //public static HashMap<String, String> addStage = new HashMap<>();

    //public static HashSet<String> removeStage = new HashSet<>(); 新建了类

    //public static String parameter2;

    //public static String parent1_SHA1;  //用全局变量有问题

    //public static String parent2_SHA1;

    //public static String HEAD;  //有问题的 要存下来

    //public static HashMap<String, String> rmMap = new HashMap<>();

    /* TODO: fill in the rest of this class. */
}
