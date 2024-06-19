package deque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        return max(this.comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxElement = get(0);
        for (int i = 1; i < arraySize(); i++) {
            T currentElement = get(i);
            if (currentElement != null) {
                if (maxElement == null) {
                    maxElement = currentElement;
                }
                else if (c.compare(currentElement, maxElement) > 0) {
                    maxElement = currentElement;
                }
            }
        }
        return maxElement;
    }
}

