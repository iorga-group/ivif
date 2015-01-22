package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.ServiceTargetFileId;
import com.iorga.ivif.ja.tag.WSTargetFileId;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityAttribute;
import com.iorga.ivif.ja.tag.entities.EntityAttributePreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityBaseServiceTargetFile;
import com.iorga.ivif.ja.tag.util.TargetFileUtils;
import com.iorga.ivif.tag.JAXBSourceTagHandler;
import com.iorga.ivif.tag.TargetFactory;
import com.iorga.ivif.tag.bean.Column;
import com.iorga.ivif.tag.bean.Grid;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;

public class GridSourceTagHandler extends JAXBSourceTagHandler<Grid, JAGeneratorContext> {

    public GridSourceTagHandler() throws JAXBException {
        super(Grid.class);
    }

    @Override
    public void declareTargets(final JAGeneratorContext context) throws Exception {
        super.declareTargets(context);

        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {
            @Override
            protected void onConfigurationPrepared(final JAConfiguration configuration) throws Exception {

                //TODO create main JAX-RS Application to set base WS path to '/api'
                final String gridName = element.getName();

                // Create Model
                context.getOrCreateTarget(GridModel.class, gridName, new TargetFactory<GridModel, String, JAGeneratorContext>() {
                    @Override
                    public GridModel createTarget() throws Exception {
                        return new GridModel(gridName, element);
                    }
                });

                // Create Java Entity Base Service
                context.getOrCreateTarget(EntityBaseServiceTargetFile.class, new ServiceTargetFileId(element.getEntity() + "BaseService", null, configuration));

                // Create Java WS
                final WSTargetFileId wsTargetFileId = new WSTargetFileId(gridName + "BaseWS", null, configuration);
                context.getOrCreateTarget(GridBaseWSTargetFile.class, wsTargetFileId, new TargetFactory<GridBaseWSTargetFile, WSTargetFileId, JAGeneratorContext>() {

                    @Override
                    public GridBaseWSTargetFile createTarget() throws Exception {
                        return new GridBaseWSTargetFile(wsTargetFileId, context, gridName);
                    }
                });

                // Create JS controller
                context.getOrCreateTarget(GridCtrlJsTargetFile.class, gridName);

                // Create HTML
                context.getOrCreateTarget(GridHtmlTargetFile.class, gridName);
            }
        });
    }
}
