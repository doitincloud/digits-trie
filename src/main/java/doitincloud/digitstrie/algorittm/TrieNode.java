package doitincloud.digitstrie.algorittm;

public class TrieNode<T> {

    private TrieNode<T>[] children = new TrieNode[10];

    private T data;

    public TrieNode() {
    }

    public TrieNode(T data) {
        this.data = data;
    }

    synchronized public TrieNode getChild(int index) {
        return children[index];
    }

    synchronized public void setChild(int index, TrieNode child) {
        this.children[index] = child;
    }

    synchronized public boolean hasData() {
        return (data != null);
    }

    synchronized public void setData(T data) {
        this.data = data;
    }

    synchronized public T getData() {
        return data;
    }
}
