package com.prototype.beach

// TrieNode represents a node in the Trie
class TrieNode {
    val children: MutableMap<Char, TrieNode> = mutableMapOf()
    var isEndOfWord: Boolean = false  // Marks the end of a word
}

// Trie class that contains the root node and operations
class Trie {

    // TrieNode class definition remains unchanged
    class TrieNode {
        val children: MutableMap<Char, TrieNode> = mutableMapOf()
        var isEndOfWord: Boolean = false
    }

    private val root = TrieNode()

    // Insert a word into the Trie (always in lowercase)
    fun insert(word: String) {
        var current = root
        for (char in word.lowercase()) {  // Convert word to lowercase before inserting
            current = current.children.getOrPut(char) { TrieNode() }
        }
        current.isEndOfWord = true
    }

    // Search for words that start with the given prefix (always in lowercase)
    fun searchByPrefix(prefix: String): List<String> {
        val result = mutableListOf<String>()
        var current = root

        // Convert prefix to lowercase before searching
        for (char in prefix.lowercase()) {  // Case-insensitive search
            current = current.children[char] ?: return emptyList()  // Return empty if prefix not found
        }

        // Collect all words that start with this prefix
        collectAllWords(current, prefix.lowercase(), result)
        return result
    }

    // Helper function to collect all words starting from a given node
    private fun collectAllWords(node: TrieNode, prefix: String, result: MutableList<String>) {
        if (node.isEndOfWord) {
            result.add(prefix)  // Add the word to the result list if it's a complete word
        }
        for ((char, childNode) in node.children) {
            collectAllWords(childNode, prefix + char, result)  // Recursively collect words for each child node
        }
    }
}
