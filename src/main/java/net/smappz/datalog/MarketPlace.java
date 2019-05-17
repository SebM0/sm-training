package net.smappz.datalog;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.*;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class MarketPlace {
    private static final String SRC_DATA_FOLDER = "D:\\Backup\\HW 2019-05\\instacart_2017_05_01";
    static final String CLEAN_DATA_FOLDER = "D:\\Backup\\HW 2019-05\\data";
    static final String ITEM_SEPARATOR = ",";
    public static void main(String[] args) {
        //checkOrders();
        //checkProducts();
        //checkOrderUnicity();
        //computeUserCluster();
        Scenario scenario = new UserScenario(137629);
        checkScenario(scenario);
        computeUserStats(scenario);
    }


    private static void computeUserCluster() {
        Map<Integer,Integer> countByCluster = new HashMap<>();
        Path orders = Paths.get(CLEAN_DATA_FOLDER, "user_clusters.csv");
        try (BufferedReader reader = Files.newBufferedReader(orders, ISO_8859_1)) {
            for (String line : reader.lines().collect(Collectors.toList())) {
                if (line.trim().length() == 0) {
                    continue;
                }
                String[] items = line.split(ITEM_SEPARATOR);
                int cluster_id = Integer.parseInt(items[1]);
                int count = countByCluster.getOrDefault(cluster_id, 0);
                countByCluster.put(cluster_id, ++count);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        countByCluster.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("User count for cluster %d: %d\n", entry.getKey(), entry.getValue()));
    }
    private static void computeUserStats(Scenario scenario) {
        String scenarioName = scenario.getName();
        Map<Integer, User> users = new HashMap<>();
        TreeMap<Integer,Order> orders = new TreeMap<>();
        Map<Integer, User> userByOrder = new HashMap<>();
        Path ordersFile = Paths.get(CLEAN_DATA_FOLDER, "orders_scenario" + scenarioName + ".csv");
        try (BufferedReader reader = Files.newBufferedReader(ordersFile, ISO_8859_1)) {
            Order lastOrder = null;
            for (String line : reader.lines().collect(Collectors.toList())) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (Character.isDigit(line.charAt(0))) {
                    String[] items = line.split(ITEM_SEPARATOR);
                    int order_id = Integer.parseInt(items[0]);
                    int order_day = Integer.parseInt(items[1]);
                    int user_id = Integer.parseInt(items[4]);
                    User user = users.get(user_id);
                    if (user == null) {
                        user = new User();
                        users.put(user_id, user);
                        lastOrder = null;
                    }
                    user.orders.add(order_id);
                    userByOrder.put(order_id, user);

                    Order order = new Order();
                    order.order_id = order_id;
                    order.user_id = user_id;
                    order.order_day = order_day;
                    if (lastOrder != null) {
                        lastOrder.days_until_next_order = order.order_day - lastOrder.order_day;
                    }
                    orders.put(order_id, order);
                    lastOrder = order;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        // write stats
        System.out.printf("\nNumber of customers: %d\n", users.size());

        Map<Integer, Integer> departmentByProduct = new HashMap<>();
        Path products = Paths.get(SRC_DATA_FOLDER, "products.csv");
        try (BufferedReader reader = Files.newBufferedReader(products, ISO_8859_1)) {
            for (String line : reader.lines().collect(Collectors.toList())) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (Character.isDigit(line.charAt(0))) {
                    String[] items = line.split(ITEM_SEPARATOR);
                    int product_id = Integer.parseInt(items[0]);
                    int department_id = Integer.parseInt(items[items.length-1]);
                    departmentByProduct.put(product_id, department_id);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Path orderDetails1 = Paths.get(CLEAN_DATA_FOLDER, "order_products" + scenarioName + ".csv");
        try (BufferedReader reader = Files.newBufferedReader(orderDetails1, ISO_8859_1)) {
            for (String line : reader.lines().collect(Collectors.toList())) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (Character.isDigit(line.charAt(0))) {
                    String[] items = line.split(ITEM_SEPARATOR);
                    int order_id = Integer.parseInt(items[0]);
                    int product_id = Integer.parseInt(items[1]);
                    User user = userByOrder.get(order_id);
                    int department_id = departmentByProduct.get(product_id);
                    user.addProduct(department_id);
                    Order order = orders.get(order_id);
                    order.addProduct(department_id);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // write stats
        Path orderScenario = Paths.get(CLEAN_DATA_FOLDER, "stats_orders_byUser" + scenarioName + ".csv");
        try (BufferedWriter writer = Files.newBufferedWriter(orderScenario, ISO_8859_1, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("user_id,order_count,product_count");
            writer.newLine();
            users.entrySet().stream().sorted((o1, o2) -> Integer.compare(o2.getValue().orders.size(), o1.getValue().orders.size())).forEach(entry -> {
                try {
                    writer.write(String.format("%d,%d,%d", entry.getKey(), entry.getValue().orders.size(), entry.getValue().productCount));
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);  // TODO handle exception
                }
            });
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        orderScenario = Paths.get(CLEAN_DATA_FOLDER, "stats_department_byUser" + scenarioName + ".csv");
        String departments = getDepartments();
        try (BufferedWriter writer = Files.newBufferedWriter(orderScenario, ISO_8859_1, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("user_id" + departments);
            writer.newLine();
            users.entrySet().stream().sorted((o1, o2) -> Integer.compare(o2.getValue().orders.size(), o1.getValue().orders.size())).forEach(entry -> {
                StringBuilder line = new StringBuilder();
                line.append(entry.getKey());
                for (int i=0; i<21; i++) {
                    line.append(',').append(entry.getValue().countByDepartment[i]);
                }
                try {
                    writer.write(line.toString());
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);  // TODO handle exception
                }
            });
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        orderScenario = Paths.get(CLEAN_DATA_FOLDER, "department_byOrder" + scenarioName + ".csv");
        try (BufferedWriter writer = Files.newBufferedWriter(orderScenario, ISO_8859_1, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("order_id,days_until_next_order" + departments);
            writer.newLine();
            orders.entrySet().stream().sorted((e1, e2) -> Integer.compare(e1.getValue().order_day, e2.getValue().order_day)).forEach(entry -> {
                StringBuilder line = new StringBuilder();
                line.append(entry.getKey());
                line.append(',');
                line.append(entry.getValue().days_until_next_order);
                for (int i = 0; i < 21; i++) {
                    line.append(',').append(entry.getValue().countByDepartment[i]);
                }
                try {
                    writer.write(line.toString());
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);  // TODO handle exception
                }
            });
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String getDepartments() {
        StringBuilder dep = new StringBuilder();
        Path orderDetails2 = Paths.get(SRC_DATA_FOLDER, "departments.csv");
        try (BufferedReader reader = Files.newBufferedReader(orderDetails2, ISO_8859_1)) {
            for (String line : reader.lines().collect(Collectors.toList())) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (Character.isDigit(line.charAt(0))) {
                    String[] items = line.split(ITEM_SEPARATOR);
                    dep.append(ITEM_SEPARATOR);
                    dep.append(items[1]);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return dep.toString();
    }

    private static void checkOrderUnicity() {
        Set<Integer> orderSet = new HashSet<>();
        List<Integer> duplicatedSet = new ArrayList<>();
        Path orders = Paths.get(CLEAN_DATA_FOLDER, "orders.csv");
        try (BufferedReader reader = Files.newBufferedReader(orders, ISO_8859_1)) {
            reader.lines().filter(line -> line.trim().length() > 0) //
                    .skip(1) // skip header
                    .map(line -> line.split(ITEM_SEPARATOR)) //
                    .filter(tokens -> tokens.length > 0) //
                    .map(args -> Integer.parseInt(args[0])) //
                    .forEach(index -> {
                        if (orderSet.contains(index)) {
                            duplicatedSet.add(index);
                        } else {
                            orderSet.add(index);
                        }
                    });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (duplicatedSet.isEmpty()) {
            System.out.print("No duplicated order id\n");
        } else {
            duplicatedSet.forEach(index -> System.out.printf("Duplicated order id %d\n", index));
        }
    }

    private static void checkScenario(Scenario scenario) {
        Map<Integer, Integer> lengthByUser = new HashMap<>();
        List<Order> orderList = new ArrayList<>();
        int currentUser = -1;
        int currentLength = 0;
        Path orders = Paths.get(CLEAN_DATA_FOLDER, "orders.csv");
        try (BufferedReader reader = Files.newBufferedReader(orders, ISO_8859_1)) {
            for (String line : reader.lines().collect(Collectors.toList())) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (Character.isDigit(line.charAt(0))) {
                    String[] items = line.split(ITEM_SEPARATOR);
                    Order order = new Order();
                    order.order_id = Integer.parseInt(items[0]);
                    order.user_id = Integer.parseInt(items[1]);
                    order.order_dow = Integer.parseInt(items[4]);
                    order.order_hour_of_day = Integer.parseInt(items[5]);
                    order.days_since_prior_order = Integer.parseInt(items[6]);
                    orderList.add(order);
                    if (currentUser != -1 && currentUser != order.user_id) {
                        lengthByUser.putIfAbsent(currentUser, currentLength);
                        currentLength = 0;
                    }
                    currentUser = order.user_id;
                    currentLength += order.days_since_prior_order;
                }
            }
            lengthByUser.putIfAbsent(currentUser, currentLength);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // design scenario (max length = 365)
        // gaussian repartition over 52 weeks of year
        currentUser = -1;
        int lastDay = 0;
        for (Order order : orderList) {
            if (currentUser != order.user_id) {
                int length = lengthByUser.get(order.user_id);
                int startWeek = ((365 - length) / 2) / 7;
                lastDay = startWeek * 7 + order.order_dow;
                currentUser = order.user_id;
            }
            lastDay += order.days_since_prior_order;
            order.order_day = lastDay;
        }
        // write scenario
        Map<Integer, Integer> scenarioLengthByUser = lengthByUser.entrySet().stream().sorted((o1, o2) -> (o1.getValue() == o2.getValue()) ? Integer.compare(o2.getKey(), o1.getKey()) : Integer.compare(o2.getValue(), o1.getValue())
        ).filter(scenario.getFilter()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        orderList = orderList.stream().filter(order -> scenarioLengthByUser.containsKey(order.user_id)).collect(Collectors.toList());
        orderList.sort(Comparator.comparingInt(a -> a.order_day * 100 + a.order_hour_of_day));
        int[] statsByMonth = new int[12];
        Path orderScenario = Paths.get(CLEAN_DATA_FOLDER, "orders_scenario" + scenario.getName() + ".csv");
        try (BufferedWriter writer = Files.newBufferedWriter(orderScenario, ISO_8859_1, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("order_id,order_day,order_hour_of_day,order_dow,user_id");
            writer.newLine();
            for (Order order : orderList) {
                writer.write(String.format("%d,%d,%d,%d,%d", order.order_id, order.order_day, order.order_hour_of_day, order.order_dow, order.user_id));
                writer.newLine();
                int month = order.order_day / 31;
                statsByMonth[month] ++;
            }
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        System.out.print("Orders repartition: ");
        Arrays.stream(statsByMonth).forEach(count -> System.out.printf(" - %d",count));

        //write order details
        Map<Integer,Order> orderMap = orderList.stream().collect(Collectors.toMap(x -> x.order_id, x -> x));
        Path orderDetails1 = Paths.get(SRC_DATA_FOLDER, "order_products__train.csv");
        Path orderDetails2 = Paths.get(SRC_DATA_FOLDER, "order_products__prior.csv");
        Path cleanOrderDetails1 = Paths.get(CLEAN_DATA_FOLDER, "order_products" + scenario.getName() + ".csv");
        try (BufferedWriter writer = Files.newBufferedWriter(cleanOrderDetails1, ISO_8859_1, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            try (BufferedReader reader = Files.newBufferedReader(orderDetails1, ISO_8859_1)) {
                for (String line : reader.lines().collect(Collectors.toList())) {
                    if (line.trim().length() == 0) {
                        continue;
                    }
                    if (Character.isDigit(line.charAt(0))) {
                        String[] items = line.split(ITEM_SEPARATOR);
                        Order order = orderMap.get(Integer.parseInt(items[0]));
                        if (order != null) {
                            String cleanedLine = String.format("%s,%d,%d", line, order.order_day, order.order_hour_of_day);
                            writer.write(cleanedLine);
                            writer.newLine();
                        }
                    } else {
                        writer.write(line + ",order_day,order_hour_of_day");
                        writer.newLine();
                    }
                }
            }
            try (BufferedReader reader = Files.newBufferedReader(orderDetails2, ISO_8859_1)) {
                for (String line : reader.lines().collect(Collectors.toList())) {
                    if (line.trim().length() == 0) {
                        continue;
                    }
                    if (Character.isDigit(line.charAt(0))) {
                        String[] items = line.split(ITEM_SEPARATOR);
                        Order order = orderMap.get(Integer.parseInt(items[0]));
                        if (order != null) {
                            String cleanedLine = String.format("%s,%d,%d", line, order.order_day, order.order_hour_of_day);
                            writer.write(cleanedLine);
                            writer.newLine();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void checkProducts() {
        Path products = Paths.get(SRC_DATA_FOLDER, "products.csv");
        Set<Integer> concreteProducts = readIndexes(products, 0);
        System.out.printf("Referenced products: %d\n", concreteProducts.size());

        Path orderDetails1 = Paths.get(SRC_DATA_FOLDER, "order_products__train.csv");
        Set<Integer> trainProducts = readIndexes(orderDetails1, 1);
        //trainProducts.removeAll(concreteProducts);
        //System.out.printf("Unlinked products in train: %d\n", trainProducts.size());

        Path orderDetails2 = Paths.get(SRC_DATA_FOLDER, "order_products__prior.csv");
        Set<Integer> priorProducts = readIndexes(orderDetails2, 1);
        //priorProducts.removeAll(concreteProducts);
        //System.out.printf("Unlinked products in prior: %d\n", priorProducts.size());

        concreteProducts.removeAll(trainProducts);
        concreteProducts.removeAll(priorProducts);
        System.out.printf("Useless products: %d\n", concreteProducts.size());
    }

    private static void checkOrders() {
        Set<Integer> concreteOrder = new HashSet<>();
        Path orderDetails1 = Paths.get(SRC_DATA_FOLDER, "order_products__train.csv");
        concreteOrder.addAll(readIndexes(orderDetails1, 0));
        Path orderDetails2 = Paths.get(SRC_DATA_FOLDER, "order_products__prior.csv");
        concreteOrder.addAll(readIndexes(orderDetails2, 0));

        Path orders = Paths.get(SRC_DATA_FOLDER, "orders.csv");
        cleanOrders(orders, concreteOrder);
    }

    private static Set<Integer> readIndexes(Path csvFile, int index) {
        Set<Integer> concreteOrder = new HashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(csvFile, ISO_8859_1)) {
            reader.lines().filter(line -> line.trim().length() > 0) //
                    .skip(1) // skip header
                    .map(line -> line.split(ITEM_SEPARATOR)) //
                    .filter(tokens -> tokens.length > 0) //
                    .forEach(args -> concreteOrder.add(Integer.parseInt(args[index])));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return concreteOrder;
    }

    private static void cleanOrders(Path csvFile, Set<Integer> concreteOrder) {
        Map<String,Integer> skippedTypes = new HashMap<>();
        Path cleanOrders = Paths.get(CLEAN_DATA_FOLDER, csvFile.getFileName().toString());
        try (BufferedWriter writer = Files.newBufferedWriter(cleanOrders, ISO_8859_1, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                BufferedReader reader = Files.newBufferedReader(csvFile,ISO_8859_1)) {
            for (String line : reader.lines().collect(Collectors.toList())) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (Character.isDigit(line.charAt(0))) {
                    String[] items = line.split(ITEM_SEPARATOR);
                    int order = Integer.parseInt(items[0]);
                    if (concreteOrder.contains(order)) {
                        String cleanedLine = line.endsWith(".0") ? line.substring(0, line.length()-2) : line;
                        if (items.length == 6 || (items.length == 7 && items[6].trim().isEmpty())) {
                            cleanedLine += "0";
                        }
                        writer.write(cleanedLine);
                        writer.newLine();
                    } else {
                        String type = items[2];
                        skippedTypes.putIfAbsent(type, 0);
                        skippedTypes.compute(type, (__, count) -> count + 1);
                    }
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        skippedTypes.forEach( (type, count) -> System.out.printf("Unlinked orders: %s - %d", type, count));
    }
}
