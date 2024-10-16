package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
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


    public static void copyFromRemote(String remotePath, String SHA1) throws IOException {
        File currentFile = Utils.join(Utils.join(remotePath, ".objects"), SHA1);
        File targetFile = Utils.join(Utils.join(OBJECTS_DIR), SHA1);

        // 使用 FileChannel 来进行文件拷贝
        try (FileInputStream inStream = new FileInputStream(currentFile);
             FileOutputStream outStream = new FileOutputStream(targetFile);
             FileChannel inChannel = inStream.getChannel();
             FileChannel outChannel = outStream.getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
    }

    public static void copyFromCurrent(String remotePath, String SHA1) throws IOException {
        File currentFile = Utils.join(Utils.join(remotePath, ".objects"), SHA1);
        File targetFile = Utils.join(Utils.join(OBJECTS_DIR), SHA1);

        // 使用 FileChannel 来进行文件拷贝
        try (FileInputStream inStream = new FileInputStream(currentFile);
             FileOutputStream outStream = new FileOutputStream(targetFile);
             FileChannel inChannel = inStream.getChannel();
             FileChannel outChannel = outStream.getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
    }



    public Blob(String remotePath, String name, String SHA1) throws IOException {
        this.name = name;
        //File remoteBlob = Utils.join(remotePath + "/objects", SHA1);
        File remoteBlob = Utils.join(Utils.join(remotePath, ".objects"), SHA1);
        //String content = Utils.readContentsAsString(remoteBlob);
        //this.contents = content.getBytes();
        this.contents = Files.readAllBytes(remoteBlob.toPath());
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
