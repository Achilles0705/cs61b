package gitlet;

import java.io.File;
import static gitlet.Repository.*;

public class Branch {

    public static void setCommitId(String branchName, String commitId) {
        File branchFile = Utils.join(BRANCH_DIR, branchName);
        Utils.writeContents(branchFile, commitId);
    }

    public static String getCommitId(String branchName) {
        File branchFile = Utils.join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            return null;
        } else {
            return Utils.readContentsAsString(branchFile);
        }
    }

}
