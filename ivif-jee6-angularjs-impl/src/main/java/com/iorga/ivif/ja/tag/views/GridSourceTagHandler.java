package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.ServiceTargetFileId;
import com.iorga.ivif.ja.tag.WSTargetFileId;
import com.iorga.ivif.ja.tag.entities.EntityAttribute;
import com.iorga.ivif.ja.tag.entities.EntityBaseServiceTargetFile;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile;
import com.iorga.ivif.ja.tag.util.TargetFileUtils;
import com.iorga.ivif.tag.JAXBSourceTagHandler;
import com.iorga.ivif.tag.PartWaiter;
import com.iorga.ivif.tag.bean.Column;
import com.iorga.ivif.tag.bean.Grid;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridSourceTagHandler extends JAXBSourceTagHandler<Grid, JAGeneratorContext> {

    public static final Pattern LINE_REF_PATTERN = Pattern.compile("(?<![\\w$])line\\.[\\p{Alpha}$_][\\w$\\.]*");

    protected String variableName;
    protected List<GridColumn> selectedColumns;
    protected List<DisplayedGridColumn> displayedColumns;
    protected EntityTargetFile entityTargetFile;
    private GridCtrlJsTargetFile gridCtrlJsTargetFile;

    public static class GridColumn {
        protected String refVariableName;
        protected String ref;
        private EntityAttribute entityAttribute;

        public GridColumn(String ref) {
            this.ref = ref;
            this.refVariableName = ref.replaceAll("\\.", "_");
        }

        public void setEntityAttribute(EntityAttribute entityAttribute) {
            this.entityAttribute = entityAttribute;
        }

        public String getRefVariableName() {
            return refVariableName;
        }

        public String getRef() {
            return ref;
        }

        public EntityAttribute getEntityAttribute() {
            return entityAttribute;
        }
    }

    public static class DisplayedGridColumn extends GridColumn {
        private final Column column;
        protected String title;

        public DisplayedGridColumn(Column column) {
            super(column.getRef());
            this.column = column;
        }

        @Override
        public void setEntityAttribute(EntityAttribute entityAttribute) {
            super.setEntityAttribute(entityAttribute);
            String columnTitle = column.getTitle();
            this.title = StringUtils.isNotBlank(columnTitle) ? columnTitle : entityAttribute.getTitle();
        }

        public String getTitle() {
            return title;
        }
    }

    public GridSourceTagHandler() throws JAXBException {
        super(Grid.class);
    }

    @Override
    public void prepareTargetFiles(JAGeneratorContext context) throws Exception {
        super.prepareTargetFiles(context);

        variableName = TargetFileUtils.getVariableNameFromName(element.getName());

        // Prepare displayed columns
        selectedColumns = new ArrayList<>();
        displayedColumns = new ArrayList<>();
        entityTargetFile = context.getOrCreateTargetFile(EntityTargetFile.class, new EntityTargetFile.EntityTargetFileId(element.getEntity(), context));
        for (Column column : element.getColumn()) {
            DisplayedGridColumn displayedGridColumn = new DisplayedGridColumn(column);
            displayedColumns.add(displayedGridColumn);
            prepareSelectedColumn(displayedGridColumn, context);
        }

        // Add columns selected by an action
        String onOpenAction = element.getOnOpen();
        if (StringUtils.isNotBlank(onOpenAction)) {
            Matcher matcher = LINE_REF_PATTERN.matcher(onOpenAction);
            while (matcher.find()) {
                String ref = StringUtils.substringAfter(matcher.group(), "line.");
                GridColumn gridColumn = new GridColumn(ref);
                prepareSelectedColumn(gridColumn, context);
            }
        }

        //TODO create main JAX-RS Application to set base WS path to '/api'
        String gridName = element.getName();

        // Create Java Entity Base Service
        EntityBaseServiceTargetFile baseService = context.getOrCreateTargetFile(EntityBaseServiceTargetFile.class, new ServiceTargetFileId(element.getEntity() + "BaseService", null, context));//TODO handle entity given in absolute path

        // Create Java WS
        GridBaseWSTargetFile gridBaseWSTargetFile = context.getOrCreateTargetFile(GridBaseWSTargetFile.class, new WSTargetFileId(gridName + "BaseWS", null, context));
        gridBaseWSTargetFile.setGridSourceTagHandler(this);
        gridBaseWSTargetFile.setBaseService(baseService);

        // Create JS controller
        gridCtrlJsTargetFile = context.getOrCreateTargetFile(GridCtrlJsTargetFile.class, gridName);
        gridCtrlJsTargetFile.setGridSourceTagHandler(this);

        // Create HTML
        GridHtmlTargetFile gridHtmlTargetFile = context.getOrCreateTargetFile(GridHtmlTargetFile.class, gridName);
        gridHtmlTargetFile.setGridSourceTagHandler(this);
    }

    protected void prepareSelectedColumn(GridColumn gridColumn, JAGeneratorContext context) throws Exception {
        String ref = gridColumn.ref;
        selectedColumns.add(gridColumn);
        Deque<String> refPath = new LinkedList<>(Arrays.asList(ref.split("\\.")));
        waitToPrepareGridColumnRecursive(refPath, entityTargetFile, gridColumn, context);
    }

    private void waitToPrepareGridColumnRecursive(final Deque<String> refPath, EntityTargetFile entityTargetFile, final GridColumn displayedGridColumn, final JAGeneratorContext context) throws Exception {
        String curPath = refPath.remove();
        entityTargetFile.waitForPartToBePrepared(new PartWaiter<EntityAttribute, String, JAGeneratorContext>(EntityAttribute.class, curPath, this) {
            @Override
            public void onPrepared(EntityAttribute entityAttribute) throws Exception {
                if (refPath.isEmpty()) {
                    // that was the last part of the path, we can resolve the attribute
                    displayedGridColumn.setEntityAttribute(entityAttribute);
                } else {
                    // this is not the last part of the path, let's wait on next part
                    EntityTargetFile entityTargetFile = context.getOrCreateTargetFile(EntityTargetFile.class, new EntityTargetFile.EntityTargetFileId(entityAttribute.getType(), context));
                    GridSourceTagHandler.this.waitToPrepareGridColumnRecursive(refPath, entityTargetFile, displayedGridColumn, context);
                }
            }
        });
    }

    /// Getters & Setters

    public EntityTargetFile getEntityTargetFile() {
        return entityTargetFile;
    }

    public String getVariableName() {
        return variableName;
    }

    public List<GridColumn> getSelectedColumns() {
        return selectedColumns;
    }

    public List<DisplayedGridColumn> getDisplayedColumns() {
        return displayedColumns;
    }

    public GridCtrlJsTargetFile getGridCtrlJsTargetFile() {
        return gridCtrlJsTargetFile;
    }
}
