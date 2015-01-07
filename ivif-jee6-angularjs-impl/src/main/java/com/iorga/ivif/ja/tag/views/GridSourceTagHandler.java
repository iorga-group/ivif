package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.ServiceTargetFileId;
import com.iorga.ivif.ja.tag.WSTargetFileId;
import com.iorga.ivif.ja.tag.entities.EntityBaseServiceTargetFile;
import com.iorga.ivif.tag.JAXBSourceTagHandler;
import com.iorga.ivif.tag.bean.Grid;

import javax.xml.bind.JAXBException;

public class GridSourceTagHandler extends JAXBSourceTagHandler<Grid, JAGeneratorContext> {

    public GridSourceTagHandler() throws JAXBException {
        super(Grid.class);
    }

    @Override
    public void prepareTargetFiles(JAGeneratorContext context) throws Exception {
        super.prepareTargetFiles(context);

        //TODO create main JAX-RS Application to set base WS path to '/api'
        String gridName = element.getName();

        // Create Java Entity Base Service
        EntityBaseServiceTargetFile baseService = context.getOrCreateTargetFile(EntityBaseServiceTargetFile.class, new ServiceTargetFileId(element.getEntity() + "BaseService", null, context));//TODO handle entity given in absolute path

        // Create Java WS
        GridBaseWSTargetFile gridBaseWSTargetFile = context.getOrCreateTargetFile(GridBaseWSTargetFile.class, new WSTargetFileId(gridName + "BaseWS", null, context));
        gridBaseWSTargetFile.setGrid(element);
        gridBaseWSTargetFile.setBaseService(baseService);

        // Create JS controller
        GridCtrlJsTargetFile gridCtrlJsTargetFile = context.getOrCreateTargetFile(GridCtrlJsTargetFile.class, gridName);
        gridCtrlJsTargetFile.setGrid(element);

        // Create HTML
        GridHtmlTargetFile gridHtmlTargetFile = context.getOrCreateTargetFile(GridHtmlTargetFile.class, gridName);
        gridHtmlTargetFile.setGrid(element);
    }
}
