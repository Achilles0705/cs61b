package gitlet;

import java.io.File;
import static gitlet.Repository.*;
import static gitlet.Repository.REMOTE_DIR;
import static gitlet.Utils.join;

public class Remote {

    public static void addRemotePath(String remoteName, String remoteGitPath) {
        //Utils.writeContents(Utils.join(REMOTE_DIR + "/branch", remoteName), remoteGitPath);
        Utils.writeContents(Utils.join(REMOTE_DIR, remoteName), remoteGitPath);
    }

    public static void removeRemotePath(String remoteName) {
        Utils.join(REMOTE_DIR, remoteName).delete();
    }

    public static String getRemotePath(String remoteName) {
        File remoteFile = Utils.join(REMOTE_DIR, remoteName);
        if (!remoteFile.exists()) {
            return null;
        }
        return Utils.readContentsAsString(remoteFile);
    }
}