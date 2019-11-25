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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ping
 */
@MappedJdbcTypes({JdbcType.VARCHAR})
@MappedTypes({List.class})
public class ListTypeHandler extends BaseTypeHandler<List<String>> {

    private static final String DELIMITER = ",";

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int index, List<String> list, JdbcType jdbcType) throws SQLException {
        if (CollectionUtils.isEmpty(list)) {
            preparedStatement.setString(index, null);
            return;
        }
        String join = String.join(DELIMITER, list);
        preparedStatement.setString(index, join);
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, String label) throws SQLException {
        return str2List(resultSet.getString(label));
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, int index) throws SQLException {
        return str2List(resultSet.getString(index));
    }

    @Override
    public List<String> getNullableResult(CallableStatement callableStatement, int index) throws SQLException {
        return str2List(callableStatement.getString(index));
    }

    private List<String> str2List(String value) {
        if (value == null) {
            return null;
        } else if (value.isEmpty()) {
            return new ArrayList<>();
        }
        String[] strings = value.split(DELIMITER);
        return Arrays.asList(strings);
    }
}
