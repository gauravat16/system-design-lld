package trie;

public interface Trie {

    void insertWord(String word);

    boolean wordExists(String word);

    boolean deleteWord(String word);

    boolean startsWith(String word);

}
