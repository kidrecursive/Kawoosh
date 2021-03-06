package com.datastax.kawoosh.parser.fileReader;

import com.datastax.kawoosh.common.Tuple;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

public class ClusterInfoReader implements Reader {
    @Override
    public Stream<Tuple> read(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map info = mapper.readValue(new File(path), Map.class);
            return info.keySet().stream()
                    .map(key -> new Tuple(key, info.get(key) == null ? "" : info.get(key)));
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }
}
