package com.doitincloud.digitstrie.services;

import com.doitincloud.digitstrie.algorittm.DigitsTrie;
import com.doitincloud.digitstrie.algorittm.KeyData;
import com.doitincloud.digitstrie.repositories.DigitsValueRepo;
import com.doitincloud.rdbcache.supports.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("digitsTrieServices")
public class DigitsTrieOps implements DigitsTrieServices {

    @Value("#{'${digits.value.tables}'.split(',')}")
    private List<String> tableList;

    @Autowired
    private DigitsValueRepo digitsValueRepo;

    private Map<String, DigitsTrie<Boolean>> trieMap = new HashMap<>();

    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        for (String table: tableList) {
            load(table);
        }
    }

    @Override
    public boolean hasTable(String table) {
        return tableList.contains(table);
    }

    @Override
    public void load(String table) {
        DigitsTrie<Boolean> digitsTrie = new DigitsTrie<>();
        digitsValueRepo.load(null, table, digitsTrie);
        trieMap.put(table, digitsTrie);
    }

    @Override
    public Set<String> all(String table) {
        DigitsTrie<Boolean> digitsTrie = trieMap.get(table);
        if (digitsTrie == null) {
            return null;
        }
        Map<String, Boolean> map = digitsTrie.all();
        return map.keySet();
    }

    @Override
    public boolean add(String table, String key) {
        DigitsTrie<Boolean> digitsTrie = trieMap.get(table);
        if (digitsTrie == null) {
            return false;
        }
        digitsTrie.put(key, true);
        return true;
    }

    @Override
    public boolean remove(String table, String key) {
        DigitsTrie<Boolean> digitsTrie = trieMap.get(table);
        return digitsTrie.remove(key);
    }

    @Override
    public Map<String, Object> allValues(Context context, String table) {
        Set<String> keys = all(table);
        if (keys == null) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        keys.forEach((k) -> {
            map.put(k, digitsValueRepo.getMap(context, table, k));
        });
        return map;
    }

    @Override
    public Map<String, Object> get(Context context, String table, String key) {
        DigitsTrie<Boolean> digitsTrie = trieMap.get(table);
        Boolean hasIt = digitsTrie.get(key);
        if (hasIt != null && hasIt) {
            return digitsValueRepo.getMap(context, table, key);
        }
        return null;
    }

    @Override
    public Map<String, Object> longestMatch(Context context, String table, String key) {
        DigitsTrie<Boolean> digitsTrie = trieMap.get(table);
        if (digitsTrie == null) {
            return null;
        }
        KeyData<Boolean> keyData =digitsTrie.longestMatch(key);
        if (keyData != null) {
            return digitsValueRepo.getMap(context, table, keyData.getKey());
        }
        return null;
    }

    @Override
    public Map<String, Object> allStartWith(Context context, String table, String key, int limit) {
        DigitsTrie<Boolean> digitsTrie = trieMap.get(table);
        if (digitsTrie == null) {
            return null;
        }
        Map<String, Boolean> keyMap = digitsTrie.allStartWith(key);
        if (keyMap == null) {
            return null;
        }
        Set<String> keys = keyMap.keySet();
        Map<String, Object> map = new LinkedHashMap<>();
        int i = 0;
        for (String k: keys) {
            if (limit != 0 && i == limit) {
                break;
            }
            map.put(k, digitsValueRepo.getMap(context, table, k));
            i++;
        }
        return map;
    }

    @Override
    public Map<String, Object> save(Context context, String table, String key, Map<String, Object> map) {
        DigitsTrie<Boolean> digitsTrie = trieMap.get(table);
        if (digitsTrie == null) {
            return null;
        }
        Map<String, Object> amp = digitsValueRepo.save(context, table, key, map);
        if (map != null) {
            digitsTrie.put(key, true);
        }
        return map;
    }

    @Override
    public boolean delete(Context context, String table, String key) {
        DigitsTrie<Boolean> digitsTrie = trieMap.get(table);
        if (digitsTrie == null) {
            return false;
        }
        boolean result = digitsValueRepo.delete(context, table, key);
        if (result) {
            digitsTrie.remove(key);
        }
        return result;
    }
}
