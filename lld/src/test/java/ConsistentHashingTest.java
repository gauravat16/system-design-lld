import com.gaurav.consistent_hashing.ConsistentHashing;
import com.gaurav.consistent_hashing.ConsistentHashingException;
import com.gaurav.consistent_hashing.ConsistentHashingImpl;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConsistentHashingTest {

    @Test
    public void test_consistentHashing() throws ConsistentHashingException {
        ConsistentHashing consistentHashing = new ConsistentHashingImpl(10000);
        consistentHashing.addServer("server_1");
        consistentHashing.addServer("server_2");
        consistentHashing.addServer("server_3");

        Map<String, Integer> map = new HashMap<>();

        for (int i = 0; i < 1000; i++) {
            String server = consistentHashing.getServerForKey(UUID.randomUUID().toString()).get();
            map.put(server, map.getOrDefault(server, 0) + 1);
        }

        System.out.println(map);


    }
}
