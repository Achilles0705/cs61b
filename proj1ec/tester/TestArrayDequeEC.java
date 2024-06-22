package tester;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.princeton.cs.introcs.StdRandom;
import student.StudentArrayDeque;

public class TestArrayDequeEC {


    StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
    ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<>();
    String operationSequence = "";

    @Test
    public void test1() {
        for (int i = 1; i <= 1000; i++) {
            int optionalNum = StdRandom.uniform(2);
            if (optionalNum == 0) {
                sad.addFirst(i);
                ads.addFirst(i);
                operationSequence += "addFirst(" + i + ")\n";
            } else {
                sad.addLast(i);
                ads.addLast(i);
                operationSequence += "addLast(" + i + ")\n";
            }
        }

        for (int i = 0; i < 1000; i++) {  // Here, change to 999 as mentioned in the problem description
            int optionalNum = StdRandom.uniform(2);
            if (optionalNum == 0) {
                Integer sadRemoved = sad.removeFirst();
                Integer adsRemoved = ads.removeFirst();
                operationSequence += "removeFirst()\n";
                assertEquals(operationSequence, adsRemoved, sadRemoved);
            } else {
                Integer sadRemoved = sad.removeLast();
                Integer adsRemoved = ads.removeLast();
                operationSequence += "removeLast()\n";
                assertEquals(operationSequence, adsRemoved, sadRemoved);
            }
        }

    }

    /*public static void main(String[] args) {
        jh61b.junit.TestRunner.runTests(TestArrayDequeEC.class);
    }*/


}
