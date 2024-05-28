package trie;

import java.util.HashMap;
import java.util.Map;

public class TrieIml implements Trie {

    private TrieNode root;

    public TrieIml() {
        this.root = new TrieNode(false, '*');
    }

    @Override
    public void insertWord(String word) {

        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            if (!curr.children.containsKey(c)) {
                curr.children.put(c, new TrieNode(false, c));
            }
            curr = curr.children.get(c);
        }

        curr.isWord = true;
    }

    @Override
    public boolean wordExists(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            if (!curr.children.containsKey(c)) {
                return false;
            }
            curr = curr.children.get(c);
        }
        return curr.isWord;
    }

    @Override
    public boolean deleteWord(String word) {
        TrieNode curr = root;
        if (wordExists(word)){
            for (char c : word.toCharArray()){
                curr = curr.children.get(c);
            }
        }else {
            return false;
        }
        curr.isWord = false;
        return true;
    }

    @Override
    public boolean startsWith(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            if (!curr.children.containsKey(c)) {
                return false;
            }
            curr = curr.children.get(c);
        }
        return curr != null;
    }

    private static class TrieNode {
        private Map<Character, TrieNode> children;
        private boolean isWord;
        private char value;

        public TrieNode(boolean isWord, char value) {
            this.isWord = isWord;
            this.value = value;
            this.children = new HashMap<>();
        }

        public void addChild(char child, TrieNode childNode) {
            children.put(child, childNode);
        }
    }
}
