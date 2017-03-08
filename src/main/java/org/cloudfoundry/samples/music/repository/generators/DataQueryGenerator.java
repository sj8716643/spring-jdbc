package org.cloudfoundry.samples.music.repository.generators;

import org.cloudfoundry.samples.music.domain.DataObject;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Profile({"postgres", "my-sql"})
public class DataQueryGenerator {

    private String table;
    private String field;
    private String value;
    private String condition;

    public DataQueryGenerator(String table) {
        this.table = table;
    }

    public String SELECT(DataObject dataObject) {
        return String.format("SELECT * FROM %s WHERE id='%s';", table, dataObject.getId());
    }

    public String SELECT(String id) {
        return String.format("SELECT * FROM %s WHERE id='%s';", table, id);
    }

    /**
     * Select all.
     * @return
     */
    public String SELECT() {
        return String.format("SELECT * FROM %s;", table);
    }

    public String INSERT(DataObject dataObject) {
        fieldBuilder(dataObject);
        // TODO : if target data has extra columns
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s);", table, field, value);
        System.out.println("sql = " + sql);
        return sql;
    }

    public String UPDATE(DataObject dataObject) {
        fieldBuilder(dataObject);
        // TODO : if target data has extra columns
        String sql = String.format("UPDATE %s SET (%s) = (%s) WHERE id=%s;", table, field, value, "'" + dataObject.getId() + "'");
        System.out.println("sql = " + sql);
        return sql;
    }

    public String DELETE(DataObject dataObject) {
        String sql = String.format("DELETE FROM %s WHERE id=%s;", table, "'" + dataObject.getId() + "'");
        System.out.println("sql = " + sql);
        return sql;
    }

    public String DELETE(String id) {
        String sql = String.format("DELETE FROM %s WHERE id=%s;", table, "'" + id + "'");
        System.out.println("sql = " + sql);
        return sql;
    }

    /**
     * Search all the fields if fields is null.
     * @param query
     * @param fields
     * @return
     */
    public String fuzzyQuery(String query, List<String> fields) {
        if(fields == null) {
            return ";";
        }
        List<String> list = new ArrayList<>();
        for(String field : fields) {
            list.add("LOWER(" + field + ")");
        }
        String col = String.join("||", list);
        // using %% to escape %
        String sql = String.format("SELECT * FROM %s WHERE (%s) LIKE LOWER('%%%s%%');", table, col, query);
        System.out.println("sql = " + sql);
        return sql;
    }


    private void fieldBuilder(DataObject dataObject) {
        StringBuffer field = new StringBuffer();
        StringBuffer value = new StringBuffer();
        field.append("id");
        value.append("'"+dataObject.getId()+"'");
        for(Map.Entry<String, Object> map : dataObject.getContent().entrySet()) {
                field.append("," + map.getKey());
                // in postgres, we use '' instead of '.
                value.append(",'" + map.getValue().toString().replace("'", "''") + "'");
        }
        this.field = field.toString();
        this.value = value.toString();
    }

    private void conditionBuilder(DataObject dataObject) {
        StringBuffer condition = new StringBuffer();
        for(Map.Entry<String, Object> map : dataObject.getContent().entrySet()) {
            condition.append("," + map.getKey() + "=" + map.getValue().toString());
        }
        condition.deleteCharAt(0);
        this.condition = condition.toString();
    }

}