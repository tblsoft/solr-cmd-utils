package de.tblsoft.solr.nlp.trie;

import de.tblsoft.solr.nlp.Phrase;

class Trie {
    private TrieNode root;

    private char[] ignoreChars = {' ', '-', '+'};

    Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;

        for (char l : word.toCharArray()) {
            if(containsChar(ignoreChars, l)) {
                continue;
            }
            current = current.getChildren().computeIfAbsent(l, c -> new TrieNode());
        }
        current.setEndOfWord(true);
        current.setValue(word);
    }

    boolean delete(String word) {
        return delete(root, word, 0);
    }




    boolean isEndToken(int position, String text) {
        if(text.length() <= position + 1) {
            return true;
        }
        char token = text.charAt(position+1);
        return token == ' ';
    }

    int getNextTokenSize(String text) {

        return 0;
    }

    int findNextTokenizerPosition(String text) {
        return findNextTokenizerPosition(text, 0);
    }

    int findNextTokenizerPosition(String text, int start) {
        for (int i = start; i < text.length(); i++) {
            char ch = text.charAt(i);
            if(ch == ' ') {
                return i+1;
            }
        }
        return -1;
    }


    Phrase getPhraseEndPosition(String word) {
        Phrase phrase = new Phrase();
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if(containsChar(ignoreChars, ch)) {
                continue;
            }
            TrieNode node = current.getChildren().get(ch);
            if (node == null) {
                return phrase;
            }
            current = node;
            if(current.isEndOfWord() && isEndToken(i, word)) {
                phrase.setEndPosition(i);
                phrase.setNormalizedText(current.getValue());
            }
        }
        return phrase;
    }

    boolean containsNode(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if(containsChar(ignoreChars, ch)) {
                continue;
            }
            TrieNode node = current.getChildren().get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isEndOfWord();
    }

    boolean isEmpty() {
        return root == null;
    }

    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord()) {
                return false;
            }
            current.setEndOfWord(false);
            return current.getChildren().isEmpty();
        }
        char ch = word.charAt(index);
        TrieNode node = current.getChildren().get(ch);
        if (node == null) {
            return false;
        }
        boolean shouldDeleteCurrentNode = delete(node, word, index + 1) && !node.isEndOfWord();

        if (shouldDeleteCurrentNode) {
            current.getChildren().remove(ch);
            return current.getChildren().isEmpty();
        }
        return false;
    }

    protected static boolean containsChar(char[] accept, char c) {
        for (int i = accept.length - 1; i >= 0; i--) {
            if (accept[i] == c) {
                return true;
            }
        }

        return false;
    }
}