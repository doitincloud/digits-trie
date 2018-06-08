package doitincloud.digitstrie.repositories;

import doitincloud.digitstrie.models.HitLog;

public interface HitLogRepo {

    void save(String table, String digits);

}
