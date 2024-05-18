package consistent_hashing;

import java.util.List;
import java.util.Optional;

public interface ConsistentHashing {

    void addServer(String serverId) throws ConsistentHashingException;

    void removeServer(String serverId) throws ConsistentHashingException;

    Optional<String> getServerForKey(String key) throws ConsistentHashingException;

}
