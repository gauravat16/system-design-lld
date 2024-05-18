package consistent_hashing;

import java.security.MessageDigest;
import java.util.Optional;
import java.util.TreeMap;

public class ConsistentHashingImpl implements ConsistentHashing {

    private int virtualNodes;
    private TreeMap<Long, VirtualNode> hashRing;

    public ConsistentHashingImpl(int virtualNodes) {
        this.virtualNodes = virtualNodes;
        this.hashRing = new TreeMap<>();
    }

    @Override
    public void addServer(String serverId) throws ConsistentHashingException {


        for (int i = 0; i < virtualNodes; i++) {
            long index = getHashValue(serverId + i)
                    .orElseThrow(() -> new ConsistentHashingException("No hashvalue found"));

            if (hashRing.containsKey(index)) {
                throw new ConsistentHashingException("Server already added");
            }
            hashRing.putIfAbsent(index, new VirtualNode(serverId, serverId + i));
        }
//        System.out.println(hashRing);
    }

    @Override
    public void removeServer(String serverId) throws ConsistentHashingException {
        for (int i = 0; i < virtualNodes; i++) {
            long index = getHashValue(serverId + i)
                    .orElseThrow(() -> new ConsistentHashingException("No hashvalue found"));
            hashRing.remove(index);
        }
    }

    @Override
    public Optional<String> getServerForKey(String key) throws ConsistentHashingException {
        if (hashRing.isEmpty()) return Optional.empty();
        long index = getHashValue(key)
                .orElseThrow(() -> new ConsistentHashingException("No hashvalue found"));
        return Optional.ofNullable(Optional.ofNullable(hashRing.ceilingEntry(index))
                .orElse(hashRing.firstEntry())
                .getValue().serverId);
    }

    private Optional<Long> getHashValue(String key) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = sha1.digest(key.getBytes());

            return Optional.of(byteArrayToLong(hashBytes));
        } catch (Exception e) {
            System.out.println("Failed to get hash : " + e.getMessage());
        }
        return Optional.empty();
    }

    private long byteArrayToLong(byte[] bytes) {
        return ((long) (bytes[0] & 0xFF) << 56) |
                ((long) (bytes[1] & 0xFF) << 48) |
                ((long) (bytes[2] & 0xFF) << 40) |
                ((long) (bytes[3] & 0xFF) << 32) |
                ((long) (bytes[4] & 0xFF) << 24) |
                ((long) (bytes[5] & 0xFF) << 16) |
                ((long) (bytes[6] & 0xFF) << 8) |
                ((long) (bytes[7] & 0xFF));
    }


    private static class Node {
        protected String serverId;

        public Node(String serverId) {
            this.serverId = serverId;
        }
    }

    private static class VirtualNode extends Node {
        private String virtualServerId;

        public VirtualNode(String serverId, String virtualServerId) {
            super(serverId);
            this.virtualServerId = virtualServerId;
        }

        @Override
        public String toString() {
            return "VirtualNode{" +
                    "virtualServerId='" + virtualServerId + '\'' +
                    ", serverId='" + serverId + '\'' +
                    '}';
        }
    }

}
