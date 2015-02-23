package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.ServiceTargetFileId;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityAttribute;
import com.iorga.ivif.ja.tag.entities.EntityAttributePreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.tag.bean.*;
import com.iorga.ivif.util.TargetFileUtils;
import com.iorga.ivif.tag.AbstractTarget;
import com.iorga.ivif.tag.TargetFactory;
import com.iorga.ivif.tag.TargetPreparedWaiter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.lang.Boolean;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.iorga.ivif.ja.tag.views.JsExpressionParser.*;

public class GridModel extends AbstractTarget<String, JAGeneratorContext> {
    protected final Grid element;

    public static final Pattern LINE_OR_RECORD_REF = Pattern.compile("(?<![\\w\\$])\\$(line|record)(\\.[\\p{Alpha}\\$_][\\w\\$]*)*");

    protected String variableName;
    protected List<GridColumn> selectedColumns;
    protected LinkedHashSet<GridColumn> nonTransientSelectedColumns;
    protected List<DisplayedGridColumn> displayedColumns;
    protected LinkedHashSet<DisplayedGridColumn> nonTransientDisplayedColumns;
    protected EntityTargetFileId entityTargetFileId;
    protected ServiceTargetFileId serviceTargetFileId;
    protected Map<String, GridColumn> selectedColumnsByRef;
    protected LinkedHashSet<GridColumn> idColumns;
    /**
     * Editable columns are all columns which are editable = true
     */
    protected LinkedHashSet<GridColumn> editableColumns;
    /**
     * Save columns are all editable columns + all id columns + version column if any
     */
    protected LinkedHashSet<GridColumn> saveColumns;
    protected LinkedHashSet<GridColumn> nonTransientSelectedWithoutSaveColumns;
    protected List<Object> displayedColumnsOrCode;
    protected GridColumn versionColumn;
    protected QueryModel queryModel;

    protected boolean singleSelection;
    protected JsExpression onOpen;
    protected List<ToolbarButton> toolbarButtons;
    protected String title;
    protected JsExpression onSelect;

    protected String serviceSaveClassname;
    protected String serviceSaveMethod;
    protected String tabTitle;

    protected List<GridHighlight> highlights;

    public static class GridColumn {
        protected String refVariableName;
        protected String ref;
        private EntityAttribute entityAttribute;
        private List<EntityAttribute> refEntityAttributes = new ArrayList<>();
        private boolean editable;

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
        private final boolean editable;
        private JsExpression editableIfExpression;
        private String editSwitch = "$edit";

        public DisplayedGridColumn(Column column) {
            super(column.getRef());
            this.column = column;
            editable = column.isEditable() || (StringUtils.isNotBlank(column.getEditableIf()) && BooleanUtils.toBooleanObject(column.getEditableIf()) != Boolean.FALSE);
        }

        @Override
        public void setEntityAttribute(EntityAttribute entityAttribute) {
            super.setEntityAttribute(entityAttribute);
            String columnTitle = column.getTitle();
            this.title = StringUtils.isNotBlank(columnTitle) ? columnTitle : entityAttribute.getTitle();
        }

        public void setEditableIfExpression(JsExpression editableIfExpression) {
            this.editableIfExpression = editableIfExpression;
            if (editableIfExpression != null) {
                editSwitch = "$edit && (" + editableIfExpression.getExpression() + ")";
            } else {
                editSwitch = "$edit";
            }
        }

        public String getTitle() {
            return title;
        }

        public Column getElement() {
            return column;
        }

        public boolean isEditable() {
            return editable;
        }

        public String getEditSwitch() {
            return editSwitch;
        }
    }

    public static class ToolbarButton {
        private final Button element;
        private final JsExpression actionExpression;
        private final JsExpression disabledIfExpression;
        public List<List<String>> rolesAllowed;

        public ToolbarButton(Button element, JsExpression actionExpression, JsExpression disabledIfExpression) {
            this.element = element;
            this.actionExpression = actionExpression;
            this.disabledIfExpression = disabledIfExpression;
            rolesAllowed = new ArrayList<>();
        }

        public Button getElement() {
            return element;
        }

        public JsExpression getActionExpression() {
            return actionExpression;
        }

        public List<List<String>> getRolesAllowed() {
            return rolesAllowed;
        }

        public JsExpression getDisabledIfExpression() {
            return disabledIfExpression;
        }
    }

    public static class GridHighlight {
        private final String colorClass;
        private final String _if;

        public GridHighlight(String colorClass, String _if) {
            this.colorClass = colorClass;
            this._if = _if;
        }

        public String getColorClass() {
            return colorClass;
        }

        public String getIf() {
            return _if;
        }
    }

    public GridModel(String id, Grid element) {
        super(id);
        this.element = element;
    }

    @Override
    public void prepare(final JAGeneratorContext context) throws Exception {
        super.prepare(context);

        final String elementTitle = element.getTitle();
        title = StringUtils.isNotBlank(elementTitle) ? elementTitle : TargetFileUtils.getTitleFromCamelCasedName(element.getName());
        final String elementTabTitle = element.getTabTitle();
        tabTitle = StringUtils.isNotBlank(elementTabTitle) ? elementTabTitle : title;

        variableName = TargetFileUtils.getVariableNameFromCamelCasedName(element.getName());

        // Prepare displayed columns
        selectedColumns = new ArrayList<>();
        nonTransientSelectedColumns = new LinkedHashSet<>();
        displayedColumns = new ArrayList<>();
        nonTransientDisplayedColumns = new LinkedHashSet<>();
        selectedColumnsByRef = new HashMap<>();
        idColumns = new LinkedHashSet<>();
        editableColumns = new LinkedHashSet<>();
        displayedColumnsOrCode = new ArrayList<>();

        // Handle selection
        singleSelection = SelectionType.SINGLE.equals(element.getSelection());

        toolbarButtons = new ArrayList<>();

        highlights = new ArrayList<>();

        final String serviceSaveMethod = StringUtils.trim(element.getServiceSaveMethod());
        if (StringUtils.isNotBlank(serviceSaveMethod)) {
            this.serviceSaveClassname = StringUtils.substringBeforeLast(serviceSaveMethod, ".");
            this.serviceSaveMethod = StringUtils.substringAfterLast(serviceSaveMethod, ".");
        }

        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {
            @Override
            protected void onConfigurationPrepared(final JAConfiguration configuration) throws Exception {

                entityTargetFileId = new EntityTargetFileId(element.getEntity(), configuration);
                for (Object columnOrCode : element.getColumnOrCode()) {
                    if (columnOrCode instanceof Column) {
                        Column column = (Column) columnOrCode;
                        DisplayedGridColumn displayedGridColumn = new DisplayedGridColumn(column);

                        displayedColumns.add(displayedGridColumn);
                        nonTransientDisplayedColumns.add(displayedGridColumn); // will remove if non transient when it will be resolved
                        displayedColumnsOrCode.add(displayedGridColumn);
                        if (displayedGridColumn.isEditable()) {
                            editableColumns.add(displayedGridColumn);
                        }

                        prepareSelectedColumn(displayedGridColumn, context, configuration);
                    } else {
                        // this is a <code> element
                        String code = ((Element) columnOrCode).getTextContent();
                        // we must replace the $line and $record references
                        final StringBuffer codeBuffer = new StringBuffer();
                        final Matcher matcher = LINE_OR_RECORD_REF.matcher(code);
                        while (matcher.find()) {
                            String ref = matcher.group();
                            final int dotIndex = ref.indexOf('.');
                            final StringBuilder refBuilder = new StringBuilder();
                            if (dotIndex > -1) {
                                // we have field references, let's replace them
                                final String fieldsRef = ref.substring(dotIndex + 1);
                                addColumnsToSelectForRefIfNecessary(fieldsRef, configuration, context); // add them as a select if necessary
                                refBuilder.append("." + fieldsRef.replaceAll("\\.", "_"));
                            }
                            if (ref.startsWith("$line")) {
                                refBuilder.insert(0, "line");
                            } else if (ref.startsWith("$record")) {
                                refBuilder.insert(0, "line.$original");
                            }
                            matcher.appendReplacement(codeBuffer, refBuilder.toString());
                        }
                        matcher.appendTail(codeBuffer);
                        displayedColumnsOrCode.add(codeBuffer.toString());
                    }
                }

                // Add columns selected by actions
                onOpen = addSelectColumnForActionIfNecessary(element.getOnOpen(), "selectedLine", "selectedLine.$original", configuration, context);
                onSelect = addSelectColumnForActionIfNecessary(element.getOnSelect(), "selectedLine", "selectedLine.$original", configuration, context);
                final Toolbar toolbar = element.getToolbar();
                if (toolbar != null) {
                    for (Button button : toolbar.getButton()) {
                        final JsExpression actionExpression = addSelectColumnForActionIfNecessary(button.getAction(), "$scope.selectedLine", "$scope.selectedLine.$original", configuration, context);
                        String disabledIfStr = button.getDisabledIf();
                        if (!actionExpression.getLineRefs().isEmpty()) {
                            // Button will be disabled if no line is selected
                            disabledIfStr = "!selectedLine" + (StringUtils.isNotBlank(disabledIfStr) ? " || (selectedLine && ("+disabledIfStr+"))" : "");
                        }
                        final JsExpression disabledIfExpression = addSelectColumnForExpressionIfNecessary(disabledIfStr, "selectedLine", "selectedLine.$original", configuration, context);
                        final ToolbarButton toolbarButton = new ToolbarButton(button, actionExpression, disabledIfExpression);
                        toolbarButtons.add(toolbarButton);
                        // Now compute the roles allowed if any
                        for (String action : actionExpression.getActions()) {
                            context.waitForEvent(new TargetPreparedWaiter<ActionOpenViewModel, String, JAGeneratorContext>(ActionOpenViewModel.class, action, GridModel.this) {
                                @Override
                                protected void onTargetPrepared(ActionOpenViewModel actionOpenViewModel) throws Exception {
                                    addRolesAllowedIfNotEmpty(actionOpenViewModel.getElement().getRolesAllowed());
                                    addRolesAllowedIfNotEmpty(actionOpenViewModel.getGridBaseWSTargetFile().getGrid().getElement().getRolesAllowed());
                                }

                                private void addRolesAllowedIfNotEmpty(List<String> rolesAllowed) {
                                    if (rolesAllowed != null && !rolesAllowed.isEmpty()) {
                                        toolbarButton.rolesAllowed.add(rolesAllowed);
                                    }
                                }
                            });
                        }
                    }
                }

                // Compute highlights
                for (Highlight highlight : element.getHighlight()) {
                    final JsExpression expression = addSelectColumnForExpressionIfNecessary(highlight.getIf(), "line", "line.$record", configuration, context);
                    highlights.add(new GridHighlight(highlight.getColorClass(), expression.getExpression()));
                }
                // handle single selection as a highlight
                if (singleSelection) {
                    highlights.add(new GridHighlight("active", "line.$selected"));
                }

                if (singleSelection) {
                    // We can select a line, must add the id attributes to the selected column in order to identify which line is selected
                    context.waitForEvent(new TargetPreparedWaiter<EntityTargetFile, EntityTargetFileId, JAGeneratorContext>(EntityTargetFile.class, entityTargetFileId, GridModel.this) {
                        @Override
                        protected void onTargetPrepared(EntityTargetFile entityTargetFile) throws Exception {
                            // Add id columns if not specified on selected columns
                            for (EntityAttribute entityAttribute : entityTargetFile.getIdAttributes()) {
                                addNewIdGridColumnIfNecessary(entityAttribute, context, configuration);
                            }
                        }
                    });
                }

                if (element.isEditable()) {
                    // Parse the potential editable-if expression of displayed columns
                    for (DisplayedGridColumn displayedColumn : displayedColumns) {
                        displayedColumn.setEditableIfExpression(addSelectColumnForExpressionIfNecessary(displayedColumn.getElement().getEditableIf(), "line", "line.$original", configuration, context));
                    }
                    nonTransientSelectedWithoutSaveColumns = new LinkedHashSet<>(nonTransientSelectedColumns);

                    context.waitForEvent(new TargetPreparedWaiter<EntityTargetFile, EntityTargetFileId, JAGeneratorContext>(EntityTargetFile.class, entityTargetFileId, GridModel.this) {
                        @Override
                        protected void onTargetPrepared(EntityTargetFile entityTargetFile) throws Exception {
                            // If the grid is editable, we must be sure that id columns & version columns of the entity is selected
                            saveColumns = new LinkedHashSet<>(editableColumns);

                            // Add id columns if not specified on selected columns
                            for (EntityAttribute entityAttribute : entityTargetFile.getIdAttributes()) {
                                final GridColumn gridColumn = addNewIdGridColumnIfNecessary(entityAttribute, context, configuration);
                                saveColumns.add(gridColumn);
                            }
                            final EntityAttribute versionAttribute = entityTargetFile.getVersionAttribute();
                            if (versionAttribute != null) {
                                final GridColumn gridColumn = addNewGridColumnIfNecessary(versionAttribute, context, configuration);
                                saveColumns.add(gridColumn);
                                versionColumn = gridColumn;
                            }
                            nonTransientSelectedWithoutSaveColumns.removeAll(saveColumns);
                        }
                    });
                } else {
                    nonTransientSelectedWithoutSaveColumns = new LinkedHashSet<>(nonTransientSelectedColumns);
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

    private GridColumn addNewIdGridColumnIfNecessary(EntityAttribute entityAttribute, JAGeneratorContext context, JAConfiguration configuration) throws Exception {
        final GridColumn gridColumn = addNewGridColumnIfNecessary(entityAttribute, context, configuration);
        idColumns.add(gridColumn);
        return gridColumn;
    }

    private GridColumn addNewGridColumnIfNecessary(EntityAttribute entityAttribute, JAGeneratorContext context, JAConfiguration configuration) throws Exception {
        final String attributeName = entityAttribute.getElement().getValue().getName();
        if (!selectedColumnsByRef.containsKey(attributeName)) {
            // Add this non already added id column
            prepareSelectedColumn(new GridColumn(entityAttribute), context, configuration);
        }
        return selectedColumnsByRef.get(attributeName);
    }

    private JsExpression addSelectColumnForExpressionIfNecessary(String expression, String dollarLineReplacement, String dollarRecordReplacement, JAConfiguration configuration, JAGeneratorContext context) throws Exception {
        if (StringUtils.isNotBlank(expression)) {
            final JsExpression jsExpression = JsExpressionParser.parseExpression(expression, dollarLineReplacement, dollarRecordReplacement);

            addColumnsToSelectForExpressionIfNecessary(jsExpression, configuration, context);
            return jsExpression;
        } else {
            return null;
        }
    }

    private void addColumnsToSelectForExpressionIfNecessary(JsExpression jsExpression, JAConfiguration configuration, JAGeneratorContext context) throws Exception {
        for (LineRef lineRef : jsExpression.getLineRefs()) {
            final String ref = lineRef.getRef();
            addColumnsToSelectForRefIfNecessary(ref, configuration, context);
        }
    }

    private void addColumnsToSelectForRefIfNecessary(String ref, JAConfiguration configuration, JAGeneratorContext context) throws Exception {
        if (!selectedColumnsByRef.containsKey(ref)) {
            // Add this non already added id column
            GridColumn gridColumn = new GridColumn(ref);
            prepareSelectedColumn(gridColumn, context, configuration);
        }
    }

    private JsExpression addSelectColumnForActionIfNecessary(String expression, String dollarLineReplacement, String dollarRecordReplacement, JAConfiguration configuration, JAGeneratorContext context) throws Exception {
        if (StringUtils.isNotBlank(expression)) {
            final JsExpression jsExpression = JsExpressionParser.parseActions(expression, dollarLineReplacement, dollarRecordReplacement);

            addColumnsToSelectForExpressionIfNecessary(jsExpression, configuration, context);
            return jsExpression;
        } else {
            return null;
        }
    }

    protected void prepareSelectedColumn(GridColumn gridColumn, JAGeneratorContext context, final JAConfiguration configuration) throws Exception {
        String ref = gridColumn.ref;
        selectedColumns.add(gridColumn);
        selectedColumnsByRef.put(gridColumn.ref, gridColumn);
        if (gridColumn.entityAttribute == null) {
            nonTransientSelectedColumns.add(gridColumn); // will remove if non transient when the entityAttribute will be resolved
            Deque<String> refPath = new LinkedList<>(Arrays.asList(ref.split("\\.")));
            waitToPrepareGridColumnRecursive(refPath, entityTargetFileId, gridColumn, context, configuration);
        } else {
            if (!gridColumn.entityAttribute.getElement().getValue().isTransient()) {
                nonTransientSelectedColumns.add(gridColumn);
            }
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
                    // And remove from the non transient column list if it is
                    if (entityAttribute.getElement().getValue().isTransient()) {
                        nonTransientSelectedColumns.remove(gridColumn);
                        nonTransientDisplayedColumns.remove(gridColumn);
                        if (nonTransientSelectedWithoutSaveColumns != null) {
                            nonTransientSelectedWithoutSaveColumns.remove(gridColumn);
                        }
                    }
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

    public LinkedHashSet<GridColumn> getIdColumns() {
        return idColumns;
    }

    public LinkedHashSet<GridColumn> getEditableColumns() {
        return editableColumns;
    }

    public LinkedHashSet<GridColumn> getNonTransientSelectedWithoutSaveColumns() {
        return nonTransientSelectedWithoutSaveColumns;
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

    public boolean isSingleSelection() {
        return singleSelection;
    }

    public JsExpression getOnOpen() {
        return onOpen;
    }

    public List<ToolbarButton> getToolbarButtons() {
        return toolbarButtons;
    }

    public String getTitle() {
        return title;
    }

    public JsExpression getOnSelect() {
        return onSelect;
    }

    public String getServiceSaveClassname() {
        return serviceSaveClassname;
    }

    public String getServiceSaveMethod() {
        return serviceSaveMethod;
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public List<GridHighlight> getHighlights() {
        return highlights;
    }

    public List<Object> getDisplayedColumnsOrCode() {
        return displayedColumnsOrCode;
    }

    public LinkedHashSet<DisplayedGridColumn> getNonTransientDisplayedColumns() {
        return nonTransientDisplayedColumns;
    }

    public LinkedHashSet<GridColumn> getNonTransientSelectedColumns() {
        return nonTransientSelectedColumns;
    }
}
