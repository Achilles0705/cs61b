package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.*;

public class Blob implements Serializable {

    private byte[] contents;
    private String name;

    public Blob(String name) {
        this.name = name;
        this.contents = getContents();
    }

    public Blob(String remotePath, String name, String SHA1) {
        this.name = name;
        File remoteBlob = Utils.join(remotePath + "/objects", SHA1);
        this.contents = Utils.readContents(remoteBlob);
    }

    public String getSHA1() {
        return Utils.sha1(this.name, this.contents);
    }

    public byte[] getContent() {
        return this.contents;
    }

    public String getName() {
        return this.name;
    }

    private byte[] getContents() {
        File f = Utils.join(CWD, this.name);
        return Utils.readContents(f);
    }

    public void save() {    //所有的文件都会存在Objects.dir中不会被改变，改变的只有CWD中的文件
        Utils.writeObject(Utils.join(OBJECTS_DIR, this.getSHA1()), this);
    }

    public void saveOnRemotePath(String remotePath) {
        File objectsDir = Utils.join(remotePath, "/objects");
        if (!objectsDir.exists()) {
            objectsDir.mkdirs();  // 创建目录
        }
        File file = Utils.join(objectsDir, this.getSHA1());
        Utils.writeObject(file, this);
    }

}
