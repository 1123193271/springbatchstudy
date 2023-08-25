package org.example._19_part_step_job;

import com.sun.xml.internal.bind.api.ClassResolver;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserPartitioner implements Partitioner {

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap<>(gridSize);
        int range = 10;
        int start = 1;
        int end = 10;
        String text = "user%s-%s.txt";
        for (int i = 0; i < gridSize; i++) {
            ExecutionContext value = new ExecutionContext();
            Resource resource = new ClassPathResource(String.format(text, start, end));
            try {
                value.putString("file", resource.getURL().toExternalForm());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            start += range;
            end += range;

            result.put("user_partition_" + i,value);
        }
        return result;
    }
}
