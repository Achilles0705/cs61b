package gitlet;

import java.util.HashSet;
import java.util.HashMap;
import java.io.Serializable;
import java.io.File;
import static gitlet.Repository.*;

public class StagingArea implements Serializable {

    //public static HashSet<String> removeStage = new HashSet<>();
    //public static HashMap<String, String> addStage = new HashMap<>();

    private HashSet<String> removeStage;
    private HashMap<String, String> addStage;   //key是SHA1值，value是名字，通过SHA1找名字

    public StagingArea() {
        removeStage = new HashSet<>();
        addStage = new HashMap<>();
    }

    public static StagingArea load() {
        return Utils.readObject(STAGE_FILE, StagingArea.class);
    }

    public void save() {
        Utils.writeContents(STAGE_FILE, this);
    }

    public HashSet<String> getRemoveStage() {
        return removeStage;
    }

    public HashMap<String, String> getAddStage() {
        return addStage;
    }

    public void clear() {
        addStage.clear();
        removeStage.clear();
    }

}
