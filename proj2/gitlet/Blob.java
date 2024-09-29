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

}
