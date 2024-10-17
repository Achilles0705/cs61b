package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Repository.*;

public class SomeObj {

    public void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        } else {
            Utils.exitWithMessage(
                    "A Gitlet version-control system already exists "
                            + "in the current directory."
            );
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
        StagingArea currentStagingArea = StagingArea.load();
        if (!f.exists() && !currentStagingArea.getRemoveStage().contains(fileName)) {
            Utils.exitWithMessage("File does not exist.");
        }
        if (currentStagingArea.getRemoveStage().contains(fileName)) { //若在删除区，则从中删除
            currentStagingArea.getRemoveStage().remove(fileName);
            currentStagingArea.save();
            return;
        }
        Blob currentBlob = new Blob(fileName);
        String currentSHA1 = currentBlob.getSHA1();
        Commit headCommit = Commit.load(Branch.getCommitId(HEAD.getBranchName()));

        if (headCommit.getBlobTree().containsKey(currentSHA1)) {  //最近提交里有，且完全相同
            if (currentStagingArea.getAddStage().containsValue(fileName)) { //在暂加区中，就删掉；不在暂加区不需要改动
                currentStagingArea.removeAddStageValue(fileName);
            }
        } else {   //最近提交里有，但内容不同；最近提交中没有——合并为else
            currentStagingArea.getAddStage().put(currentSHA1, fileName);
        }
        currentStagingArea.save();
        currentBlob.save();
    }

    public static void commit(String message) {
        StagingArea currentStagingArea = StagingArea.load();
        if (currentStagingArea.getAddStage().isEmpty() &&
                currentStagingArea.getRemoveStage().isEmpty()) {
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

    public static void mergeCommit(String message, String head, String another) {
        if (message.isEmpty()) {
            Utils.exitWithMessage("Please enter a commit message.");
        }
        Commit currentCommit = new Commit(message, head, another);
        currentCommit.save();
        Branch.setCommitId(HEAD.getBranchName(), currentCommit.getSHA1());
    }

    public void rm(String fileName) {
        StagingArea currentStagingArea = StagingArea.load();
        Commit currentCommit = Commit.load(Branch.getCommitId(HEAD.getBranchName()));

        if (currentStagingArea.getAddStage().containsValue(fileName)) {    //已经暂存，则取消暂存
            String fileSHA1 = valueToKey(currentStagingArea.getAddStage(), fileName);
            currentStagingArea.getAddStage().remove(fileSHA1);
        } else if (currentCommit.getBlobTree().containsValue(fileName)) {
            //当前head commit中记录了该文件，则暂存删除
            currentStagingArea.getRemoveStage().add(fileName);
            Utils.restrictedDelete(Utils.join(CWD, fileName));
        } else {    //失败情况
            Utils.exitWithMessage("No reason to remove the file.");
        }
        currentStagingArea.save();
    }

    public void log() { //merge情况已处理
        Commit currentCommit = null;
        if (HEAD.getBranchName().contains("/")) {   //重构log中的HEAD
            File branchFile = branchNameToBranchFile(HEAD.getBranchName());
            currentCommit = Commit.load(Utils.readContentsAsString(branchFile));
        } else {
            currentCommit = Commit.load(Branch.getCommitId(HEAD.getBranchName()));
        }
        while (currentCommit.getParent1() != null) {
            if (currentCommit.getParent2() == null) {
                System.out.println("===\n"
                        + "commit " + currentCommit.getSHA1() + "\n"
                        + "Date: " + currentCommit.getTimestamp() + "\n"
                        + currentCommit.getMessage() + "\n");
            } else {
                String shortId1 = currentCommit.getParent1().substring(0, 7);
                String shortId2 = currentCommit.getParent2().substring(0, 7);
                System.out.println("===\n"
                        + "commit " + currentCommit.getSHA1() + "\n"
                        + "Merge: " + shortId1 + " " + shortId2 + "\n"
                        + "Date: " + currentCommit.getTimestamp() + "\n"
                        + currentCommit.getMessage() + "\n");
            }
            currentCommit = Commit.load(currentCommit.getParent1());
        }
        System.out.println("===\n"
                + "commit " + currentCommit.getSHA1() + "\n"
                + "Date: " + currentCommit.getTimestamp() + "\n"
                + currentCommit.getMessage() + "\n");
    }

    public void globalLog() {  //merge情况已处理
        List<String> commitList = Utils.plainFilenamesIn(COMMITS_DIR);
        for (String commitId : commitList) {
            Commit currentCommit = Commit.load(commitId);
            if (currentCommit.getParent2() == null) {
                System.out.println("===\n"
                        + "commit " + commitId + "\n"
                        + "Date: " + currentCommit.getTimestamp() + "\n"
                        + currentCommit.getMessage() + "\n");
            } else {
                String shortId1 = currentCommit.getParent1().substring(0, 7);
                String shortId2 = currentCommit.getParent2().substring(0, 7);
                System.out.println("===\n"
                        + "commit " + commitId + "\n"
                        + "Merge: " + shortId1 + " " + shortId2 + "\n"
                        + "Date: " + currentCommit.getTimestamp() + "\n"
                        + currentCommit.getMessage() + "\n");
            }
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

    public void status() {  //较难
        TreeMap<String, String> addStage = StagingArea.load().getAddStage();
        TreeSet<String> removeStage = StagingArea.load().getRemoveStage();
        List<String> branchNameList = Utils.plainFilenamesIn(BRANCH_DIR);   //branch重构了

        Collections.sort(branchNameList);
        List<String> sortedAddStageName = sortMapNames(addStage);
        List<String> sortedRemoveStageName = sortSetNames(removeStage);
        Commit headCommit = Commit.load(Branch.getCommitId(HEAD.getBranchName()));

        System.out.println("=== Branches ===");
        for (String branchName : branchNameList) {
            if (Objects.equals(branchName, HEAD.getBranchName())) {
                System.out.print("*");  //当前分支前面多个*
            }
            System.out.println(branchName);
        }

        System.out.println("\n=== Staged Files ===");
        for (String stageName : sortedAddStageName) {
            System.out.println(stageName);
        }

        System.out.println("\n=== Removed Files ===");
        for (String stageName : sortedRemoveStageName) {
            System.out.println(stageName);
        }

        System.out.println("\n=== Modifications Not Staged For Commit ===");
        TreeSet<String> modifiedNameToPrint = new TreeSet<>();
        List<String> fileNameInCWD = Utils.plainFilenamesIn(CWD);
        for (String fileName : fileNameInCWD) {
            File filePath = Utils.join(CWD, fileName);
            String currentFileHash = Utils.sha1(fileName, Utils.readContents(filePath));
            String commitBlobHash = valueToKey(headCommit.getBlobTree(), fileName);
            String addStageFileHash = valueToKey(addStage, fileName);
            boolean isExist = Utils.join(CWD, fileName).exists();
            boolean isInAddStage = addStage.containsValue(fileName);
            boolean isInCommit = headCommit.getBlobTree().containsValue(fileName);
            boolean differentInCommit = false;
            boolean differentInAddStage = false;
            if (isExist && isInCommit) {
                if (!Objects.equals(currentFileHash, commitBlobHash)) {
                    differentInCommit = true; // 文件内容与提交时不同
                }
            }
            if (isExist && isInAddStage) {
                if (!Objects.equals(addStageFileHash, currentFileHash)) {
                    differentInAddStage = true; // 暂存区的内容与工作目录内容不同
                }
            }
            if (isExist && (differentInCommit || differentInAddStage)) {
                modifiedNameToPrint.add(fileName + " (modified)");
            }

        }
        for (Map.Entry<String, String> entry : headCommit.getBlobTree().entrySet()) {

            String fileName = entry.getValue();
            boolean isNotExist = !Utils.join(CWD, fileName).exists();
            boolean isNotInRemoveStage = !removeStage.contains(fileName);
            boolean isInAddStage = addStage.containsValue(fileName);
            if (isNotExist && (isNotInRemoveStage || isInAddStage)) {
                modifiedNameToPrint.add(fileName + " (deleted)");
            }

        }
        for (String name : modifiedNameToPrint) {
            System.out.println(name);
        }

        System.out.println("\n=== Untracked Files ===");
        List<String> fileList2 = Utils.plainFilenamesIn(CWD);
        TreeSet<String> untrackedNameToPrint = new TreeSet<>();
        for (String fileName : fileList2) {
            boolean isNotInHead = !headCommit.getBlobTree().containsValue(fileName);
            boolean isNotInAddStage = !addStage.containsValue(fileName);
            boolean isInRemoveStage = removeStage.contains(fileName);  // 检查是否在RemoveStage中
            // 文件未被追踪，且未暂存，或者文件已经被标记为删除但重新出现
            if ((isNotInHead && isNotInAddStage) || isInRemoveStage) {
                untrackedNameToPrint.add(fileName);
            }
        }
        for (String name : untrackedNameToPrint) {
            System.out.println(name);
        }
        //最后两部分（未暂存的修改和未跟踪的文件）是额外加分，价值 32 分。请随意将它们留空（只留下标题）。

    }

    public void checkoutFile(String fileName) {
        checkoutFileInCommit(Branch.getCommitId(HEAD.getBranchName()), fileName);
    }

    public void checkoutFileInCommit(String commitId, String fileName) {
        if (Commit.load(commitId) == null) {
            Utils.exitWithMessage("No commit with that id exists.");
        }
        Commit targetCommit = Commit.load(commitId);
        if (!targetCommit.getBlobTree().containsValue(fileName)) { //目标commit里没有
            Utils.exitWithMessage("File does not exist in that commit.");
        } else {
            String fileSHA1 = valueToKey(targetCommit.getBlobTree(), fileName);

            Blob currentBlob = Utils.readObject(Utils.join(OBJECTS_DIR, fileSHA1), Blob.class);
            File blobPath = Utils.join(CWD, currentBlob.getName());
            Utils.writeContents(blobPath, (Object) currentBlob.getContent());
        }
    }

    public void checkoutBranch(String branchName) {
        //该功能需要到目标branch的最新commit，感觉HEAD需要重构一下
        //找到目标commit，把blobTree里的嘎嘎往CWD里加就完事了
        if (branchName.contains("/")) {
            File branchFile = branchNameToBranchFile(branchName);

            // 读取远程分支的 commit ID
            String commitId = Utils.readContentsAsString(branchFile);
            Commit branchCommit = Commit.load(commitId);
            // 现在需要根据 commitId 更新工作区和索引
            checkoutCommit(branchCommit);
            HEAD.setBranchName(branchName);
            return;
        }
        if (Objects.equals(branchName, HEAD.getBranchName())) {
            Utils.exitWithMessage("No need to checkout the current branch.");
        }
        List<String> branchNameList = Utils.plainFilenamesIn(BRANCH_DIR);
        if (!branchNameList.contains(branchName)) {
            Utils.exitWithMessage("No such branch exists.");
        }
        Commit branchCommit = Commit.load(Branch.getCommitId(branchName));
        //看CWD里有没有currentCommit没有的，如果存在就报错
        checkoutCommit(branchCommit);
        HEAD.setBranchName(branchName);
    }

    private static void checkoutCommit(Commit commit) { //目前是checkoutBranch()要用，将来reset()和merge()会复用
        List<String> fileList = Utils.plainFilenamesIn(CWD);
        StagingArea stagingArea = StagingArea.load();
        String headCommitId = Branch.getCommitId(HEAD.getBranchName());
        Commit headCommit = Commit.load(headCommitId);
        for (String fileName : fileList) {  //如果CWD中有，但headCommit和addStage都没有
            boolean isNotInHead = !headCommit.getBlobTree().containsValue(fileName);
            boolean isNotInAddStage = !stagingArea.getAddStage().containsValue(fileName);
            boolean isInCurrentCommit = commit.getBlobTree().containsValue(fileName);
            if (isNotInHead && isNotInAddStage && isInCurrentCommit) {
                Utils.exitWithMessage(
                        "There is an untracked file in the way; "
                                + "delete it, or add and commit it first."
                );
            }
        }

        if (commit.getSHA1().equals(headCommitId)) { //如果目标commit就是HEAD指针，不做改变
            return;
        }
        for (String fileSHA1 : headCommit.getBlobTree().keySet()) {
            if (!commit.getBlobTree().containsKey(fileSHA1)) {
                Blob currentBlob = Utils.readObject(Utils.join(OBJECTS_DIR, fileSHA1), Blob.class);
                Utils.join(CWD, currentBlob.getName()).delete();
            }
        }
        for (String fileSHA1 : commit.getBlobTree().keySet()) {
            Blob currentBlob = Utils.readObject(Utils.join(OBJECTS_DIR, fileSHA1), Blob.class);
            File blobPath = Utils.join(CWD, currentBlob.getName());
            Utils.writeContents(blobPath, (Object) currentBlob.getContent());
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

    public void rmBranch(String branchName) {
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

    private boolean isConflict = false;
    private HashSet<String> skipFiles = new HashSet<>();

    public void merge(String branchName) {  //最难

        StagingArea currentStage = StagingArea.load();
        String branchCommitId = null;

        if (branchName.contains("/")) {   //重构log中的HEAD
            if (!currentStage.getAddStage().isEmpty() || !currentStage.getRemoveStage().isEmpty()) {
                Utils.exitWithMessage("You have uncommitted changes.");
            }
            File branchFile = branchNameToBranchFile(branchName);
            Commit remoteBranchCommit = Commit.load(Utils.readContentsAsString(branchFile));
            if (remoteBranchCommit == null) {
                Utils.exitWithMessage("A branch with that name does not exist.");
            }
            if (Objects.equals(branchName, HEAD.getBranchName())) {
                Utils.exitWithMessage("Cannot merge a branch with itself.");
            }
            branchCommitId = remoteBranchCommit.getSHA1();
        } else {
            if (!currentStage.getAddStage().isEmpty() || !currentStage.getRemoveStage().isEmpty()) {
                Utils.exitWithMessage("You have uncommitted changes.");
            }
            List<String> branchNameList = Utils.plainFilenamesIn(BRANCH_DIR);
            if (!branchNameList.contains(branchName)) {
                Utils.exitWithMessage("A branch with that name does not exist.");
            }
            if (Objects.equals(branchName, HEAD.getBranchName())) {
                Utils.exitWithMessage("Cannot merge a branch with itself.");
            }
            branchCommitId = Branch.getCommitId(branchName);
        }

        List<String> fileList2 = Utils.plainFilenamesIn(CWD);
        Commit headCommit = Commit.load(Branch.getCommitId(HEAD.getBranchName()));
        for (String fileName : fileList2) {  //如果CWD中有，但headCommit和addStage都没有
            boolean isNotInHead = !headCommit.getBlobTree().containsValue(fileName);
            boolean isNotInAddStage = !currentStage.getAddStage().containsValue(fileName);
            if (isNotInHead && isNotInAddStage) {
                Utils.exitWithMessage(
                        "There is an untracked file in the way; "
                                + "delete it, or add and commit it first."
                );
            }
        }

        String newMessage = null;
        String headCommitId = Branch.getCommitId(HEAD.getBranchName()); //CWD中是headCommit的文件
        String splitPointCommitId = findAncestor(headCommitId, branchCommitId);

        if (Objects.equals(splitPointCommitId, branchCommitId)) {
            Utils.exitWithMessage("Given branch is an ancestor of the current branch.");
        }
        if (Objects.equals(splitPointCommitId, headCommitId)) {
            checkoutCommit(Commit.load(branchCommitId));
            Branch.setCommitId(HEAD.getBranchName(), branchCommitId);
            Utils.exitWithMessage("Current branch fast-forwarded.");
        }

        Commit branchCommit = Commit.load(branchCommitId);
        Commit splitPointCommit = Commit.load(splitPointCommitId);
        splitPointCheck(headCommit, branchCommit, splitPointCommit);
        notSplitPointCheck(headCommit, branchCommit, splitPointCommit);

        List<String> fileList = Utils.plainFilenamesIn(CWD);
        for (String fileName : fileList) {
            if (!skipFiles.contains(fileName)) {
                add(fileName);
            }
        }
        newMessage = "Merged " + branchName + " into " + HEAD.getBranchName() + ".";
        mergeCommit(newMessage, headCommitId, branchCommitId);
        if (isConflict) {
            Utils.exitWithMessage("Encountered a merge conflict.");
        }

    }

    private void splitPointCheck(Commit headCommit, Commit branchCommit, Commit splitPointCommit) {

        TreeMap<String, String> splitPointTree = splitPointCommit.getBlobTree();
        TreeMap<String, String> headCommitTree = headCommit.getBlobTree();
        TreeMap<String, String> branchCommitTree = branchCommit.getBlobTree();

        for (Map.Entry<String, String> entry : splitPointTree.entrySet()) {
            String currentKey = entry.getKey();
            String currentName = entry.getValue();
            String headKey = valueToKey(headCommitTree, currentName);
            String branchKey = valueToKey(branchCommitTree, currentName);
            boolean isInHeadCommit = headCommitTree.containsKey(currentKey);
            boolean isNotInHeadCommit = !headCommitTree.containsKey(currentKey);
            boolean isInBranchCommit = branchCommitTree.containsKey(currentKey);
            boolean nameIsInHeadCommit = headCommitTree.containsValue(currentName);
            boolean nameIsInBranchCommit = branchCommitTree.containsValue(currentName);
            boolean nameIsNotInHeadCommit = !headCommitTree.containsValue(currentName);
            boolean nameIsNotInBranchCommit = !branchCommitTree.containsValue(currentName);
            if (isInHeadCommit && isInBranchCommit) {
                continue;
            } else if (isInHeadCommit) {   //unmodified in HEAD
                if (nameIsInBranchCommit) { //modified in other--1
                    checkoutFileInCommit(branchCommit.getSHA1(), currentName);
                } else if (nameIsNotInBranchCommit) { //not present in other--6
                    rm(currentName);
                }
            } else if (isInBranchCommit) {   //unmodified in other
                if (nameIsInHeadCommit && isNotInHeadCommit) {
                    //modified in HEAD--2
                    checkoutFileInCommit(headCommit.getSHA1(), currentName);
                }
                //not present in HEAD--7
            } else if (nameIsInHeadCommit && nameIsInBranchCommit) {
                if (!Objects.equals(headKey, branchKey)) {  //in diff way--3b,3a不变
                    String conflictContents = conflictFileContents(headKey, branchKey);
                    Utils.writeContents(Utils.join(CWD, currentName), conflictContents);
                }
            } else if (nameIsInHeadCommit && nameIsNotInBranchCommit) {
                String conflictContents = conflictFileContents(headKey, branchKey);
                Utils.writeContents(Utils.join(CWD, currentName), conflictContents);
            } else if (nameIsNotInHeadCommit && nameIsNotInBranchCommit) {
                skipFiles.add(currentName);
            }
        }

    }

    private String conflictFileContents(String headBlobId, String branchBlobId) {

        isConflict = true;
        String currentContents;
        String mergedContents;
        if (headBlobId == null) {
            currentContents = "";
        } else {
            Blob blob = Utils.readObject(Utils.join(OBJECTS_DIR, headBlobId), Blob.class);
            currentContents = new String(blob.getContent());
        }
        if (branchBlobId == null) {
            mergedContents = "";
        } else {
            Blob blob = Utils.readObject(Utils.join(OBJECTS_DIR, branchBlobId), Blob.class);
            mergedContents = new String(blob.getContent());
        }
        return "<<<<<<< HEAD\n" + currentContents + "=======\n" + mergedContents + ">>>>>>>\n";
    }

    private void notSplitPointCheck(Commit headCommit, Commit branchCommit, Commit splitPointCommit) {

        TreeMap<String, String> splitPointTree = splitPointCommit.getBlobTree();
        TreeMap<String, String> headCommitTree = headCommit.getBlobTree();
        TreeMap<String, String> branchCommitTree = branchCommit.getBlobTree();

        for (Map.Entry<String, String> entry : branchCommitTree.entrySet()) {   //5,4不变
            String currentName = entry.getValue();
            String currentKey = entry.getKey();
            boolean isNotInHeadCommit = !headCommitTree.containsKey(currentKey);
            boolean nameIsNotInSplitPoint = !splitPointTree.containsValue(currentName);
            boolean nameIsInHeadCommit = headCommitTree.containsValue(currentName);
            boolean nameIsNotInHeadCommit = !headCommitTree.containsValue(currentName);
            String headKey = valueToKey(headCommitTree, currentName);
            String branchKey = valueToKey(branchCommitTree, currentName);
            if (nameIsNotInSplitPoint && nameIsNotInHeadCommit) {
                checkoutFileInCommit(branchCommit.getSHA1(), entry.getValue());
            } else if (nameIsNotInSplitPoint && nameIsInHeadCommit && isNotInHeadCommit) {
                String conflictContents = conflictFileContents(headKey, branchKey);
                Utils.writeContents(Utils.join(CWD, currentName), conflictContents);
            }
        }

    }

    private String findAncestor(String headCommitId, String targetCommitId) {

        Queue<String> headCommitQueue = new LinkedList<>();
        Queue<String> targetCommitQueue = new LinkedList<>();
        HashSet<String> booked = new HashSet<>();
        headCommitQueue.add(headCommitId);
        targetCommitQueue.add(targetCommitId);

        while (!headCommitQueue.isEmpty()) {
            String currentCommitId = headCommitQueue.poll();
            if (Objects.equals(currentCommitId, targetCommitId)) {
                return currentCommitId;
            }
            booked.add(currentCommitId);
            Commit cur = Commit.load(currentCommitId);
            if (cur.getParent1() != null) {
                headCommitQueue.add(cur.getParent1());
            }
            if (cur.getParent2() != null) {
                headCommitQueue.add(cur.getParent2());
            }
        }

        while (!targetCommitQueue.isEmpty()) {
            String currentCommitId = targetCommitQueue.poll();
            if (booked.contains(currentCommitId)) {
                return currentCommitId;
            }
            Commit cur = Commit.load(currentCommitId);
            if (cur.getParent1() != null) {
                targetCommitQueue.add(cur.getParent1());
            }
            if (cur.getParent2() != null) {
                targetCommitQueue.add(cur.getParent2());
            }
        }
        return null;

    }

    public void addRemote(String remoteName, String remotePath) {
        ensureRemoteDirExists();
        if (Remote.getRemotePath(remoteName) != null) {
            Utils.exitWithMessage("A remote with that name already exists.");
        }
        remotePath = remotePath.replace("/", File.separator);
        Remote.addRemotePath(remoteName, remotePath);
    }

    public void rmRemote(String remoteName) {
        ensureRemoteDirExists();
        if (Remote.getRemotePath(remoteName) == null) {
            Utils.exitWithMessage("A remote with that name does not exist.");
        }
        Remote.removeRemotePath(remoteName);
    }

    public void push(String remoteName, String remoteBranchName) throws IOException {

        ensureRemoteDirExists();
        String remoteGitPath = Remote.getRemotePath(remoteName);


        File remoteFolder = new File(remoteGitPath);

        if (!remoteFolder.exists()) {
            Utils.exitWithMessage("Remote directory not found.");
        }
        String localBranchName = HEAD.getBranchName();
        List<String> commitHistory = new ArrayList<>();
        String localCommitId = Branch.getCommitId(localBranchName);
        String remoteCommitId = Branch.getRemoteCommitId(remoteGitPath, remoteBranchName);

        boolean isHistory = false;

        Commit commit = Commit.load(localCommitId);
        while (commit.getParent1() != null) {
            commitHistory.add(commit.getSHA1());
            if (Objects.equals(commit.getSHA1(), remoteCommitId)) {
                isHistory = true;
                break;
            }
            commit = Commit.load(commit.getParent1());
        }

        if (!isHistory) {
            Utils.exitWithMessage("Please pull down remote changes before pushing.");
        }

        for (String commitId : commitHistory) {
            commit = Commit.load(commitId);
            for (Map.Entry<String, String> entry : commit.getBlobTree().entrySet()) {
                Blob newBlob = new Blob(entry.getValue());
                newBlob.saveOnRemotePath(remoteGitPath);
            }
            commit.saveOnRemotePath(remoteGitPath);
        }

        Branch.setRemoteCommitId(remoteGitPath, remoteBranchName, localCommitId);

    }

    public void fetch(String remoteName, String remoteBranchName) throws IOException {

        ensureRemoteDirExists();
        String remoteGitPath = Remote.getRemotePath(remoteName);

        File remoteFolder = new File(remoteGitPath);

        if (!remoteFolder.exists()) {
            Utils.exitWithMessage("Remote directory not found.");
        }
        if (Branch.getRemoteCommitId(remoteGitPath, remoteBranchName) == null) {
            Utils.exitWithMessage("That remote does not have that branch.");
        }

        String localBranchName = remoteName + "/" + remoteBranchName;
        localBranchName = localBranchName.replace("/", File.separator);

        String remoteCommitId = Branch.getRemoteCommitId(remoteGitPath, remoteBranchName);
        String localCommitId = Branch.getCommitId(localBranchName);
        List<String> remoteCommitHistory = new ArrayList<>();

        Commit commit = Commit.remoteLoad(remoteGitPath, remoteCommitId);    //远程的新 本地的旧 追溯远程
        while (commit.getParent1() != null) {
            remoteCommitHistory.add(commit.getSHA1());
            if (Objects.equals(localCommitId, commit.getSHA1())) {
                break;
            }
            commit = Commit.remoteLoad(remoteGitPath, commit.getParent1());
        }

        if (remoteCommitHistory.isEmpty()) {
            Utils.exitWithMessage("Please pull down remote changes before pushing.");
        }

        Branch.setCommitId2(remoteName, remoteBranchName, remoteCommitId);

        for (String commitId : remoteCommitHistory) {
            commit = Commit.remoteLoad(remoteGitPath, commitId);
            for (Map.Entry<String, String> entry : commit.getBlobTree().entrySet()) {
                Blob.copyFromRemote(remoteGitPath, entry.getKey());
            }
            commit.save();
        }

    }

    public void pull(String remoteName, String remoteBranchName) throws IOException {
        fetch(remoteName, remoteBranchName);
        String localBranchName = remoteName + "/" + remoteBranchName;
        merge(localBranchName);
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

    public static String valueToKey(TreeMap<String, String> map, String name) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void ensureRemoteDirExists() {
        if (!REMOTE_DIR.exists()) {
            REMOTE_DIR.mkdirs();
        }
    }

    private File branchNameToBranchFile(String branchName) {

        File branchFile = null;
        if (branchName.contains("/")) {
            String[] parts = branchName.split("/");
            String remoteName = parts[0];  // 例如 R1
            String remoteBranchName = parts[1];  // 例如 master

            // 确保远程分支存在
            branchFile = Utils.join(BRANCH_DIR, remoteName, remoteBranchName);
            if (!branchFile.exists()) {
                Utils.exitWithMessage("No such branch: " + branchName);
            }
        }
        return branchFile;

    }
}
