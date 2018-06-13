package com.doitincloud.digitstrie.repositories;

import com.doitincloud.digitstrie.models.HitLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository("hitLog")
public class HitLogImpl implements HitLogRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(String table, String digits) {

        String sql = "INSERT INTO hit_log (name, digits) values (?, ?)";
        List params = Arrays.asList(table, digits);
        jdbcTemplate.update(sql, params.toArray());

    }
}
