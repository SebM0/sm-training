package net.smappz.datalog;

import java.util.*;

public class User {
    List<Integer> orders = new ArrayList<>();
    int[] countByDepartment = new int[21];
    int productCount = 0;

    void addProduct(int department) {
        productCount++;
        countByDepartment[department - 1]++;
    }
}
