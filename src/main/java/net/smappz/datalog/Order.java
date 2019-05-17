package net.smappz.datalog;

public class Order {
    int order_id;
    int user_id;
    int order_dow;
    int order_hour_of_day;
    int days_since_prior_order;
    int days_until_next_order;
    int order_day = -1;
    int[] countByDepartment = new int[21];

    void addProduct(int department) {
        countByDepartment[department - 1]++;
    }
}
