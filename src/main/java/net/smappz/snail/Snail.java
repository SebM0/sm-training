package net.smappz.snail;

import java.util.*;

class Snail {
    private int m_data[][];
    private Direction m_currentDirection = Direction.right;
    private Point m_currentPosition = new Point(0, 0);
    private final int m_size;

    Snail(int size) {
        m_size = size;
    }

    static String render(int n) {
        Snail snail = new Snail(n);
        snail.buildData();
        return snail.renderData();
    }

    private String renderData() {
        StringBuilder sb = new StringBuilder();
        sb.append("<TABLE>\n");
        Arrays.stream(m_data).forEach(row -> {
            sb.append("<TR>");
            Arrays.stream(row).forEach(cell -> sb.append("<TD>").append(cell).append("</TD>"));
            sb.append("</TR>\n");
        });
        sb.append("</TABLE>");
        return sb.toString();
    }

    int[][] buildData() {
        m_data = new int[m_size][m_size];

        for (int i = 1; i<= m_size* m_size; i++) {
            writeNumberAtCurrentPosition(i);
            performMove();
        }
        return m_data;
    }

    private void writeNumberAtCurrentPosition(int i) {
        m_data[m_currentPosition.getY()][m_currentPosition.getX()] = i;
    }

    private void performMove() {
        Point nextPosition = m_currentDirection.stepForward(m_currentPosition);
        if (isOutOfBounds(nextPosition) || !isFree(nextPosition)) {
            m_currentDirection = m_currentDirection.nextDirection();
            nextPosition = m_currentDirection.stepForward(m_currentPosition);
        }
        m_currentPosition = nextPosition;
    }

    private boolean isFree(Point position) {
        return m_data[position.getY()][position.getX()] == 0;
    }

    private boolean isOutOfBounds(Point position) {
        return position.getX() < 0 || position.getX() >= m_size || position.getY() < 0 || position.getY() >= m_size;
    }
}
