package com.doitincloud.digitstrie.repositories;

import com.doitincloud.commons.Utils;
import com.doitincloud.digitstrie.algorittm.DigitsTrie;
import com.doitincloud.digitstrie.models.DigitsValue;
import com.doitincloud.rdbcache.configs.AppCtx;
import com.doitincloud.rdbcache.configs.PropCfg;
import com.doitincloud.rdbcache.models.KeyInfo;
import com.doitincloud.rdbcache.models.KvIdType;
import com.doitincloud.rdbcache.models.KvPair;
import com.doitincloud.rdbcache.supports.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("mapValueRepo")
public class DigitsValueRepoImpl implements DigitsValueRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HitLogRepo hitLogRepo;

    private static String indexKey = "digits";

    @Override
    public void load(Context context, String table, DigitsTrie<Boolean> trie) {

        String sql = "SELECT digits FROM " + table;
        List<String> list = jdbcTemplate.queryForList(sql, String.class);
        if (list == null || list.size() == 0) {
            throw new RuntimeException("Not found for table " + table);
        }
        for (String digits: list) {
            trie.put(digits, true);
        }
    }

    @Override
    public Map<String, Object> save(Context context, String table, String key, Map<String, Object> map) {

        KvPair pair = new KvPair(key, table);
        KeyInfo keyInfo = new KeyInfo(table, indexKey, key);
        keyInfo.setQueryKey("NOOPS");

        Map<String, Object> result = null;
        if (AppCtx.getDbaseRepo().find(context, pair, keyInfo)) {
            DigitsValue mapValue = Utils.toPojo(pair.getData(), DigitsValue.class);
            Map<String, Object> value = mapValue.getMapValue();
            for (Map.Entry<String, Object> entry: map.entrySet()) {
                value.put(entry.getKey(), entry.getValue());
            }
            result = mapValue.getMap();
            pair.setValue(Utils.toJson(mapValue));
            AppCtx.getRedisRepo().save(context, pair, keyInfo);
            Utils.getExcutorService().submit(() -> {
                AppCtx.getDbaseRepo().update(context, pair, keyInfo);
                AppCtx.getExpireOps().setExpireKey(context, pair, keyInfo);
            });
        } else {
            DigitsValue mapValue = new DigitsValue(key, map);
            result = mapValue.getMap();
            pair.setValue(Utils.toJson(mapValue));
            AppCtx.getRedisRepo().save(context, pair, keyInfo);
            Utils.getExcutorService().submit(() -> {
                AppCtx.getDbaseRepo().insert(context, pair, keyInfo);
                AppCtx.getExpireOps().setExpireKey(context, pair, keyInfo);
            });
        }

        return result;
    }

    @Override
    public boolean delete(Context context, String table, String key) {

        KvPair pair = new KvPair(key, table);
        KeyInfo keyInfo = new KeyInfo(table, indexKey, key);
        keyInfo.setQueryKey("NOOPS");

        boolean findIt = false;
        if (AppCtx.getRedisRepo().find(context, pair, keyInfo)) {
            findIt = true;
        } else if (AppCtx.getDbaseRepo().find(context, pair, keyInfo)) {
            findIt = true;
        }
        if (!findIt) {
            return findIt;
        }

        AppCtx.getRedisRepo().delete(context, pair, keyInfo);
        AppCtx.getDbaseRepo().delete(context, pair, keyInfo);
        return findIt;
    }

    @Override
    public Map<String, Object> getMap(Context context, String table, String key) {

        KvPair pair = new KvPair(key, table);
        KeyInfo keyInfo = new KeyInfo(table, indexKey, key);
        keyInfo.setQueryKey("NOOPS");

        if (AppCtx.getRedisRepo().find(context, pair, keyInfo)) {
            DigitsValue mapValue = Utils.toPojo(pair.getData(), DigitsValue.class);
            Utils.getExcutorService().submit(() -> {
                AppCtx.getExpireOps().setExpireKey(context, pair, keyInfo);
                hitLogRepo.save(table, key);
            });
            return mapValue.getMap();
        }
        if (AppCtx.getDbaseRepo().find(context, pair, keyInfo)) {
            AppCtx.getRedisRepo().save(context, pair, keyInfo);
            Utils.getExcutorService().submit(() -> {
                AppCtx.getExpireOps().setExpireKey(context, pair, keyInfo);
                hitLogRepo.save(table, key);
            });
            DigitsValue mapValue = Utils.toPojo(pair.getData(), DigitsValue.class);
            return mapValue.getMap();
        }

        return null;
    }
}
