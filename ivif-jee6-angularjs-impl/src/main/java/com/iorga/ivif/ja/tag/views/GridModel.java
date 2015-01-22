package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.ServiceTargetFileId;
import com.iorga.ivif.ja.tag.WSTargetFileId;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityAttribute;
import com.iorga.ivif.ja.tag.entities.EntityAttributePreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityBaseServiceTargetFile;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile;
import com.iorga.ivif.ja.tag.util.TargetFileUtils;
import com.iorga.ivif.tag.AbstractTarget;
import com.iorga.ivif.tag.TargetFactory;
import com.iorga.ivif.tag.bean.Column;
import com.iorga.ivif.tag.bean.Grid;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridModel extends AbstractTarget<String, JAGeneratorContext> {
    protected final Grid element;

    public static final Pattern LINE_REF_PATTERN = Pattern.compile("(?<![\\w$])line\\.[\\p{Alpha}$_][\\w$\\.]*");

    protected String variableName;
    protected List<GridColumn> selectedColumns;
    protected List<DisplayedGridColumn> displayedColumns;
    protected EntityTargetFile.EntityTargetFileId entityTargetFileId;
    protected ServiceTargetFileId serviceTargetFileId;

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

    public GridModel(String id, Grid element) {
        super(id);
        this.element = element;
    }

    @Override
    public void prepare(final JAGeneratorContext context) throws Exception {
        super.prepare(context);


        variableName = TargetFileUtils.getVariableNameFromName(element.getName());

        // Prepare displayed columns
        selectedColumns = new ArrayList<>();
        displayedColumns = new ArrayList<>();
        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {
            @Override
            protected void onConfigurationPrepared(final JAConfiguration configuration) throws Exception {

                entityTargetFileId = new EntityTargetFile.EntityTargetFileId(element.getEntity(), configuration);
                for (Column column : element.getColumn()) {
                    DisplayedGridColumn displayedGridColumn = new DisplayedGridColumn(column);
                    displayedColumns.add(displayedGridColumn);
                    prepareSelectedColumn(displayedGridColumn, context, configuration);
                }

                // Add columns selected by an action
                String onOpenAction = element.getOnOpen();
                if (StringUtils.isNotBlank(onOpenAction)) {
                    Matcher matcher = LINE_REF_PATTERN.matcher(onOpenAction);
                    while (matcher.find()) {
                        String ref = StringUtils.substringAfter(matcher.group(), "line.");
                        GridColumn gridColumn = new GridColumn(ref);
                        prepareSelectedColumn(gridColumn, context, configuration);
                    }
                }

                //TODO create main JAX-RS Application to set base WS path to '/api'
                // Create Java Entity Base Service
                serviceTargetFileId = new ServiceTargetFileId(element.getEntity() + "BaseService", null, configuration);
            }
        });
    }

    protected void prepareSelectedColumn(GridColumn gridColumn, JAGeneratorContext context, final JAConfiguration configuration) throws Exception {
        String ref = gridColumn.ref;
        selectedColumns.add(gridColumn);
        Deque<String> refPath = new LinkedList<>(Arrays.asList(ref.split("\\.")));
        waitToPrepareGridColumnRecursive(refPath, entityTargetFileId, gridColumn, context, configuration);
    }

    private void waitToPrepareGridColumnRecursive(final Deque<String> refPath, EntityTargetFile.EntityTargetFileId entityTargetFileId, final GridColumn displayedGridColumn, final JAGeneratorContext context, final JAConfiguration configuration) throws Exception {
        String curPath = refPath.remove();
        context.waitForEvent(new EntityAttributePreparedWaiter(curPath, entityTargetFileId, this) {
            @Override
            protected void onEntityAttributePrepared(EntityAttribute entityAttribute) throws Exception {
                if (refPath.isEmpty()) {
                    // that was the last part of the path, we can resolve the attribute
                    displayedGridColumn.setEntityAttribute(entityAttribute);
                } else {
                    // this is not the last part of the path, let's wait on next part
                    EntityTargetFile.EntityTargetFileId entityTargetFileId = new EntityTargetFile.EntityTargetFileId(entityAttribute.getType(), configuration);
                    GridModel.this.waitToPrepareGridColumnRecursive(refPath, entityTargetFileId, displayedGridColumn, context, configuration);
                }
            }
        });
    }

    public String getGridName() {
        return getId();
    }

    /// Getters & Setters

    public ServiceTargetFileId getServiceTargetFileId() {
        return serviceTargetFileId;
    }

    public EntityTargetFile.EntityTargetFileId getEntityTargetFileId() {
        return entityTargetFileId;
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

    public Grid getElement() {
        return element;
    }
}
