package gh2;

import deque.LinkedListDeque;
import deque.Deque;
import edu.princeton.cs.algs4.StdRandom;

public class Drums {


    private static final int SR = 44100;
    private static final double DECAY = .2;
    private int bufferSize;
    private Deque<Double> buffer;

    public Drums(double frequency) {
        bufferSize = (int) Math.round(SR / frequency);
        buffer = new LinkedListDeque<>();
        for (int i = 0; i < bufferSize; i++) {
            buffer.addLast(0.0);
        }
    }

    public void pluck() {
        for (int i = 0; i < bufferSize; i++) {
            double r = Math.random() - 0.5;
            buffer.removeFirst();
            buffer.addLast(r);
        }
    }

    public void tic() {
        double first = buffer.removeFirst();
        double second = buffer.get(1);
        double tmp = (first + second) * DECAY * 0.5;
        /*int operationNumber = StdRandom.uniform(0, 2);
        if(operationNumber == 0) {
            buffer.addLast(tmp);
        } else {
            buffer.addLast(-tmp);
        }*/
        if (StdRandom.bernoulli(0.5)) { // 以 50% 的概率翻转符号
            tmp = -tmp;
        }
        buffer.addLast(tmp);
    }

    public double sample() {
        return buffer.get(1);
    }


}
