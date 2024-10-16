package gitlet;

import java.io.File;

import static gitlet.Repository.*;

public class Branch {

    public static void setCommitId(String branchName, String commitId) {
        File branchFile = Utils.join(BRANCH_DIR, branchName);
        Utils.writeContents(branchFile, commitId);
    }

    public static void setCommitId2(String name, String branchName, String commitId) {
        if (!Utils.join(BRANCH_DIR, name).exists()) {
            Utils.join(BRANCH_DIR, name).mkdir();
        }
        File branchFile = Utils.join(Utils.join(BRANCH_DIR, name), branchName);
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

    public static void setRemoteCommitId(String remoteGitPath, String remoteBranchName,
                                         String commitId) {
        File branchDir = Utils.join(remoteGitPath, ".branch");
        if (!branchDir.exists()) {
            branchDir.mkdirs();  // 创建目录
        }
        File remoteBranchFile = Utils.join(branchDir, remoteBranchName);
        Utils.writeContents(remoteBranchFile, commitId);
    }

    public static String getRemoteCommitId(String remoteGitPath, String remoteBranchName) {
        //File remoteBranchFile = Utils.join(remoteGitPath + ".branch", remoteBranchName);
        File remoteBranchFile = Utils.join(Utils.join(remoteGitPath, ".branch"), remoteBranchName);
        if (!remoteBranchFile.exists()) {
            return null;
        } else {
            return Utils.readContentsAsString(remoteBranchFile);
        }
    }

}
