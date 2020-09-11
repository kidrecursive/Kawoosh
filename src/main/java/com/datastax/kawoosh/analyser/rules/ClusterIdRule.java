package com.datastax.kawoosh.analyser.rules;

import com.datastax.kawoosh.analyser.ClusterConfigRetriver;

import java.util.Arrays;
import java.util.List;

public class ClusterIdRule extends AggregatorRule {
    public ClusterIdRule(ClusterConfigRetriver clusterConfigRetriver) {
        super(clusterConfigRetriver, "ClusterId",

                Arrays.asList(new String[]{"bdp_version",
                        "cassandra_versions",
                        "cluster_cores",
                        "cluster_os",
                        "cluster_ram",
                        "dc_count",
                        "node_count",
                        "opscenter_version"})


        );



    }
}