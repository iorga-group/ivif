package com.iorga.ivif.ja.tag.configurations;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.tag.TargetPreparedWaiter;

public abstract class JAConfigurationPreparedWaiter extends TargetPreparedWaiter<JAConfiguration, Void, JAGeneratorContext> {
    public JAConfigurationPreparedWaiter(Object waiterSource) {
        super(JAConfiguration.class, null, waiterSource);
    }

    @Override
    protected void onTargetPrepared(JAConfiguration configuration) throws Exception {
        onConfigurationPrepared(configuration);
    }

    protected abstract void onConfigurationPrepared(JAConfiguration configuration) throws Exception;
}
