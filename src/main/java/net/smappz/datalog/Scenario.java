package net.smappz.datalog;

import java.util.*;
import java.util.function.*;

public interface Scenario {
    String getName();

    Predicate<Map.Entry<Integer,Integer>> getFilter();
}
