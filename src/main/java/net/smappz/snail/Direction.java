package net.smappz.snail;

public enum Direction {
    right(1, 0), down(0, 1), left(-1, 0), top(0, -1);

    private final Point delta;
    Direction(int deltaX, int deltaY) {
        delta = new Point(deltaX, deltaY);
    }

    public Point stepForward(Point pos) {
        Point nextpos = new Point(pos);
        nextpos.add(delta);
        return nextpos;
    }

    public Direction nextDirection() {
        int nextOrdinal = this.ordinal() + 1;
        if (nextOrdinal >= Direction.values().length) {
            nextOrdinal = 0;
        }
        return Direction.values()[nextOrdinal];
    }
}
