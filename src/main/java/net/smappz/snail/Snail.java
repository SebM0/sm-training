package net.smappz.snail;

import java.util.*;

class Snail {
    private int m_data[][];
    private Direction m_direction = Direction.right;
    private Point m_position = new Point(0, 0);
    private final int m_size;

    Snail(int size) {
        m_size = size;
    }

    static String render(int size, int count) {
        Snail snail = new Snail(size);
        snail.buildData(count);
        return snail.renderData();
    }

    private String renderData() {
        StringBuilder sb = new StringBuilder();
        sb.append("<TABLE>\n");
        Arrays.stream(m_data).forEach(row -> {
            sb.append("<TR>");
            Arrays.stream(row).forEach(cell -> sb.append("<TD>").append(cell > 0 ? cell : "").append("</TD>"));
            sb.append("</TR>\n");
        });
        sb.append("</TABLE>");
        return sb.toString();
    }

    int[][] buildData() {
        return buildData(m_size* m_size);
    }

    private int[][] buildData(int count) {
        m_data = new int[m_size][m_size];

        for (int i = 1; i<= count; i++) {
            writeNumberAtCurrentPosition(i);
            performMove();
        }
        return m_data;
    }

    private void writeNumberAtCurrentPosition(int i) {
        m_data[m_position.getY()][m_position.getX()] = i;
    }

    private void performMove() {
        Point nextPosition = m_direction.stepForward(m_position);
        if (isOutOfBounds(nextPosition) || !isFree(nextPosition)) {
            m_direction = m_direction.nextDirection();
            nextPosition = m_direction.stepForward(m_position);
        }
        m_position = nextPosition;
    }

    private boolean isFree(Point position) {
        return m_data[position.getY()][position.getX()] == 0;
    }

    private boolean isOutOfBounds(Point position) {
        return position.getX() < 0 || position.getX() >= m_size || position.getY() < 0 || position.getY() >= m_size;
    }
}
