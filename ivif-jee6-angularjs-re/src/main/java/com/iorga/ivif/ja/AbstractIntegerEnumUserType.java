package com.iorga.ivif.ja;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public abstract class AbstractIntegerEnumUserType<E extends Enum<E> & Valuable<Integer>> extends EnumUserType<E, Integer> {

    @Override
    protected Integer getFromResultSet(ResultSet rs, String columnName) throws SQLException {
        return rs.getInt(columnName);
    }

    @Override
    protected int getSqlType() {
        return Types.INTEGER;
    }
}
