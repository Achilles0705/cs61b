package gitlet;

// TODO: any imports you need here

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Achilles
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private String message; //commit双引号里的信息

    private String timestamp;

    private String parent1; //近

    private String parent2; //远

    private HashMap<String, String> blobTree;   //key是SHA1，value是name

    public Commit() {
        this.message = "initial commit";
        this.parent1 = null;
        this.parent2 = null;
        blobTree = new HashMap<>();
        this.timestamp = this.getDate();
    }

    public Commit(String message, String parent1, String parent2) { //根据说明，commit要保留最近的两个父级
        this.message = message;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.timestamp = getDate();
        blobTree = new HashMap<>();
        //File f = Utils.join(GITLET_DIR, message);

        buildBlobTree();    //对于blob的操作
        //Utils.writeObject(f, this); //写入
        //String curSHA1 = Utils.sha1(f);
        //HEAD = curSHA1; //HEAD指针为最新commit

        /*if (parent1_SHA1 == null) { //更新两个全局parent
            parent1_SHA1 = curSHA1;
        } else if (parent2_SHA1 == null) {
            parent2_SHA1 = curSHA1;
        } else {
            //parent2_SHA1 = curSHA1;
            //parent1_SHA1 = parent2_SHA1;
            parent1_SHA1 = HEADCommit.parent2;
            parent2_SHA1 = HEAD;
            HEAD = curSHA1;
        }*/
    }

    private void buildBlobTree() {
        Commit parentCommit;
        File parentFile = Utils.join(COMMITS_DIR, parent1);
        if (parentFile.exists()) {
            parentCommit = Utils.readObject(parentFile, Commit.class);
            this.blobTree.putAll(parentCommit.blobTree); //把父commit的blobs加到现在commit中
        }
        StagingArea currentStagingArea = StagingArea.load();
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
            for (String Id : commitIdList) {
                if (Id.startsWith(commitId)) {
                    commitId = Id;
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

    private String getDate() {
        if (this.parent1 == null) {
            return "00:00:00 UTC, Thursday, 1 January 1970";
        } else {
            return generateTimestamp();
        }
    }

    private String generateTimestamp() {    //生成一条当下的时间戳
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
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

    public HashMap<String, String> getBlobTree() {
        return this.blobTree;
    }

    public void save() {
        Utils.writeObject(Utils.join(COMMITS_DIR, this.getSHA1()), this);
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


    /* TODO: fill in the rest of this class. */
}
