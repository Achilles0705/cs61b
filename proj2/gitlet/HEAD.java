package gitlet;

import static gitlet.Repository.*;

public class HEAD { //头指针定义改变，当前Commit的SHA1值 -> 当前Commit的分支名，要得到最新Commit要沿分支走下去

    public static void setBranchName(String branchName) {
        Utils.writeContents(HEAD_FILE, branchName);
    }

    public static String getBranchName() {
        return Utils.readContentsAsString(HEAD_FILE);
    }

}
