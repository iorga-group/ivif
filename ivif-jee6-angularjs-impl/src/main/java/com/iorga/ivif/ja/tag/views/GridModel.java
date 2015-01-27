package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.ServiceTargetFileId;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityAttribute;
import com.iorga.ivif.ja.tag.entities.EntityAttributePreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.util.TargetFileUtils;
import com.iorga.ivif.tag.AbstractTarget;
import com.iorga.ivif.tag.TargetFactory;
import com.iorga.ivif.tag.TargetPreparedWaiter;
import com.iorga.ivif.tag.bean.Column;
import com.iorga.ivif.tag.bean.Grid;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridModel extends AbstractTarget<String, JAGeneratorContext> {
    protected final Grid element;

    public static final Pattern LINE_REF_PATTERN = Pattern.compile("(?<![\\w\\$])\\$line\\.[\\p{Alpha}\\$_][\\w\\$\\.]*");

    protected String variableName;
    protected List<GridColumn> selectedColumns;
    protected List<DisplayedGridColumn> displayedColumns;
    protected EntityTargetFileId entityTargetFileId;
    protected ServiceTargetFileId serviceTargetFileId;
    protected Map<String, GridColumn> selectedColumnsByRef;
    protected List<GridColumn> idColumns;
    /**
     * Editable columns are all columns which are editable = true
     */
    protected LinkedHashSet<GridColumn> editableColumns;
    /**
     * Save columns are all editable columns + all id columns + version column if any
     */
    protected LinkedHashSet<GridColumn> saveColumns;
    protected LinkedHashSet<GridColumn> selectedWithoutSaveColumns;
    protected GridColumn versionColumn;
    protected QueryModel queryModel;

    public static class GridColumn {
        protected String refVariableName;
        protected String ref;
        private EntityAttribute entityAttribute;
        private List<EntityAttribute> refEntityAttributes = new ArrayList<>();

        public GridColumn(EntityAttribute entityAttribute) {
            this(entityAttribute.getElement().getValue().getName());
            this.entityAttribute = entityAttribute;
        }

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

        public List<EntityAttribute> getRefEntityAttributes() {
            return refEntityAttributes;
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

        public Column getElement() {
            return column;
        }
    }

    public GridModel(String id, Grid element) {
        super(id);
        this.element = element;
    }

    @Override
    public void prepare(final JAGeneratorContext context) throws Exception {
        super.prepare(context);


        variableName = TargetFileUtils.getVariableNameFromCamelCasedName(element.getName());

        // Prepare displayed columns
        selectedColumns = new ArrayList<>();
        displayedColumns = new ArrayList<>();
        selectedColumnsByRef = new HashMap<>();
        idColumns = new ArrayList<>();
        editableColumns = new LinkedHashSet<>();

        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {
            @Override
            protected void onConfigurationPrepared(final JAConfiguration configuration) throws Exception {

                entityTargetFileId = new EntityTargetFileId(element.getEntity(), configuration);
                for (Column column : element.getColumn()) {
                    DisplayedGridColumn displayedGridColumn = new DisplayedGridColumn(column);
                    displayedColumns.add(displayedGridColumn);
                    if (column.isEditable()) {
                        editableColumns.add(displayedGridColumn);
                    }
                    prepareSelectedColumn(displayedGridColumn, context, configuration);
                }

                // Add columns selected by an action
                String onOpenAction = element.getOnOpen();
                if (StringUtils.isNotBlank(onOpenAction)) {
                    Matcher matcher = LINE_REF_PATTERN.matcher(onOpenAction);
                    while (matcher.find()) {
                        String ref = StringUtils.substringAfter(matcher.group(), "$line.");
                        if (!selectedColumnsByRef.containsKey(ref)) {
                            // Add this non already added id column
                            GridColumn gridColumn = new GridColumn(ref);
                            prepareSelectedColumn(gridColumn, context, configuration);
                        }
                    }
                }

                selectedWithoutSaveColumns = new LinkedHashSet<>(selectedColumns);

                // Now if the grid is editable, we must be sure that id columns & verion columns of the entity is selected
                if (element.isEditable()) {
                    context.waitForEvent(new TargetPreparedWaiter<EntityTargetFile, EntityTargetFileId, JAGeneratorContext>(EntityTargetFile.class, entityTargetFileId, GridModel.this) {
                        @Override
                        protected void onTargetPrepared(EntityTargetFile entityTargetFile) throws Exception {
                            saveColumns = new LinkedHashSet<>(editableColumns);

                            // Add id columns if not specified on selected columns
                            for (EntityAttribute entityAttribute : entityTargetFile.getIdAttributes()) {
                                final GridColumn gridColumn = addNewGridColumnIfNecessary(entityAttribute);
                                idColumns.add(gridColumn);
                                saveColumns.add(gridColumn);
                            }
                            final EntityAttribute versionAttribute = entityTargetFile.getVersionAttribute();
                            if (versionAttribute != null) {
                                final GridColumn gridColumn = addNewGridColumnIfNecessary(versionAttribute);
                                saveColumns.add(gridColumn);
                                versionColumn = gridColumn;
                            }
                            selectedWithoutSaveColumns.removeAll(saveColumns);
                        }

                        private GridColumn addNewGridColumnIfNecessary(EntityAttribute entityAttribute) throws Exception {
                            final String attributeName = entityAttribute.getElement().getValue().getName();
                            if (!selectedColumnsByRef.containsKey(attributeName)) {
                                // Add this non already added id column
                                prepareSelectedColumn(new GridColumn(entityAttribute), context, configuration);
                            }
                            return selectedColumnsByRef.get(attributeName);
                        }
                    });
                }

                //TODO create main JAX-RS Application to set base WS path to '/api'
                // Create Java Entity Base Service
                serviceTargetFileId = new ServiceTargetFileId(element.getEntity() + "BaseService", null, configuration);

                // Create QueryModel
                final String queryModelId = "grid:" + element.getName();
                queryModel = context.getOrCreateTarget(QueryModel.class, queryModelId, new TargetFactory<QueryModel, String, JAGeneratorContext>() {
                    @Override
                    public QueryModel createTarget() throws Exception {
                        return new QueryModel(queryModelId, element.getQuery(), entityTargetFileId, GridModel.this);
                    }
                });
            }
        });
    }

    protected void prepareSelectedColumn(GridColumn gridColumn, JAGeneratorContext context, final JAConfiguration configuration) throws Exception {
        String ref = gridColumn.ref;
        selectedColumns.add(gridColumn);
        selectedColumnsByRef.put(gridColumn.ref, gridColumn);
        if (gridColumn.entityAttribute == null) {
            Deque<String> refPath = new LinkedList<>(Arrays.asList(ref.split("\\.")));
            waitToPrepareGridColumnRecursive(refPath, entityTargetFileId, gridColumn, context, configuration);
        }
    }

    private void waitToPrepareGridColumnRecursive(final Deque<String> refPath, EntityTargetFileId entityTargetFileId, final GridColumn gridColumn, final JAGeneratorContext context, final JAConfiguration configuration) throws Exception {
        String curPath = refPath.remove();
        context.waitForEvent(new EntityAttributePreparedWaiter(curPath, entityTargetFileId, this) {
            @Override
            protected void onEntityAttributePrepared(EntityAttribute entityAttribute) throws Exception {
                gridColumn.refEntityAttributes.add(entityAttribute);
                if (refPath.isEmpty()) {
                    // that was the last part of the path, we can resolve the attribute
                    gridColumn.setEntityAttribute(entityAttribute);
                } else {
                    // this is not the last part of the path, let's wait on next part
                    EntityTargetFileId entityTargetFileId = new EntityTargetFileId(entityAttribute.getType(), configuration);
                    GridModel.this.waitToPrepareGridColumnRecursive(refPath, entityTargetFileId, gridColumn, context, configuration);
                }
            }
        });
    }

    public String getGridName() {
        return getId();
    }
    
    @Override
	public String toString() {
		return "GridModel [id=" + getId() + "]";
	}

    /// Getters & Setters

	public ServiceTargetFileId getServiceTargetFileId() {
        return serviceTargetFileId;
    }

    public EntityTargetFileId getEntityTargetFileId() {
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

    public List<GridColumn> getIdColumns() {
        return idColumns;
    }

    public LinkedHashSet<GridColumn> getEditableColumns() {
        return editableColumns;
    }

    public LinkedHashSet<GridColumn> getSelectedWithoutSaveColumns() {
        return selectedWithoutSaveColumns;
    }

    public LinkedHashSet<GridColumn> getSaveColumns() {
        return saveColumns;
    }

    public GridColumn getVersionColumn() {
        return versionColumn;
    }

    public QueryModel getQueryModel() {
        return queryModel;
    }
}
