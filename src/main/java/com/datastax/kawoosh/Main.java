package com.datastax.kawoosh;

import com.datastax.kawoosh.analyser.Analyser;
import com.datastax.kawoosh.analyser.ClusterConfigRetriver;
import com.datastax.kawoosh.analyser.rules.AutoBootStrapCheckRule;
import com.datastax.kawoosh.analyser.rules.AutoSnapshotCheckRule;
import com.datastax.kawoosh.analyser.rules.LargePartitionCheckRule;
import com.datastax.kawoosh.analyser.rules.Rule;
import com.datastax.kawoosh.analyser.rules.*;
import com.datastax.kawoosh.common.ClusterConfigBuilder;
import com.datastax.kawoosh.common.ClusterConfigImpl;
import com.datastax.kawoosh.dataStorageAdaptor.DataStorage;
import com.datastax.kawoosh.dataStorageAdaptor.MapStorage;
import com.datastax.kawoosh.parser.DirectoryParser;
import com.datastax.kawoosh.parser.OpsCenterGeneratedDiag;
import com.datastax.kawoosh.parser.fileReader.ClusterInfoReader;
import com.datastax.kawoosh.parser.fileReader.TableStatReader;
import com.datastax.kawoosh.parser.fileReader.YamlReader;

import java.net.ServerSocket;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        System.out.println(args[0]);
        System.out.println(args[1]);
        System.out.println(args[2]);
        System.out.println(args[3]);
        System.out.println(args[4]);

        YamlReader yamlReader = new YamlReader();
        TableStatReader tableStatReader = new TableStatReader();
        ClusterInfoReader clusterInfoReader = new ClusterInfoReader();
        DataStorage storage = new MapStorage();

        if(args[0].equals("Upload")){
            ClusterConfigBuilder builder = new ClusterConfigImpl(args[2], args[3], args[4], args[5]);
            DirectoryParser parser = new OpsCenterGeneratedDiag(args[1], builder, yamlReader, tableStatReader, clusterInfoReader);
            parser.readDiag().forEach(conf -> storage.write(conf));
        } else if(args[0].equals("Report")){
            ClusterConfigRetriver clusterConfigRetriver = new ClusterConfigRetriver(storage, args[1], args[2], args[3], args[4], args[5]);
            RuleBook ruleBook = new RuleBook(clusterConfigRetriver);
            Analyser analyser = new Analyser(ruleBook);
            analyser.analyse().forEach(s -> System.out.println("***********\n" + s));
        } else if(args[0].equals("Test")) {
            ClusterConfigBuilder builder = new ClusterConfigImpl(args[2], args[3], args[4], args[5]);
            DirectoryParser parser = new OpsCenterGeneratedDiag(args[1], builder, yamlReader, tableStatReader, clusterInfoReader);
            parser.readDiag().forEach(conf -> storage.write(conf));
            ClusterConfigRetriver clusterConfigRetriver = new ClusterConfigRetriver(storage, args[2], args[3], args[4], args[5], parser.getClusterName());
            RuleBook ruleBook = new RuleBook(clusterConfigRetriver);
            Analyser analyser = new Analyser(ruleBook);
            analyser.analyse().forEach(s -> System.out.println("***********\n" + s));
        } else {
            System.out.println(String.format("The task {0} was not recognised, please refer to the readme!", args[0]));
        }

        System.out.println("Done!");
    }
}
