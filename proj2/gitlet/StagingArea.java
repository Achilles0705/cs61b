package gitlet;

import java.util.*;
import java.io.Serializable;
import java.io.File;

import static gitlet.Repository.*;

public class StagingArea implements Serializable {

    //public static HashSet<String> removeStage = new HashSet<>();
    //public static HashMap<String, String> addStage = new HashMap<>();

    private TreeSet<String> removeStage;
    private TreeMap<String, String> addStage;   //key是SHA1值，value是名字，通过SHA1找名字

    public StagingArea() {
        removeStage = new TreeSet<>();
        addStage = new TreeMap<>();
    }

    public static StagingArea load() {
        return Utils.readObject(STAGE_FILE, StagingArea.class);
    }

    public void save() {
        Utils.writeObject(STAGE_FILE, this);
    }

    public TreeSet<String> getRemoveStage() {
        return removeStage;
    }

    public TreeMap<String, String> getAddStage() {
        return addStage;
    }

    public void clear() {
        addStage.clear();
        removeStage.clear();
    }

    public void addStage_removeValue(String value) {
        Iterator<Map.Entry<String, String>> iterator = addStage.entrySet().iterator();  //使用迭代器遍历 HashMap
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (entry.getValue().equals(value)) {   //检查当前 value 是否与要删除的 value 相匹配
                iterator.remove();  //使用迭代器的 remove() 方法安全地删除条目
                break;
            }
        }
    }

}
