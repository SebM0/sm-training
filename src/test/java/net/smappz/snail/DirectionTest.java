package net.smappz.snail;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for the ZZZ context.
 */
public class DirectionTest {

    /**
     * Tests set up.
     */
    @BeforeMethod(alwaysRun = true)
    public void init() {
        // You must initialize class members here.
        // Delete this method if not used. You can recreate it with command bddi<tab>
        // Use command bdd<tab> for test method creation
    }

    @Test
    public void testNextDirection() {
        Assert.assertEquals(Direction.right.nextDirection(), Direction.down);
        Assert.assertEquals(Direction.down.nextDirection(), Direction.left);
        Assert.assertEquals(Direction.left.nextDirection(), Direction.top);
        Assert.assertEquals(Direction.top.nextDirection(), Direction.right);
    }

    @Test
    public void testNextMove() {
        Point pos = new Point(3,3);
        Assert.assertEquals(Direction.right.stepForward(pos), new Point(4, 3));
        Assert.assertEquals(Direction.down.stepForward(pos), new Point(3, 4));
        Assert.assertEquals(Direction.left.stepForward(pos), new Point(2, 3));
        Assert.assertEquals(Direction.top.stepForward(pos), new Point(3, 2));
    }
}
