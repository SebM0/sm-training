package net.smappz.datalog;

import java.util.*;
import java.util.function.*;

public class UserScenario implements Scenario {
    private final int m_user;

    public UserScenario(int user) {
        m_user = user;
    }

    @Override
    public String getName() {
        return "_user" + m_user;
    }

    @Override
    public Predicate<Map.Entry<Integer, Integer>> getFilter() {
        return entry -> entry.getKey() == m_user;
    }
}
