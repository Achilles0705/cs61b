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
        if (removeStage.contains(currentSHA1)) { //若在删除区，则从中删除
            removeStage.remove(currentSHA1);
        } else if (addStage.get(fileName) == null) {    //暂存区没有，加入
            addStage.put(fileName, currentSHA1);
        } else if (!Objects.equals(addStage.get(fileName), currentSHA1)) {  //暂存区有，覆盖
            addStage.remove(fileName);
            addStage.put(fileName, currentSHA1);
        }
    }

    public void commit(String message) {
        Commit cur = new Commit(message, parent1_SHA1, parent2_SHA1);
    }

    public void rm(String fileName) {
        File f = Utils.join(OBJECTS_DIR, fileName);
        String currentSHA1 = Utils.sha1(f.getAbsolutePath());
        if (addStage.containsKey(currentSHA1)) {
            addStage.remove(currentSHA1);
        } else if (true) {   //当前head commit中记录了该文件
            removeStage.add(fileName);
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

    }

    public void checkout(String name) {

    }

    public void branch(String branchName) {

    }

    public void rm_branch(String branchName) {

    }

    public void reset(String commitID) {

    }

    public void merge(String branchName) {
        //最难
    }

}
