package net.smappz.datalog;

import java.util.*;
import java.util.function.*;

public class TopMostScenario implements Scenario {
    @Override
    public String getName() {
        return "big";
    }

    @Override
    public Predicate<Map.Entry<Integer, Integer>> getFilter() {
        return entry -> entry.getValue() >= 365;
    }
}
