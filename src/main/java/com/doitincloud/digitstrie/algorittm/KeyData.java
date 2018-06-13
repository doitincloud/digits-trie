package com.doitincloud.digitstrie.algorittm;

public class KeyData<T> {

    private String key;

    private T data;

    public KeyData(String key, T data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
