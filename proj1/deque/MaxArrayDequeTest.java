package deque;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Comparator;
import java.util.Objects;

public class MaxArrayDequeTest {

    private MaxArrayDeque<Integer> maxDeque;

    @Before
    public void setUp() {
        Comparator<Integer> intComparator = Integer::compareTo;
        maxDeque = new MaxArrayDeque<>(intComparator);
    }

    @Test
    public void testMaxWithDefaultComparator() {
        maxDeque.addLast(1);
        maxDeque.addLast(3);
        maxDeque.addLast(2);
        assertEquals((Integer) 3, maxDeque.max());
    }

    @Test
    public void testMaxWithCustomComparator() {
        maxDeque.addLast(1);
        maxDeque.addLast(3);
        maxDeque.addLast(2);
        Comparator<Integer> reverseComparator = (a, b) -> b - a;
        assertEquals((Integer) 1, maxDeque.max(reverseComparator));
    }

    @Test
    public void testMaxWithEmptyDeque() {
        assertNull(maxDeque.max());
    }

    @Test
    public void testMaxWithCustomObjects() {
        MaxArrayDeque<Person> personDeque1 = new MaxArrayDeque<>(PersonComparators.byAge());
        personDeque1.addLast(new Person("Alice", 30));
        personDeque1.addLast(new Person("Bob", 25));
        personDeque1.addLast(new Person("Charlie", 35));
        assertEquals(new Person("Charlie", 35), personDeque1.max());

        MaxArrayDeque<Person> personDeque2 = new MaxArrayDeque<>(PersonComparators.byName());
        Comparator<Person> nameComparator = PersonComparators.byName();
        personDeque2.addLast(new Person("Charlie", 30));
        personDeque2.addLast(new Person("Bob", 25));
        personDeque2.addLast(new Person("Alice", 35));
        assertEquals(new Person("Charlie", 30), personDeque2.max(nameComparator));
    }
}

class Person {
    String name;
    int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public String toString() {
        return name + " (" + age + ")";
    }
}

class PersonComparators {
    public static Comparator<Person> byAge() {
        return Comparator.comparingInt(p -> p.age);
    }

    public static Comparator<Person> byName() {
        return Comparator.comparing(p -> p.name);
    }
}
