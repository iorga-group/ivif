package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.WSTargetFileId;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.tag.JAXBSourceTagHandler;
import com.iorga.ivif.tag.TargetFactory;
import com.iorga.ivif.tag.TargetPreparedWaiter;
import com.iorga.ivif.tag.bean.ActionOpenView;

import javax.xml.bind.JAXBException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ActionOpenViewSourceTagHandler extends JAXBSourceTagHandler<ActionOpenView, JAGeneratorContext> {

    public ActionOpenViewSourceTagHandler() throws JAXBException {
        super(ActionOpenView.class);
    }

    @Override
    public void declareTargets(final JAGeneratorContext context) throws Exception {
        super.declareTargets(context);

        final String queryModelId = "action-open-view:" + element.getName();

        context.getOrCreateTarget(ActionOpenViewServiceJsTargetFile.class, element.getName(), new TargetFactory<ActionOpenViewServiceJsTargetFile, String, JAGeneratorContext>() {
            @Override
            public ActionOpenViewServiceJsTargetFile createTarget() throws Exception {
                return new ActionOpenViewServiceJsTargetFile(element.getName(), element.getGridName(), context);
            }
        });

        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {
            @Override
            protected void onConfigurationPrepared(final JAConfiguration configuration) throws Exception {

                context.waitForEvent(new TargetPreparedWaiter<GridBaseWSTargetFile, WSTargetFileId, JAGeneratorContext>(GridBaseWSTargetFile.class, new WSTargetFileId(element.getGridName()+"BaseWS", configuration), ActionOpenViewSourceTagHandler.this) {

                    @Override
                    public void onTargetPrepared(final GridBaseWSTargetFile gridBaseWSTargetFile) throws Exception {
                        // We will parse the query, and generate the corresponding QueryDSL code, and if an identifier is detected, register it and compute its type
                        // First we must retrieve the Grid=>Entity because it is the base types reference
                        final EntityTargetFileId baseEntityId = gridBaseWSTargetFile.getGrid().getEntityTargetFileId();
                        // Then we parse the query and will visit it to find parameters and resolve its type
                        final QueryModel queryModel = context.getOrCreateTarget(QueryModel.class, queryModelId, new TargetFactory<QueryModel, String, JAGeneratorContext>() {
                            @Override
                            public QueryModel createTarget() throws Exception {
                                return new QueryModel(queryModelId, element.getQuery(), baseEntityId, ActionOpenViewSourceTagHandler.this);
                            }
                        });

                        final String actionName = element.getName();
                        final ActionOpenViewModel actionOpenViewModel = context.getOrCreateTarget(ActionOpenViewModel.class, actionName, new TargetFactory<ActionOpenViewModel, String, JAGeneratorContext>() {
                            @Override
                            public ActionOpenViewModel createTarget() throws Exception {
                                return new ActionOpenViewModel(actionName, element, queryModel, gridBaseWSTargetFile);
                            }
                        });

                        // Finaly set add the action to the grid WS
                        gridBaseWSTargetFile.addActionOpenView(actionOpenViewModel);
                        // And tell its controller
                        context.waitForEvent(new TargetPreparedWaiter<GridCtrlJsTargetFile, String, JAGeneratorContext>(GridCtrlJsTargetFile.class, gridBaseWSTargetFile.getGrid().getId(), ActionOpenViewSourceTagHandler.this) {
                            @Override
                            protected void onTargetPrepared(GridCtrlJsTargetFile target) throws Exception {
                                target.setActionOpenViewDefined(true);
                            }
                        });
                    }
                });
            }
        });
    }
}
