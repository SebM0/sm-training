package net.smappz.snail;

import org.testng.Assert;
import org.testng.annotations.*;

/**
 * Unit tests for the ZZZ context.
 */
public class SnailTest {


    @Test
    public void test0() {
        Snail snail = new Snail(0);
        int[][] data = snail.buildData();
        Assert.assertEquals(data.length, 0);
    }

    @Test
    public void test1() {
        Snail snail = new Snail(1);
        int[][] data = snail.buildData();
        int[][] expected = {{1}};
        Assert.assertEquals(data, expected);
    }

    @Test
    public void test2() {
        Snail snail = new Snail(2);
        int[][] data = snail.buildData();
        int[][] expected = {{1, 2}, {4, 3}};
        Assert.assertEquals(data, expected);
    }

    @Test
    public void test3() {
        Snail snail = new Snail(3);
        int[][] data = snail.buildData();
        int[][] expected = {{1, 2, 3}, {8, 9, 4}, {7, 6, 5}};
        Assert.assertEquals(data, expected);
    }
}
