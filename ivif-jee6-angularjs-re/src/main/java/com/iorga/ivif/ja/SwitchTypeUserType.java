package com.iorga.ivif.ja;

import com.google.common.reflect.TypeToken;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public abstract class SwitchTypeUserType<T, J> implements UserType {

    abstract protected J getFromResultSet(ResultSet rs, String columnName) throws SQLException;

    abstract protected T getSwitchValue(J value);

    protected abstract J getJdbcValue(T value);

    abstract protected int getSqlType();

    protected Class<T> switchType;


    protected Class<T> getEnumClass() {
        if (switchType == null) {
            switchType = (Class<T>) new TypeToken<T>(getClass()){}.getRawType();
        }
        return switchType;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] {getSqlType()};
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        J value = getFromResultSet(rs, names[0]);
        if (rs.wasNull()) {
            return null;
        }

        return getSwitchValue(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        final J jdbcValue = value == null ? null : getJdbcValue((T) value);

        if (jdbcValue == null) {
            st.setNull(index, getSqlType());
        } else {
            st.setObject(index, jdbcValue, getSqlType());
        }
    }

    @Override
    public Class returnedClass() {
        return getEnumClass();
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
