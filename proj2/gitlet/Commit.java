package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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

    private String parent1;

    private String parent2;

    private HashMap<String, String> blobTree;

    public Commit(String message, String parent1, String parent2) { //根据说明，commit要保留最近的两个父级
        this.message = message;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.timestamp = getDate();
        blobTree = new HashMap<>();
        File f = Utils.join(GITLET_DIR, message);

        buildBlobTree();    //对于blob的操作
        Utils.writeObject(f, this); //写入
        String curSHA1 = Utils.sha1(f);
        if (parent1_SHA1 == null) { //更新两个全局parent
            parent1_SHA1 = curSHA1;
        } else if (parent2_SHA1 == null) {
            parent2_SHA1 = curSHA1;
        } else {
            parent2_SHA1 = curSHA1;
            parent1_SHA1 = parent2_SHA1;
        }
    }

    private void buildBlobTree() {
        blobTree.putAll(addStage);  //将缓存区的文件存到树中
        Commit parentCommit;
        File parentFile = Utils.join(COMMITS_DIR, parent1);
        if (parentFile.exists()) {
            parentCommit = Utils.readObject(parentFile, Commit.class);
            blobTree.putAll(parentCommit.blobTree); //把父commit的blobs加到现在commit中
        }
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


    /* TODO: fill in the rest of this class. */
}
