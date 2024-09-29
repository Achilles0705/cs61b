package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Repository.*;

public class SomeObj {

    public void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        } else {
            Utils.exitWithMessage("A Gitlet version-control system already exists in the current directory.");
        }
        OBJECTS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BRANCH_DIR.mkdir();

        StagingArea stagingArea = new StagingArea();
        stagingArea.save();
        Commit initialCommit = new Commit();
        initialCommit.save();

        Branch.setCommitId("master", initialCommit.getSHA1());
        HEAD.setBranchName("master");
    }

    public void add(String fileName) {
        File f = Utils.join(CWD, fileName);
        if (!f.exists()) {
            Utils.exitWithMessage("File does not exist.");
        }
        Blob currentBlob = new Blob(fileName);
        String currentSHA1 = currentBlob.getSHA1();
        Commit headCommit = Commit.load(Branch.getCommitId(HEAD.getBranchName()));

        StagingArea currentStagingArea = StagingArea.load();
        if (currentStagingArea.getRemoveStage().contains(currentSHA1)) { //若在删除区，则从中删除
            currentStagingArea.getRemoveStage().remove(currentSHA1);
        } else if (headCommit.getBlobTree().containsKey(currentSHA1)) {  //最近提交里有，且完全相同
            if (currentStagingArea.getAddStage().containsValue(fileName)) { //在暂加区中，就删掉；不在暂加区不需要改动
                currentStagingArea.addStage_removeValue(fileName);
            }
        } else {   //最近提交里有，但内容不同；最近提交中没有——合并为else
            currentStagingArea.getAddStage().put(currentSHA1, fileName);
        }
        currentStagingArea.save();
        currentBlob.save();
    }

    public void commit(String message) {
        StagingArea currentStagingArea = StagingArea.load();
        if (currentStagingArea.getAddStage().isEmpty() && currentStagingArea.getRemoveStage().isEmpty()) {
            Utils.exitWithMessage("No changes added to the commit.");
        }
        if (message.isEmpty()) {
            Utils.exitWithMessage("Please enter a commit message.");
        }
        String headCommitId = Branch.getCommitId(HEAD.getBranchName());
        Commit currentCommit = new Commit(message, headCommitId, null); //parent2只在merge中起作用
        currentCommit.save();
        Branch.setCommitId(HEAD.getBranchName(), currentCommit.getSHA1());
    }

    public void rm(String fileName) {
        StagingArea currentStagingArea = StagingArea.load();
        Commit currentCommit = Commit.load(Branch.getCommitId(HEAD.getBranchName()));

        if (currentStagingArea.getAddStage().containsValue(fileName)) {    //已经暂存，则取消暂存
            String fileSHA1 = valueToKey(currentStagingArea.getAddStage(), fileName);
            currentStagingArea.getAddStage().remove(fileSHA1);
        } else if (currentCommit.getBlobTree().containsValue(fileName)) {   //当前head commit中记录了该文件，则暂存删除
            currentStagingArea.getRemoveStage().add(fileName);
            Utils.restrictedDelete(Utils.join(CWD, fileName));
        } else {    //失败情况
            Utils.exitWithMessage("No reason to remove the file.");
        }
        currentStagingArea.save();
    }

    public void log() { //合并提交还没有处理
        Commit currentCommit = Commit.load(Branch.getCommitId(HEAD.getBranchName()));
        while (currentCommit.getParent1() != null) {
            System.out.println("===\n" +
                    "commit " + currentCommit.getSHA1() + "\n" +
                    "Date: " + currentCommit.getTimestamp() + "\n" +
                    currentCommit.getMessage() + "\n");
            currentCommit = Commit.load(currentCommit.getParent1());
        }
        System.out.println("===\n" +
                "commit " + currentCommit.getSHA1() + "\n" +
                "Date: " + currentCommit.getTimestamp() + "\n" +
                currentCommit.getMessage() + "\n");
    }

    public void global_log() {  //合并提交还没有处理
        List<String> commitList = Utils.plainFilenamesIn(COMMITS_DIR);
        for (String commitId : commitList) {
            Commit currentCommit = Commit.load(commitId);
            System.out.println("===\n" +
                    "commit " + commitId + "\n" +
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
                Commit currentCommit = Utils.readObject(f, Commit.class);
                if (currentCommit.getMessage().equals(commitMessage)) {
                    builder.append(commitId).append("\n");
                }
            }
            String output = builder.toString();
            if (output.isEmpty()) {
                Utils.exitWithMessage("Found no commit with that message.");
            } else {
                System.out.println(output);
            }
        }
    }

    public void status() {  //两种思路 1.把branch换成像lab8一样的列表嵌套链表 2.通过HEAD往parent找，直至找到 3.重构branch
        //较难
        TreeMap<String, String> addStageName = StagingArea.load().getAddStage();
        TreeSet<String> removeStageName = StagingArea.load().getRemoveStage();
        List<String> branchNameList = Utils.plainFilenamesIn(BRANCH_DIR);   //branch重构了

        Collections.sort(branchNameList);
        List<String> sortedAddStageName = sortMapNames(addStageName);
        List<String> sortedRemoveStageName = sortSetNames(removeStageName);

        System.out.println("=== Branches ===");
        for (String branchName : branchNameList) {
            if (Objects.equals(branchName, HEAD.getBranchName())) {
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

    public void checkoutFile(String fileName) {
        checkoutCommit_File(Branch.getCommitId(HEAD.getBranchName()), fileName);
    }

    public void checkoutCommit_File(String commitId, String fileName) {
        if (Commit.load(commitId) == null) {
            Utils.exitWithMessage("No commit with that id exists.");
        }
        String fileSHA1 = null;
        Commit currentCommit = Commit.load(commitId);
        if (!currentCommit.getBlobTree().containsValue(fileName)) { //当前commit里没有
            Utils.exitWithMessage("File does not exist in that commit.");
        } else {
            Utils.restrictedDelete(Utils.join(CWD, fileName));  //CWD中有的话就删了再加，没有的话这条忽略
            fileSHA1 = valueToKey(currentCommit.getBlobTree(), fileName);

            Blob currentBlob = Utils.readObject(Utils.join(OBJECTS_DIR, fileSHA1), Blob.class);
            Utils.writeContents(Utils.join(CWD, currentBlob.getName()), (Object) currentBlob.getContent());
        }
    }

    public void checkoutBranch(String branchName) {
        //该功能需要到目标branch的最新commit，感觉HEAD需要重构一下
        //找到目标commit，把blobTree里的嘎嘎往CWD里加就完事了
        if (Objects.equals(branchName, HEAD.getBranchName())) {
            Utils.exitWithMessage("No need to checkout the current branch.");
        }
        List<String> branchNameList = Utils.plainFilenamesIn(BRANCH_DIR);
        if (!branchNameList.contains(branchName)) {
            Utils.exitWithMessage("No such branch exists.");
        }
        Commit branchCommit = Commit.load(Branch.getCommitId(branchName));  //看CWD里有没有currentCommit没有的，如果存在就报错
        checkoutCommit(branchCommit);
        HEAD.setBranchName(branchName);
    }

    private static void checkoutCommit(Commit commit) { //目前是checkoutBranch()要用，将来reset()和merge()会复用
        List<String> fileList = Utils.plainFilenamesIn(CWD);
        StagingArea stagingArea = StagingArea.load();
        String headCommitId = Branch.getCommitId(HEAD.getBranchName());
        Commit headCommit = Commit.load(headCommitId);
        for (String fileName : fileList) {  //如果CWD中有，但headCommit和addStage都没有
            if (!headCommit.getBlobTree().containsValue(fileName) && !stagingArea.getAddStage().containsValue(fileName)) {
                Utils.exitWithMessage("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        if (commit.getSHA1().equals(headCommitId)) { //如果目标commit就是HEAD指针，不做改变
            return;
        }
        for (String fileSHA1 : commit.getBlobTree().keySet()) {
            Blob currentBlob = Utils.readObject(Utils.join(OBJECTS_DIR, fileSHA1), Blob.class);
            Utils.writeContents(Utils.join(CWD, currentBlob.getName()), (Object) currentBlob.getContent());
        }
        for (String fileSHA1 : headCommit.getBlobTree().keySet()) {
            if (!commit.getBlobTree().containsKey(fileSHA1)) {
                Blob currentBlob = Utils.readObject(Utils.join(OBJECTS_DIR, fileSHA1), Blob.class);
                Utils.join(CWD, currentBlob.getName()).delete();
            }
        }
        stagingArea.clear();
        stagingArea.save();
    }

    public void branch(String branchName) {
        List<String> branchNameList = Utils.plainFilenamesIn(BRANCH_DIR);
        if (branchNameList.contains(branchName)) {
            Utils.exitWithMessage("A branch with that name already exists.");
        }
        Branch.setCommitId(branchName, Branch.getCommitId(HEAD.getBranchName()));
    }

    public void rm_branch(String branchName) {
        List<String> branchNameList = Utils.plainFilenamesIn(BRANCH_DIR);
        if (!branchNameList.contains(branchName)) {
            Utils.exitWithMessage("A branch with that name does not exist.");
        }
        if (Objects.equals(branchName, HEAD.getBranchName())) {
            Utils.exitWithMessage("Cannot remove the current branch.");
        }
        Utils.join(BRANCH_DIR, branchName).delete();
    }

    public void reset(String commitId) {
        Commit commit = Commit.load(commitId);
        if (Commit.load(commitId) == null) {
            Utils.exitWithMessage("No commit with that id exists.");
        }
        checkoutCommit(commit);
        Branch.setCommitId(HEAD.getBranchName(), commitId);
    }

    public void merge(String branchName) {
        //最难
    }

    private List<String> sortMapNames(TreeMap<String, String> map) {
        Collection<String> names = map.values();
        List<String> namesList = new ArrayList<>(names);
        Collections.sort(namesList);    //分支按名字字典排序
        return namesList;
    }

    private List<String> sortSetNames(TreeSet<String> set) {
        List<String> namesList = new ArrayList<>(set);
        Collections.sort(namesList);    //分支按名字字典排序
        return namesList;
    }

    private static String valueToKey(TreeMap<String, String> map, String name) {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {    //通过value找key
            Map.Entry<String, String> entry = iterator.next();
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
