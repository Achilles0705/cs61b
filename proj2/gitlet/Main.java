package gitlet;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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
                bloop.global_log();
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
                checkArgsNum(args, 2);
                bloop.checkout(args[1]);
                break;
            case "branch":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.branch(args[1]);
                break;
            case "rm-branch":
                checkCWD();
                checkArgsNum(args, 2);
                bloop.rm_branch(args[1]);
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
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    private static void checkCWD() {    //有没有init初始化
        if (!GITLET_DIR.exists()) {
            Utils.error("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void checkNoArgs(String[] args) {    //empty情况
        if (args.length == 0) {
            Utils.error("Please enter a command.");
            System.exit(0);
        }
    }

    private static void checkArgsNum(String[] args, int n) {    //确保命令合法
        if (args.length != n) {
            Utils.error("Incorrect operands.");
            System.exit(0);
        }
    }
}
