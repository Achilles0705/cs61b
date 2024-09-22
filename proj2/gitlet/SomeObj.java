package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static gitlet.Repository.*;

public class SomeObj {

    public void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        commit("initial commit");
        OBJECTS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        branches.put("master", HEAD);
        //CURRENT_BRANCH = "master";
        //branches.put(CURRENT_BRANCH,initialCommitId);
    }

    public void add(String fileName) {
        File f = Utils.join(OBJECTS_DIR, fileName);
        String currentSHA1 = Utils.sha1(f.getAbsolutePath());
        if (!f.exists()) {
            Utils.error("File does not exist.");
            System.exit(0);
        }
        if (rmMap.containsKey(currentSHA1)) { //被rm标记，不再暂存
            System.exit(0);
        }
        StagingArea currentStagingArea = StagingArea.load();
        if (currentStagingArea.getRemoveStage().contains(currentSHA1)) { //若在删除区，则从中删除
            currentStagingArea.getRemoveStage().remove(currentSHA1);
        } else if (currentStagingArea.getAddStage().get(fileName) == null) {    //暂存区没有，加入
            currentStagingArea.getAddStage().put(fileName, currentSHA1);
        } else if (!Objects.equals(currentStagingArea.getAddStage().get(fileName), currentSHA1)) {  //暂存区有，覆盖
            currentStagingArea.getAddStage().remove(fileName);
            currentStagingArea.getAddStage().put(fileName, currentSHA1);
        }
        currentStagingArea.save();
    }

    public void commit(String message) {
        Commit cur = new Commit(message, parent1_SHA1, parent2_SHA1);
    }

    public void rm(String fileName) {
        File f = Utils.join(OBJECTS_DIR, fileName);
        String currentSHA1 = Utils.sha1(f.getAbsolutePath());
        StagingArea currentStagingArea = StagingArea.load();
        if (currentStagingArea.getAddStage().containsKey(currentSHA1)) {
            currentStagingArea.getAddStage().remove(currentSHA1);
            return;
        }
        Commit curCommit = Commit.commitID_to_File(HEAD);
        if (curCommit.getBlobTree().containsKey(currentSHA1)) {   //当前head commit中记录了该文件
            currentStagingArea.getRemoveStage().add(currentSHA1);
            Utils.join(CWD, fileName).delete();
        }
    }

    public void log() {

    }

    public void global_log() {

    }

    public void find(String commitMessage) {
        List<String> commitList = Utils.plainFilenamesIn(COMMITS_DIR);
        StringBuilder builder = new StringBuilder();
        if (commitList != null) {
            for (String commitId : commitList) {
                File f = Utils.join(COMMITS_DIR, commitId);
                Commit cur = Utils.readObject(f, Commit.class);
                if (cur.getMessage().equals(commitMessage)) {
                    builder.append(commitId).append("\n");
                }
            }
            String output = builder.toString();
            if (output.isEmpty()) {
                Utils.error("Found no commit with that message.");
                System.exit(0);
            } else {
                System.out.println(output);
            }
        }
    }

    public void status() {
        //较难
    }

    public void checkout(String name) {
        //三种情况
    }

    public void branch(String branchName) {
        if (branches.containsKey(branchName)) {
            Utils.error("A branch with that name already exists.");
            System.exit(0);
        }
        branches.put(branchName, HEAD);
    }

    public void rm_branch(String branchName) {

    }

    public void reset(String commitID) {
        //代码复用
    }

    public void merge(String branchName) {
        //最难
    }

}
