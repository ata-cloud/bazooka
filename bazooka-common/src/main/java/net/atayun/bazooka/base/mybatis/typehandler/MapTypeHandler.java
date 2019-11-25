package net.atayun.bazooka.base.mybatis.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.util.CollectionUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ping
 */
@MappedJdbcTypes({JdbcType.VARCHAR})
@MappedTypes({Map.class})
public class MapTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int index, Map<String, Object> map, JdbcType jdbcType) throws SQLException {
        if (CollectionUtils.isEmpty(map)) {
            preparedStatement.setString(index, null);
            return;
        }
        preparedStatement.setString(index, map2String(map));
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet resultSet, String label) throws SQLException {
        return str2Map(resultSet.getString(label));
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet resultSet, int index) throws SQLException {
        return str2Map(resultSet.getString(index));
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement callableStatement, int index) throws SQLException {
        return str2Map(callableStatement.getString(index));
    }

    private String map2String(Map<String, Object> map) {
        if (map == null) {
            return null;
        } else if (map.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        map.forEach((k, v) -> stringBuilder.append(k).append("=").append(v).append(","));
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    private Map<String, Object> str2Map(String value) {
        if (value == null) {
            return null;
        } else if (value.isEmpty()) {
            return new HashMap<>();
        }
        String[] kvArray = value.split(",");
        Map<String, Object> map = new HashMap<>();
        for (String kvString : kvArray) {
            String[] kv = kvString.split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }
}
