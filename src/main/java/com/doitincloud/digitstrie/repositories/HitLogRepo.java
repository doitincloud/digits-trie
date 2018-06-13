package com.doitincloud.digitstrie.repositories;

import com.doitincloud.digitstrie.models.HitLog;

public interface HitLogRepo {

    void save(String table, String digits);

}
