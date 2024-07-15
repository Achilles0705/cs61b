package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join(".capers"); // TODO Hint: look at the `join`
                                            //      function in Utils

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        // TODO
        File story = Utils.join(CAPERS_FOLDER, "story");
        //File dogsName = Utils.join(Dog.DOG_FOLDER, "dogsName");
        try {
            if (!CAPERS_FOLDER.exists()) {
                CAPERS_FOLDER.mkdir();
            }
            if (!Dog.DOG_FOLDER.exists()) {
                Dog.DOG_FOLDER.mkdir();
            }
            if (!story.exists()) {
                story.createNewFile();
            }
            /*if (!dogsName.exists()) {
                dogsName.createNewFile();
            }*/
        } catch (IOException excp) {
            System.out.println("创建文件时出错: " + excp.getMessage());
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        // TODO
        File story = Utils.join(CAPERS_FOLDER, "story");
        String oldStory = readContentsAsString(story);
        String currentStory = oldStory + text + '\n';
        Utils.writeContents(story, currentStory);
        System.out.println(currentStory);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     * 使用 args 的前三个非命令参数（名称、品种、年龄）创建并持久保存一只狗。
     * 还使用 toString() 打印出狗的信息。
     */
    public static void makeDog(String name, String breed, int age) {
        // TODO
        Dog myDog = new Dog(name, breed, age);
        myDog.saveDog();
        System.out.println(myDog.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * 持续增加狗的年龄并打印出一条庆祝信息。
     * 还使用 toString() 打印出狗的信息。
     * 根据 args 的第一个非命令参数选择要增加的狗。
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        // TODO
        Dog myDog = Dog.fromFile(name);
        myDog.haveBirthday();
        myDog.saveDog();
    }
}
