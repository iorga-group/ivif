package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EnumSelectionTargetFile.EnumSelectionTargetFileId;
import com.iorga.ivif.tag.JAXBSourceTagHandler;
import com.iorga.ivif.tag.TargetFactory;
import com.iorga.ivif.tag.bean.Selection;

import javax.xml.bind.JAXBException;

public class SelectionSourceTagHandler extends JAXBSourceTagHandler<Selection, JAGeneratorContext> {

    public SelectionSourceTagHandler() throws JAXBException {
        super(Selection.class);
    }

    @Override
    public void declareTargets(final JAGeneratorContext context) throws Exception {
        super.declareTargets(context);

        final String selectionName = element.getName();

        final SelectionModel selectionModel = context.getOrCreateTarget(SelectionModel.class, selectionName, new TargetFactory<SelectionModel, String, JAGeneratorContext>() {
            @Override
            public SelectionModel createTarget() throws Exception {
                return new SelectionModel(element, context);
            }
        });

        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {
            @Override
            protected void onConfigurationPrepared(final JAConfiguration configuration) throws Exception {

                final EnumSelectionTargetFileId id = new EnumSelectionTargetFileId(selectionName, configuration);
                context.getOrCreateTarget(EnumSelectionTargetFile.class, id, new TargetFactory<EnumSelectionTargetFile, EnumSelectionTargetFileId, JAGeneratorContext>() {
                    @Override
                    public EnumSelectionTargetFile createTarget() throws Exception {
                        return new EnumSelectionTargetFile(id, selectionModel, context);
                    }
                });
                context.getOrCreateTarget(SelectionServiceJsTargetFile.class, selectionName, new TargetFactory<SelectionServiceJsTargetFile, String, JAGeneratorContext>() {
                    @Override
                    public SelectionServiceJsTargetFile createTarget() throws Exception {
                        return new SelectionServiceJsTargetFile(selectionName, selectionModel, configuration, context);
                    }
                });
            }
        });
    }
}