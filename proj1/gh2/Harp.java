package gh2;

import deque.LinkedListDeque;
import deque.Deque;

public class Harp {


    private static final int SR = 44100;
    private static final double DECAY = .999;
    private int bufferSize;
    private Deque<Double> buffer;

    public Harp(double frequency) {
        bufferSize = (int) Math.round(SR / frequency) / 2;
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
        buffer.addLast(-tmp);
    }

    public double sample() {
        return buffer.get(1);
    }


}
