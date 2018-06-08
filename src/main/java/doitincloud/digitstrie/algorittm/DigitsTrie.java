package doitincloud.digitstrie.algorittm;

import java.util.LinkedHashMap;
import java.util.Map;

public class DigitsTrie<T> {

    private int size = 0;

    private TrieNode<T> root = new TrieNode<T>();

    public DigitsTrie() {
    }

    // load entries from map
    //
    public void load(Map<String, T> map) {
        synchronized (root) {
            map.forEach((k, v) -> put(k, v));
        }
    }

    // get all entries as map
    //
    public Map<String, T> all() {
        Map<String, T> map = new LinkedHashMap<String, T>();
        synchronized (root) {
            fetchAllThrough("", root, map);
        }
        return map;
    }

    public int size() {
        return size;
    }

    // add a key and data pair
    //
    public void put(String key, T data) {
        TrieNode p = root;
        for (int i = 0; i < key.length(); i++) {
            char digit = key.charAt(i);
            int index = digit - '0';
            if (index < 0 || index > 9) {
                continue;
            }
            TrieNode tmp = null;
            synchronized (p) {
                tmp = p.getChild(index);
                if (tmp == null) {
                    tmp = new TrieNode();
                    p.setChild(index, tmp);
                }
            }
            p = tmp;
        }
        if (!p.hasData()) {
            size++;
        }
        p.setData(data);
    }

    // remove a key
    //
    public boolean remove(String key) {
        Integer start = 0;
        synchronized (root) {
            return deleteEntryThrough(start, root, key);
        }
    }

    // returns true if any item starts with prefix
    //
    public boolean hasStartsWith(String prefix) {
        TrieNode p = root;
        for(int i = 0; i < prefix.length(); i++) {
            char digit = prefix.charAt(i);
            int index = digit - '0';
            if (index < 0 || index > 9) {
                continue;
            }
            p = p.getChild(index);
            if (p == null) {
                return false;
            }
        }
        if (p == root) {
            return false;
        }
        return true;
    }

    // count all items start with prefix
    //
    public int countStartWith(String prefix) {
        TrieNode p = root;
        for(int i = 0; i < prefix.length(); i++) {
            char digit = prefix.charAt(i);
            int index = digit - '0';
            if (index < 0 || index > 9) {
                continue;
            }
            p = p.getChild(index);
            if (p == null) {
                return 0;
            }
        }
        int startCount = 0;
        if (p.hasData()) {
            startCount = 1;
        }
        return startCount + countAllThrough(p);
    }

    // returns all items start with prefix
    //
    public Map<String, T> allStartWith(String prefix) {
        TrieNode p = root;
        for(int i = 0; i < prefix.length(); i++) {
            char digit = prefix.charAt(i);
            int index = digit - '0';
            if (index < 0 || index > 9) {
                continue;
            }
            p = p.getChild(index);
            if (p == null) {
                return null;
            }
        }
        Map<String, T> map = new LinkedHashMap<String, T>();
        if (p.hasData()) {
            map.put(prefix, (T) p.getData());
        }
        fetchAllThrough(prefix, p, map);
        return map;
    }

    // returns true if the exactly match exists
    //
    public T get(String key) {
        TrieNode p = root;
        for(int i = 0; i < key.length(); i++) {
            char digit = key.charAt(i);
            int index = digit - '0';
            if (index < 0 || index > 9) {
                continue;
            }
            p = p.getChild(index);
            if (p == null) {
                return null;
            }
        }
        if (p == root) {
            return null;
        }
        return (T) p.getData();
    }


    // returns the longest match with a digit sequence
    //
    public KeyData<T> longestMatch(String digits) {
        TrieNode p = root;
        StringBuffer start = new StringBuffer();
        String key = null;
        T data = null;
        for(int i=0; i < digits.length(); i++) {
            char digit = digits.charAt(i);
            int index = digit - '0';
            if (index < 0 || index > 9) {
                continue;
            }
            p = p.getChild(index);
            if (p == null) {
                break;
            }
            start.append(digit);
            if (p.hasData()) {
                key = start.toString();
                data = (T) p.getData();
            }
        }
        if (key == null) {
            return null;
        }
        return new KeyData<T>(key, data);
    }

    // walk through a node recursively, to count all items
    //
    private int countAllThrough(TrieNode node) {
        int count = 0;
        for (int i = 0; i < 10; i++) {
            TrieNode p = node.getChild(i);
            if (p == null) {
                continue;
            }
            if (p.hasData()) {
                count++;
            }
            count += countAllThrough(p);
        }
        return count;
    }

    // walk through a node recursively, to fetch all items
    //
    private void fetchAllThrough(String prefix, TrieNode node, Map<String, T> map) {
        for (int i = 0; i < 10; i++) {
            TrieNode p = node.getChild(i);
            if (p == null) {
                continue;
            }
            String key = prefix + i;
            if (p.hasData()) {
                map.put(key, (T) p.getData());
            }
            fetchAllThrough(key, p, map);
        }
    }

    // walk through a node recursively, to delete a key
    //
    private boolean deleteEntryThrough(Integer position, TrieNode node, String key) {
        if (position == key.length()) {
            if (node.hasData()) {
                node.setData(null);
                size--;
                return true;
            }
            return false;
        }
        char digit = key.charAt(position);
        position++;
        int index = digit - '0';
        if (index < 0 || index > 9) {
            return false;
        }
        TrieNode p = node.getChild(index);
        if (p == null) {
            return false;
        }
        if (deleteEntryThrough(position, p, key)) {
            boolean isEmpty = !p.hasData();
            if (isEmpty) {
                for (int i = 0; i < 10; i++) {
                    if (p.getChild(i) != null) {
                        isEmpty = false;
                        break;
                    }
                }
            }
            if (isEmpty) {
                node.setChild(index, null);
            }
            return true;
        }
        return false;
    }
}