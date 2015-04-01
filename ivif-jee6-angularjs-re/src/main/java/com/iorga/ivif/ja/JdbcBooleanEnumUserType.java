package com.iorga.ivif.ja;

import org.hibernate.HibernateException;

import java.util.Objects;

public abstract class JdbcBooleanEnumUserType<E extends Enum<E>, J> extends SwitchTypeUserType<E, J> {

    protected E trueValue;
    protected E falseValue;
    protected J trueJdbcValue;
    protected J falseJdbcValue;


    public JdbcBooleanEnumUserType(E trueValue, E falseValue, J trueJdbcValue, J falseJdbcValue) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
        this.trueJdbcValue = trueJdbcValue;
        this.falseJdbcValue = falseJdbcValue;
    }


    @Override
    protected J getJdbcValue(E value) {
        if (value == null) {
            return null;
        } else if (Objects.equals(getTrueValue(), value)) {
            return getTrueJdbcValue();
        } else {
            return getFalseJdbcValue();
        }
    }

    @Override
    protected E getSwitchValue(J value) {
        if (value == null) {
            return null;
        } else if (Objects.equals(getTrueJdbcValue(), value)) {
            return getTrueValue();
        } else if (Objects.equals(getFalseJdbcValue(), value)) {
            return getFalseValue();
        } else {
            throw new HibernateException("Given value was nor true nor false: "+value);
        }
    }


    public E getTrueValue() {
        return trueValue;
    }

    public E getFalseValue() {
        return falseValue;
    }

    public J getTrueJdbcValue() {
        return trueJdbcValue;
    }

    public J getFalseJdbcValue() {
        return falseJdbcValue;
    }
}
