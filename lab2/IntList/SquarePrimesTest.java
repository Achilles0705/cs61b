package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testIsPrime(){
        assertTrue("2 is a prime number", Primes.isPrime(2));
        assertTrue("3 is a prime number", Primes.isPrime(3));
        assertTrue("5 is a prime number", Primes.isPrime(5));
        assertTrue("17 is a prime number", Primes.isPrime(17));
        assertTrue("19 is a prime number", Primes.isPrime(19));
        assertTrue("23 is a prime number", Primes.isPrime(23));

        // Test with some known non-prime numbers
        assertFalse("1 is not a prime number", Primes.isPrime(1));
        assertFalse("4 is not a prime number", Primes.isPrime(4));
        assertFalse("6 is not a prime number", Primes.isPrime(6));
        assertFalse("15 is not a prime number", Primes.isPrime(15));
        assertFalse("20 is not a prime number", Primes.isPrime(20));

        // Test with some edge cases
        assertFalse("0 is not a prime number", Primes.isPrime(0));
        assertFalse("-1 is not a prime number", Primes.isPrime(-1));
        assertFalse("-17 is not a prime number", Primes.isPrime(-17));
    }

    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(1, 1, 3, 5, 5 ,6);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("1 -> 1 -> 9 -> 25 -> 25 -> 6", lst.toString());
        assertTrue(changed);
    }
}
