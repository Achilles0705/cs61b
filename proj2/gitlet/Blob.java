package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import static gitlet.Repository.*;

public class Blob implements Serializable {

    private byte[] contents;
    private String name;

    public Blob(String name) {
        this.name = name;
        this.contents = getContents();
    }


    public static void copyFromRemote(String remotePath, String sha1) throws IOException {
        File currentFile = Utils.join(Utils.join(remotePath, ".objects"), sha1);
        File targetFile = Utils.join(Utils.join(OBJECTS_DIR), sha1);

        // 使用 FileChannel 来进行文件拷贝
        try (FileInputStream inStream = new FileInputStream(currentFile);
             FileOutputStream outStream = new FileOutputStream(targetFile);
             FileChannel inChannel = inStream.getChannel();
             FileChannel outChannel = outStream.getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
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
        File objectsDir = Utils.join(remotePath, ".objects");
        if (!objectsDir.exists()) {
            objectsDir.mkdirs();  // 创建目录
        }
        File file = Utils.join(objectsDir, this.getSHA1());
        Utils.writeObject(file, this);
    }

}
