package com.doitincloud.digitstrie.services;

import com.doitincloud.rdbcache.supports.Context;

import java.util.Map;
import java.util.Set;

public interface DigitsTrieServices {

    boolean hasTable(String table);

    void load(String table);

    Set<String> all(String table);

    public boolean add(String table, String key);

    public boolean remove(String table, String key);

    Map<String, Object> allValues(Context context, String table);

    public Map<String, Object> get(Context context, String table, String key);

    public Map<String, Object> longestMatch(Context context, String table, String key);

    public Map<String, Object> allStartWith(Context context, String table, String key, int limit);

    public Map<String, Object> save(Context context, String table, String key, Map<String, Object> map);

    public boolean delete(Context context, String table, String key);
}
