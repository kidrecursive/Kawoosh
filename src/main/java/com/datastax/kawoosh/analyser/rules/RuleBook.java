package com.datastax.kawoosh.analyser.rules;

import com.datastax.kawoosh.analyser.ClusterConfigRetriver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RuleBook {
    List<Rule> rules;

    public RuleBook(ClusterConfigRetriver clusterConfigRetriver) {
        this.rules =  new ArrayList<>();
        rules.add(new AutoBootStrapCheckRule(clusterConfigRetriver));
        rules.add(new AutoSnapshotCheckRule(clusterConfigRetriver));
        rules.add(new VnodeCheckRule(clusterConfigRetriver));
        rules.add(new ConcurrencyCheckRule(clusterConfigRetriver));
        rules.add(new CompactionCheckRule(clusterConfigRetriver));
        rules.add(new SeedListRule(clusterConfigRetriver));
        rules.add(new ClusterIdRule(clusterConfigRetriver));
        rules.add(new LargePartitionCheckRule(clusterConfigRetriver));
        rules.add(new NbOfSSTablesCheckRule(clusterConfigRetriver));
    }

    public Stream<Rule> getRules() {
        return rules.stream();
    }
}