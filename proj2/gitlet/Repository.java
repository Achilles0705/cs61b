package gitlet;

import java.io.File;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Achilles
 */
public class Repository {
    /*
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

    public static final File REMOTE_DIR = Utils.join(GITLET_DIR, ".remote");

    public static final File STAGE_FILE = join(GITLET_DIR, "stagingArea");

    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

}
