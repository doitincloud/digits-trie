package com.doitincloud.digitstrie.repositories;

import com.doitincloud.digitstrie.algorittm.DigitsTrie;
import com.doitincloud.rdbcache.supports.Context;

import java.util.Map;

public interface DigitsValueRepo {

    void load(Context context, String table, DigitsTrie<Boolean> trie);

    public Map<String, Object> save(Context context, String table, String key, Map<String, Object> map);

    public boolean delete(Context context, String table, String key);

    public Map<String, Object> getMap(Context context, String table, String key);
}
