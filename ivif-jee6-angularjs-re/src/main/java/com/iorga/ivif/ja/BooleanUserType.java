package com.iorga.ivif.ja;

import com.google.common.reflect.TypeToken;
import org.hibernate.HibernateException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class BooleanUserType<J> extends SwitchTypeUserType<Boolean, J> {
    protected J trueValue;
    protected J falseValue;
    protected Class<J> jdbcClass;
    protected int sqlType;

    public BooleanUserType(J trueValue, J falseValue) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
        jdbcClass = (Class<J>) (trueValue != null ? trueValue.getClass() : (falseValue != null ? falseValue.getClass() : new TypeToken<J>(getClass()) {}.getRawType()));
        if (String.class.isAssignableFrom(jdbcClass)) {
            sqlType = Types.VARCHAR;
        } else {
            throw new UnsupportedOperationException("Can't handle " + jdbcClass.toString() + " yet.");
        }
    }

    @Override
    protected J getJdbcValue(Boolean value) {
        if (value == null) {
            return null;
        } else if (Boolean.TRUE.equals(value)) {
            return getTrueValue();
        } else {
            return getFalseValue();
        }
    }

    @Override
    protected int getSqlType() {
        return sqlType;
    }

    @Override
    protected J getFromResultSet(ResultSet rs, String columnName) throws SQLException {
        switch (getSqlType()) {
            case Types.VARCHAR:
                return (J) rs.getString(columnName);
            default:
                throw new UnsupportedOperationException("Can't handle " + getSqlType() + " yet.");
        }
    }

    @Override
    protected Boolean getSwitchValue(J value) {
        if (value == null) {
            return null;
        } else if (Objects.equals(getTrueValue(), value)) {
            return Boolean.TRUE;
        } else if (Objects.equals(getFalseValue(), value)) {
            return Boolean.FALSE;
        } else {
            throw new HibernateException("Given value was nor true nor false: "+value);
        }
    }

    public J getTrueValue() {
        return trueValue;
    }

    public J getFalseValue() {
        return falseValue;
    }
}
