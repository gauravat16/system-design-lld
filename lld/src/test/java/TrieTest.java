import com.gaurav.trie.Trie;
import com.gaurav.trie.TrieIml;
import org.junit.Assert;
import org.junit.Test;

public class TrieTest {

    @Test
    public void testInsert() {
        Trie trie = new TrieIml();
        trie.insertWord("Hello");
        trie.insertWord("Hell");

//        Assert.assertTrue(trie.wordExists("Hello"));
//        Assert.assertTrue(trie.wordExists("Hell"));
//        Assert.assertFalse(trie.wordExists("He"));
        Assert.assertTrue(trie.wordExists("Hel.."));
    }
}
