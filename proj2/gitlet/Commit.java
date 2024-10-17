package gitlet;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Achilles
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private String message; //commit双引号里的信息

    private String timestamp;

    private String parent1; //近

    private String parent2; //远

    private TreeMap<String, String> blobTree;   //key是SHA1，value是name

    public Commit() {
        this.message = "initial commit";
        this.parent1 = null;
        this.parent2 = null;
        blobTree = new TreeMap<>();
        this.timestamp = getDate();
    }

    public Commit(String message, String parent1, String parent2) { //根据说明，commit要保留最近的两个父级
        this.message = message;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.timestamp = getDate();
        blobTree = new TreeMap<>();
        buildBlobTree();    //对于blob的操作
    }

    private void buildBlobTree() {
        Commit parentCommit;
        File parentFile = Utils.join(COMMITS_DIR, parent1);
        if (parentFile.exists()) {
            parentCommit = Utils.readObject(parentFile, Commit.class);
            this.blobTree.putAll(parentCommit.getBlobTree()); //把父commit的blobs加到现在commit中
        }
        StagingArea currentStagingArea = StagingArea.load();

        for (Map.Entry<String, String> entry : currentStagingArea.getAddStage().entrySet()) {
            //有同名文件则删除
            if (blobTree.containsValue(entry.getValue())) {
                String fileSHA1 = SomeObj.valueToKey(blobTree, entry.getValue());
                blobTree.remove(fileSHA1);
            }
        }

        blobTree.putAll(currentStagingArea.getAddStage());  //将暂加区的文件存到树中
        for (String removeStageFileName : currentStagingArea.getRemoveStage()) {    //暂减区的从中删除
            blobTree.entrySet().removeIf(entry -> entry.getValue().equals(removeStageFileName));
        }
        currentStagingArea.clear();
        currentStagingArea.save();
    }

    public static Commit load(String commitId) {
        if (commitId.length() < 40) {
            List<String> commitIdList = Utils.plainFilenamesIn(COMMITS_DIR);
            for (String id : commitIdList) {
                if (id.startsWith(commitId)) {
                    commitId = id;
                    break;
                }
            }
        }
        File f = Utils.join(COMMITS_DIR, commitId);
        if (!f.exists()) {
            return null;
        }
        return Utils.readObject(f, Commit.class);
    }

    public static Commit remoteLoad(String remoteGitPath, String remoteCommitId) {
        File branchFile = Utils.join(remoteGitPath, ".commits");
        if (remoteCommitId.length() < 40) {
            List<String> commitIdList = Utils.plainFilenamesIn(branchFile);
            for (String id : commitIdList) {
                if (id.startsWith(remoteCommitId)) {
                    remoteCommitId = id;
                    break;
                }
            }
        }
        File f = Utils.join(branchFile, remoteCommitId);
        if (!f.exists()) {
            return null;
        }
        return Utils.readObject(f, Commit.class);
    }

    private String getDate() {
        if (this.timestamp == null) {
            return generateTimestamp();
        }
        return this.timestamp;
    }

    private String generateTimestamp() {    //生成一条当下的时间戳
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        if (this.parent1 == null) {
            Date defaultDate = new Date(0);
            return formatter.format(defaultDate);
        }
        Date date = new Date();
        return formatter.format(date);
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getParent1() {
        return this.parent1;
    }

    public String getParent2() {
        return this.parent2;
    }

    public TreeMap<String, String> getBlobTree() {
        return this.blobTree;
    }

    public void save() {
        Utils.writeObject(Utils.join(COMMITS_DIR, this.getSHA1()), this);
    }

    public void saveOnRemotePath(String remotePath) {
        File commitsDir = Utils.join(remotePath, ".commits");
        if (!commitsDir.exists()) {
            commitsDir.mkdirs();  // 创建目录
        }
        File file = Utils.join(commitsDir, this.getSHA1());
        Utils.writeObject(file, this);
    }

    public String getSHA1() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Utils.sha1(byteArray);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize Commit object", e);
        }
    }

}
