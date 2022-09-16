package main.compression;

import java.util.*;
import java.io.ByteArrayOutputStream; // Optional

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    // TreeMap chosen here just to make debugging easier
    private TreeMap<Character, String> encodingMap;        
    // Character that represents the end of a compressed transmission
    private static final char ETB_CHAR = 23;
    private String decodedLetters = "";
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * 
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    public Huffman (String corpus) {
        TreeMap<Character, Integer> charsMap =  buildDistMap(corpus);
        PriorityQueue<HuffNode> nodeQueue = new PriorityQueue<HuffNode>();
        encodingMap = new TreeMap <Character, String>();
        for (Map.Entry<Character, Integer> entry : charsMap.entrySet()) {
            nodeQueue.add(new HuffNode( entry.getKey(), entry.getValue()));
        }
        while (nodeQueue.size() > 1 ) {
            HuffNode zeroBit = nodeQueue.poll(); 
            HuffNode oneBit =  nodeQueue.poll(); 
            HuffNode addedNode = new HuffNode('-', zeroBit.count + oneBit.count); 
            addedNode.zeroChild = zeroBit;
            addedNode.oneChild = oneBit;
            nodeQueue.add(addedNode);
        }
        trieRoot = nodeQueue.poll(); 
        String bits = "";
        buildEncodingMap(trieRoot, bits);
    }
    
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * 
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as:
     *         (1) the bitstring containing the message itself, (2) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String bits = "";
        if(message.length() == 0) {
            output.write((byte) 0b00000000);
            return output.toByteArray();
        }
        for (int i = 0; i < message.length(); i++) {
            bits += encodingMap.get(message.charAt(i));
        }
        bits += encodingMap.get(ETB_CHAR);
        while (bits.length() % 8 != 0) {
            bits += '0';
        }
        for (int i = 0; i < bits.length(); i += 8) {
            output.write((byte) (Integer.parseInt(bits.substring(i, i + 8), 2)));
        }
        return output.toByteArray();
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * 
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as:
     *        (1) the bitstring containing the message itself, (2) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
        String allBits ="";
        String eightBits = "";
        for (int i =0; i < compressedMsg.length; i++) {
            eightBits = Integer.toBinaryString(compressedMsg[i] & 0xff);
            if (eightBits.length() != 8) {
                eightBits = "0" + eightBits;
            }
            allBits += eightBits;
        }
        findChar(trieRoot, allBits);
        return decodedLetters;
    }
    
    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left (0) and right (1) child), contains
     * a character field that it represents, and a count field that holds the 
     * number of times the node's character (or those in its subtrees) appear 
     * in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode zeroChild, oneChild;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () {
            return this.zeroChild == null && this.oneChild == null;
        }
        
        public int compareTo (HuffNode other) {
            if (this.count == other.count) {
                return this.character - other.character;
            }
            return this.count - other.count;
        }
    }
    
    
    /**
     * A private helper method that constructs a treeMap of the distribution of characters in the given corpus.
     * @param corpus A String representing a message / document corpus with distributions over characters.
     * @return charMap a TreeMap that shows the distribution of characters in the given corpus.
     */
    private static TreeMap<Character, Integer> buildDistMap(String corpus) {
        TreeMap<Character, Integer> charMap = new TreeMap<Character, Integer>();
        charMap.put(ETB_CHAR, 1);
        for (int i = 0; i < corpus.length(); i++) {
            if (!charMap.containsKey(corpus.charAt(i))) {
                charMap.put(corpus.charAt(i), 1);
            }
            else {
                charMap.replace(corpus.charAt(i), charMap.get(corpus.charAt(i)) + 1);
            }
        } 
        return charMap;
    }
    
    
    /**
     * A private recursive helper method that builds the TreeMap encodingMap.
     * @param curNode The HuffNode that the method is currently focused on.
     * @param bits String of bits that represent encoded letters.
     */
    private void buildEncodingMap(HuffNode curNode, String bits ) {
        if (curNode.isLeaf()) {
            encodingMap.put(curNode.character, bits);
            return;
        }
        buildEncodingMap(curNode.zeroChild, bits + '0');
        buildEncodingMap(curNode.oneChild, bits + '1');
        return;
    }
    
    
    /**
     * A recursive private helper method for the decompress method. Traces through the bits and reconstructs
     * the encoded string which is saved in the decodedLetters variable;
     * @param currNode  The HuffNode that the method is currently focused on.
     * @param bytes String of bits that represent encoded letters.
     */
    private void findChar(HuffNode currNode, String bytes) {
        if (currNode.character == ETB_CHAR) { return; }
        if (currNode.isLeaf()) {
            decodedLetters += currNode.character;
            findChar(trieRoot, bytes);
            return;
        }
        if (bytes.charAt(0) == '0') {
            bytes = bytes.substring(1);
            findChar(currNode.zeroChild, bytes);
            return;
        }
        if (bytes.charAt(0) == '1') {
            bytes = bytes.substring(1);
            findChar(currNode.oneChild, bytes);
            return;
        } 
        return;
    }
}
