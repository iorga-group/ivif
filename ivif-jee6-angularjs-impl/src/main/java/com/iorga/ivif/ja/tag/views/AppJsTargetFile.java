package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JsTargetFile;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AppJsTargetFile extends JsTargetFile<Void> {
    private JAConfiguration configuration;

    public AppJsTargetFile(JAGeneratorContext context) {
        super(null, context);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {
            @Override
            protected void onConfigurationPrepared(JAConfiguration configuration) throws Exception {
                AppJsTargetFile.this.configuration = configuration;
            }
        });
    }

    @Override
    protected String getFreemarkerTemplateName() {
        return "views/app.js.ftl";
    }

    @Override
    public Path getPathRelativeToWebappPath(JAGeneratorContext context) {
        return Paths.get("scripts", "app.js");
    }

    public JAConfiguration getConfiguration() {
        return configuration;
    }
}
