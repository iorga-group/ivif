package com.iorga.ivif.ja;

import com.google.common.reflect.TypeToken;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public abstract class SwitchTypeUserType<T, J> implements UserType {

    abstract protected T getSwitchValue(J value);

    protected abstract J getJdbcValue(T value);

    protected Class<T> switchClass;

    protected Class<J> jdbcClass;

    protected int[] sqlTypes;


    protected Class<T> getSwitchClass() {
        if (switchClass == null) {
            switchClass = (Class<T>) new TypeToken<T>(getClass()){}.getRawType();
        }
        return switchClass;
    }

    protected Class<J> getJdbcClass() {
        if (jdbcClass == null) {
            jdbcClass = (Class<J>) new TypeToken<J>(getClass()){}.getRawType();
        }
        return jdbcClass;
    }

    @Override
    public int[] sqlTypes() {
        if (sqlTypes == null) {
            sqlTypes = new int[1];
            final Class<J> jdbcClass = getJdbcClass();
            if (String.class.isAssignableFrom(jdbcClass)) {
                sqlTypes[0] = Types.VARCHAR;
            } else if (Character.class.isAssignableFrom(jdbcClass)) {
                sqlTypes[0] = Types.CHAR;
            } else if (Integer.class.isAssignableFrom(jdbcClass)) {
                sqlTypes[0] = Types.INTEGER;
            } else {
                throw new UnsupportedOperationException("Can't handle " + jdbcClass.toString() + " yet.");
            }
        }
        return sqlTypes;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        final Class<J> jdbcClass = getJdbcClass();
        J value;
        final String name = names[0];
        if (String.class.isAssignableFrom(jdbcClass)) {
            value = (J) rs.getString(name);
        } else if (Character.class.isAssignableFrom(jdbcClass)) {
            value = (J) new Character(rs.getString(name).charAt(0));
        } else if (Integer.class.isAssignableFrom(jdbcClass)) {
            value = (J) new Integer(rs.getInt(name));
        } else {
            throw new UnsupportedOperationException("Can't handle " + jdbcClass.toString() + " yet.");
        }
        if (rs.wasNull()) {
            return null;
        }
        return getSwitchValue(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        final J jdbcValue = value == null ? null : getJdbcValue((T) value);

        if (jdbcValue == null) {
            st.setNull(index, sqlTypes()[0]);
        } else {
            st.setObject(index, jdbcValue, sqlTypes()[0]);
        }
    }

    @Override
    public Class returnedClass() {
        return getSwitchClass();
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
