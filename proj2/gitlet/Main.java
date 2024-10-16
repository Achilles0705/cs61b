package gitlet;

import java.io.IOException;
import static gitlet.Repository.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Achilles
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {

        checkNoArgs(args);
        SomeObj bloop = new SomeObj();
        String firstArg = args[0];
        /*if (args.length == 2) {
            parameter2 = args[1];
        }*/
        switch(firstArg) {
            case "init":
                checkArgsNum(args, 1);
                bloop.init();
                break;
            case "add-remote":
                checkCWD();
                checkArgsNum(args, 3);
                bloop.addRemote(args[1], args[2]);
                break;
            case "rm-remote":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.rmRemote(args[1]);
                break;
            case "add":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.add(args[1]);
                break;
            case "commit":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.commit(args[1]);
                break;
            case "rm":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.rm(args[1]);
                break;
            case "log":
                checkCWD();
                checkArgsNum(args, 1);
                bloop.log();
                break;
            case "global-log":
                checkCWD();
                checkArgsNum(args, 1);
                bloop.globalLog();
                break;
            case "find":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.find(args[1]);
                break;
            case "status":
                checkCWD();
                checkArgsNum(args, 1);
                bloop.status();
                break;
            case "checkout":
                checkCWD();
                if (args.length == 2) {
                    bloop.checkoutBranch(args[1]);
                }
                if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        Utils.exitWithMessage("Incorrect operands.");
                    }
                    bloop.checkoutFile(args[2]);
                }
                if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        Utils.exitWithMessage("Incorrect operands.");
                    }
                    bloop.checkoutFileInCommit(args[1], args[3]);
                }
                break;
            case "branch":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.branch(args[1]);
                break;
            case "rm-branch":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.rmBranch(args[1]);
                break;
            case "reset":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.reset(args[1]);
                break;
            case "merge":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.merge(args[1]);
                break;
            case "push":
                try {
                    checkCWD();
                    checkArgsNum(args, 3);
                    bloop.push(args[1], args[2]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "fetch":
                try {
                    checkCWD();
                    checkArgsNum(args, 3);
                    bloop.fetch(args[1], args[2]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "pull":
                try {
                    checkCWD();
                    checkArgsNum(args, 3);
                    bloop.pull(args[1], args[2]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    private static void checkCWD() {    //有没有init初始化
        if (!GITLET_DIR.exists()) {
            Utils.exitWithMessage("Not in an initialized Gitlet directory.");
        }
    }

    private static void checkNoArgs(String[] args) {    //empty情况
        if (args.length == 0) {
            Utils.exitWithMessage("Please enter a command.");
        }
    }

    private static void checkArgsNum(String[] args, int n) {    //确保命令合法
        if (args.length != n) {
            Utils.exitWithMessage("Incorrect operands.");
        }
    }
}
