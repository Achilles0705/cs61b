package gitlet;

import org.w3c.dom.ls.LSException;

import java.io.File;
import java.util.*;

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
        branches.put(HEAD, "master");
        //CURRENT_BRANCH = "master";
        //branches.put(CURRENT_BRANCH,initialCommitId);
    }

    public void add(String fileName) {
        File f = Utils.join(OBJECTS_DIR, fileName);
        String currentSHA1 = Utils.sha1(f.getAbsolutePath());
        if (!f.exists()) {
            //Utils.error("File does not exist.");
            //System.exit(0);
            Utils.exitWithMessage("File does not exist.");
        }
        if (rmMap.containsKey(currentSHA1)) { //被rm标记，不再暂存
            System.exit(0);
        }
        StagingArea currentStagingArea = StagingArea.load();
        if (currentStagingArea.getRemoveStage().contains(currentSHA1)) { //若在删除区，则从中删除
            currentStagingArea.getRemoveStage().remove(currentSHA1);
        } else if (currentStagingArea.getAddStage().get(currentSHA1) == null) {    //暂存区没有，加入
            currentStagingArea.getAddStage().put(currentSHA1, fileName);
        } else if (!Objects.equals(currentStagingArea.getAddStage().get(currentSHA1), fileName)) {  //暂存区有，覆盖
            currentStagingArea.getAddStage().remove(currentSHA1);
            currentStagingArea.getAddStage().put(currentSHA1, fileName);
        }
        currentStagingArea.save();
    }

    public void commit(String message) {
        StagingArea currentStagingArea = StagingArea.load();
        if (currentStagingArea.getAddStage().isEmpty() && currentStagingArea.getRemoveStage().isEmpty()) {
            //Utils.error("No changes added to the commit.");
            //System.exit(0);
            Utils.exitWithMessage("No changes added to the commit.");
        }
        if (message.isEmpty()) {
            //Utils.error("Please enter a commit message.");
            //System.exit(0);
            Utils.exitWithMessage("Please enter a commit message.");
        }
        if (HEAD == null) {
            Commit currentCommit = new Commit(message, null, null);
            HEAD = Utils.sha1(currentCommit);
        } else {
            Commit HEADCommit = Commit.load(HEAD);  //取消全局变量parent_SHA1，现在一切只与HEAD指针有关
            String parent1 = HEAD;
            String parent2 = HEADCommit.getParent1();
            Commit currentCommit = new Commit(message, parent1, parent2); //1近2远
            currentCommit.getBlobTree().putAll(currentStagingArea.getAddStage());  //将缓存区的文件存到树中
            currentStagingArea.clear(); //commit后整个缓存区清空
            currentCommit.save(); //写入
            HEAD = Utils.sha1(currentCommit);
        }
    }

    public void rm(String fileName) {
        File f = Utils.join(OBJECTS_DIR, fileName);
        String currentSHA1 = Utils.sha1(f.getAbsolutePath());
        StagingArea currentStagingArea = StagingArea.load();
        Commit curCommit = Commit.load(HEAD);

        if (currentStagingArea.getAddStage().containsKey(currentSHA1)) {    //已经暂存，则取消暂存
            currentStagingArea.getAddStage().remove(currentSHA1);
        } else if (curCommit.getBlobTree().containsKey(currentSHA1)) {   //当前head commit中记录了该文件，则暂存删除
            currentStagingArea.getRemoveStage().add(currentSHA1);
            Utils.join(CWD, fileName).delete();
        } else {    //失败情况
            //Utils.error("No reason to remove the file.");
            //System.exit(0);
            Utils.exitWithMessage("No reason to remove the file.");
        }
    }

    public void log() { //合并提交还没有处理
        Commit currentCommit = Commit.load(HEAD);
        while (currentCommit.getParent1() != null) {
            System.out.println("===\n" +
                    "commit " + currentCommit + "\n" +
                    "Date: " + currentCommit.getTimestamp() + "\n" +
                    currentCommit.getMessage() + "\n");
            currentCommit = Commit.load(currentCommit.getParent1());
        }
    }

    public void global_log() {  //合并提交还没有处理
        List<String> commitList = Utils.plainFilenamesIn(OBJECTS_DIR);
        while (!commitList.isEmpty()) {
            Commit currentCommit = Commit.load(commitList.removeFirst());
            System.out.println("===\n" +
                    "commit " + currentCommit + "\n" +
                    "Date: " + currentCommit.getTimestamp() + "\n" +
                    currentCommit.getMessage() + "\n");
        }
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
                //Utils.error("Found no commit with that message.");
                //System.exit(0);
                Utils.exitWithMessage("Found no commit with that message.");
            } else {
                System.out.println(output);
            }
        }
    }

    public void status() {  //两种思路 1.把branch换成像lab8一样的列表嵌套链表 2.通过HEAD往parent找，直至找到
        //较难
        Commit currentCommit = Commit.load(HEAD);
        while (!branches.containsKey(currentCommit.getSHA1())) {
            currentCommit = Commit.load(currentCommit.getParent1());
        }
        String currentBranch = branches.get(currentCommit.getSHA1()); //找到当前分支
        HashMap<String, String> addStageName = StagingArea.load().getAddStage();
        HashSet<String> removeStageName = StagingArea.load().getRemoveStage();

        List<String> sortedBranchNames = sortMapNames(branches);
        List<String> sortedAddStageName = sortMapNames(addStageName);
        List<String> sortedRemoveStageName = sortSetNames(removeStageName);

        System.out.println("=== Branches ===");
        for (String branchName : sortedBranchNames) {
            if (Objects.equals(branchName, currentBranch)) {
                System.out.print("*");  //当前分支前面多个*
            }
            System.out.println(branchName);
        }
        System.out.println("\n=== Staged Files ===");
        for (String StageName : sortedAddStageName) {
            System.out.println(StageName);
        }
        System.out.println("\n=== Removed Files ===");
        for (String StageName : sortedRemoveStageName) {
            System.out.println(StageName);
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        System.out.println("\n=== Untracked Files ===");
        //最后两部分（未暂存的修改和未跟踪的文件）是额外加分，价值 32 分。请随意将它们留空（只留下标题）。

    }

    public void checkout(String name) {
        //三种情况
    }

    public void branch(String branchName) {
        if (branches.containsValue(branchName)) {
            //Utils.error("A branch with that name already exists.");
            //System.exit(0);
            Utils.exitWithMessage("A branch with that name already exists.");
        }
        branches.put(HEAD, branchName);
    }

    public void rm_branch(String branchName) {

    }

    public void reset(String commitID) {
        //代码复用
    }

    public void merge(String branchName) {
        //最难
    }

    /*private List<String> sortNames(Collection<String> names) {
        List<String> Names = new ArrayList<>(names);
        Collections.sort(Names);    //分支按名字字典排序
        return Names;
    }*/

    private List<String> sortMapNames(HashMap<String, String> map) {
        Collection<String> names = map.values();
        List<String> namesList = new ArrayList<>(names);
        Collections.sort(namesList);    //分支按名字字典排序
        return namesList;
    }

    private List<String> sortSetNames(HashSet<String> set) {
        List<String> namesList = new ArrayList<>(set);
        Collections.sort(namesList);    //分支按名字字典排序
        return namesList;
    }

}
