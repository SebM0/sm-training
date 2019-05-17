package net.smappz.datalog;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static net.smappz.datalog.MarketPlace.*;

public class ClusterScenario implements Scenario {
    private final int m_cluster;
    private final Set<Integer> clusterUsers = new HashSet<>();

    public ClusterScenario(int cluster) {
        m_cluster = cluster;
        computeUserCluster();
    }

    @Override
    public String getName() {
        return "_" + m_cluster;
    }

    @Override
    public Predicate<Map.Entry<Integer, Integer>> getFilter() {
        return entry -> clusterUsers.contains(entry.getKey());
    }

    private void computeUserCluster() {

        Path orders = Paths.get(CLEAN_DATA_FOLDER, "user_clusters.csv");
        try (BufferedReader reader = Files.newBufferedReader(orders, ISO_8859_1)) {
            for (String line : reader.lines().collect(Collectors.toList())) {
                if (line.trim().length() == 0) {
                    continue;
                }
                String[] items = line.split(ITEM_SEPARATOR);
                int user_id = Integer.parseInt(items[0]);
                int cluster_id = Integer.parseInt(items[1]);
                if (m_cluster == cluster_id) {
                    clusterUsers.add(user_id);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        System.out.printf("User count for cluster %d: %d\n", m_cluster, clusterUsers.size());
    }
}
