package com.iorga.ivif.ja;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public abstract class AbstractStringEnumUserType<E extends Enum<E> & Valuable<String>> extends EnumUserType<E, String> {

    @Override
    protected String getFromResultSet(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }

    @Override
    protected int getSqlType() {
        return Types.VARCHAR;
    }
}
