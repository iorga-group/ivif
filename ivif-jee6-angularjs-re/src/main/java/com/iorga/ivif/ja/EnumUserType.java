package com.iorga.ivif.ja;

public abstract class EnumUserType<E extends Enum<E> & Valuable<J>, J> extends SwitchTypeUserType<E, J> {

    protected abstract E getByValue(J value);


    @Override
    protected J getJdbcValue(E value) {
        return value.value();
    }

    @Override
    protected E getSwitchValue(J value) {
        return getByValue(value);
    }
}
