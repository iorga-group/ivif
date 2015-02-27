package com.iorga.ivif.ja.tag.views;

import com.google.common.collect.Lists;
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
    protected EntityTargetFileId entityTargetFileId;
    protected ServiceTargetFileId serviceTargetFileId;

    protected Map<String, GridColumn> gridColumnsByRef;

    protected LinkedHashSet<GridColumn> editableGridColumns;
    protected LinkedHashSet<GridColumn> resultGridColumns;
    protected LinkedHashSet<GridColumn> filterGridColumns;
    protected LinkedHashSet<GridColumn> sortableGridColumns;

    /**
     * Editable grid columns (not shared with results & filters) + intersection between results & edit => transient fields + [id + version which are not displayed + column brought by "editable-if" expressions]
     */
    protected LinkedHashSet<GridColumn> editableOnlyAndResultIntersectionGridColumns;
    /**
     * Results grid columns (not shared with editable & filters) + intersection between results & edit => column to display (which are not filterable and not transient) + columns brought from the expressions in buttons and highlights + [id + version which are not displayed + column brought by "editable-if" expressions]
     */
    protected LinkedHashSet<GridColumn> resultOnlyAndEditableIntersectionGridColumns;
    /**
     * Intersection between filters & results (without editable) grid columns => column to display, filterable but not editable
     */
    protected LinkedHashSet<GridColumn> filterResultIntersectionGridColumns;
    /**
     * Filters grid columns (not shared with editable & results) => column which are filter only (not to display) + column brought by actions + query parameters
     */
    protected LinkedHashSet<GridColumn> filterOnlyGridColumns;
    /**
     * Intersection of editable / results / filters grid columns => column to display which are editable and filterable
     */
    protected LinkedHashSet<GridColumn> editableResultFilterIntersectionGridColumns;
    /**
     * Editable grid columns + intersection between editable / results / filters => transient fields + [column to display which are editable and filterable]
     */
    protected LinkedHashSet<GridColumn> editableOnlyAndEditableResultFilterIntersectionGridColumns;

    protected List<Object> displayedColumnsOrCode;
    protected List<DisplayedGridColumn> displayedColumns;
    protected List<GridColumnFilterParam> columnFilterParams;

    protected LinkedHashSet<GridColumn> idColumns;
    protected GridColumn versionColumn;

    protected QueryModel queryModel;

    protected boolean singleSelection;
    protected JsExpression onOpen;
    protected List<ToolbarButton> toolbarButtons;
    protected List<Object> toolbarButtonsOrCode;
    protected String title;
    protected JsExpression onSelect;

    protected String serviceSaveClassName;
    protected String serviceSaveMethod;
    protected String serviceSearchClassName;
    protected String serviceSearchMethod;
    protected String tabTitle;

    protected List<GridHighlight> highlights;

    public static class GridColumnFilterParam {
        private String name;
        private String className;

        public GridColumnFilterParam(String name, String className) {
            this.name = name;
            this.className = className;
        }

        public String getName() {
            return name;
        }

        public String getClassName() {
            return className;
        }
    }

    public static class GridColumn {
        protected String refVariableName;
        protected String ref;
        private EntityAttribute entityAttribute;
        private List<EntityAttribute> refEntityAttributes = new ArrayList<>();
        public boolean resultToResolve = false;
        public boolean filterToResolve = false;
        public boolean sortableToResolve = false;

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
        private final String name;

        public ToolbarButton(Button element, JsExpression actionExpression, JsExpression disabledIfExpression, String name) {
            this.element = element;
            this.actionExpression = actionExpression;
            this.disabledIfExpression = disabledIfExpression;
            rolesAllowed = new ArrayList<>();
            this.name = name;
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

        public String getName() {
            return name;
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
        gridColumnsByRef = new HashMap<>();

        editableGridColumns = new LinkedHashSet<>();
        resultGridColumns = new LinkedHashSet<>();
        filterGridColumns = new LinkedHashSet<>();
        sortableGridColumns = new LinkedHashSet<>();

        editableOnlyAndResultIntersectionGridColumns = null;
        resultOnlyAndEditableIntersectionGridColumns = null;
        filterResultIntersectionGridColumns = null;
        filterOnlyGridColumns = null;
        editableResultFilterIntersectionGridColumns = null;
        editableOnlyAndEditableResultFilterIntersectionGridColumns = null;

        displayedColumnsOrCode = new ArrayList<>();
        displayedColumns = new ArrayList<>();

        idColumns = new LinkedHashSet<>();
        versionColumn = null;

        // Handle selection
        singleSelection = SelectionType.SINGLE.equals(element.getSelection());

        highlights = new ArrayList<>();

        // Handle service method bypass
        final String serviceSaveMethod = StringUtils.trim(element.getServiceSaveMethod());
        if (StringUtils.isNotBlank(serviceSaveMethod)) {
            this.serviceSaveClassName = StringUtils.substringBeforeLast(serviceSaveMethod, ".");
            this.serviceSaveMethod = StringUtils.substringAfterLast(serviceSaveMethod, ".");
        }

        String serviceSearchMethod = StringUtils.trim(element.getServiceSearchMethod());
        if (StringUtils.isNotBlank(serviceSearchMethod)) {
            this.serviceSearchClassName = StringUtils.substringBeforeLast(serviceSearchMethod, ".");
            this.serviceSearchMethod = StringUtils.substringAfterLast(serviceSearchMethod, ".");
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
                        displayedColumnsOrCode.add(displayedGridColumn);
                        prepareNewGridColumn(displayedGridColumn, displayedGridColumn.isEditable(), true, true, true, context, configuration);
                    } else {
                        // this is a <code> element
                        String code = ((Element) columnOrCode).getTextContent();
                        displayedColumnsOrCode.add(parseCode(code, "line", "line.$original", context, configuration));
                    }
                }

                // Add columns for simple column-filters
                for (ColumnFilter columnFilter : element.getColumnFilter()) {
                    addColumnForRefIfNecessary(columnFilter.getRef(), false, true, false, configuration, context);
                }

                columnFilterParams = new ArrayList<>();
                // Handle column-filter-params
                for (ColumnFilterParam columnFilterParam : element.getColumnFilterParam()) {
                    // TODO handle enum type like 'type="enum[MyEnum]"
                    final String paramType = columnFilterParam.getType();
                    final Class<?> typeClass = EntityTargetFile.ATTRIBUTE_TYPES_TO_CLASS.get(paramType);
                    if (typeClass == null) {
                        throw new IllegalStateException("column-filter-param type cannot be resolved to a Java class: "+paramType);
                    }
                    columnFilterParams.add(new GridColumnFilterParam(columnFilterParam.getName(), typeClass.getName()));
                }

                // Add columns selected by actions
                onOpen = addResultColumnForActionIfNecessary(element.getOnOpen(), "selectedLine", "selectedLine.$original", configuration, context);
                onSelect = addResultColumnForActionIfNecessary(element.getOnSelect(), "selectedLine", "selectedLine.$original", configuration, context);
                // Handle toolbar
                toolbarButtons = new ArrayList<>();
                toolbarButtonsOrCode = new ArrayList<>();
                int buttonNumber = 1;
                final Toolbar toolbar = element.getToolbar();
                if (toolbar != null) {
                    for (Object buttonOrCode : toolbar.getButtonOrCode()) {
                        if (buttonOrCode instanceof Button) {
                            Button button = (Button) buttonOrCode;
                            final JsExpression actionExpression = addResultColumnForActionIfNecessary(button.getAction(), "$scope.selectedLine", "$scope.selectedLine.$original", configuration, context);
                            String disabledIfStr = button.getDisabledIf();
                            if (actionExpression != null && !actionExpression.getLineRefs().isEmpty()) {
                                // Button will be disabled if no line is selected
                                disabledIfStr = "!selectedLine" + (StringUtils.isNotBlank(disabledIfStr) ? " || (selectedLine && ("+disabledIfStr+"))" : "");
                            }
                            final JsExpression disabledIfExpression = addResultColumnForExpressionIfNecessary(disabledIfStr, "selectedLine", "selectedLine.$original", configuration, context);
                            final ToolbarButton toolbarButton = new ToolbarButton(button, actionExpression, disabledIfExpression, ""+(buttonNumber++)); // TODO use title to "name" the button
                            toolbarButtons.add(toolbarButton);
                            toolbarButtonsOrCode.add(toolbarButton);
                            // Now compute the roles allowed if any
                            if (actionExpression != null) {
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
                        } else {
                            // this is a <code> element
                            final String code = ((Element) buttonOrCode).getTextContent();
                            toolbarButtonsOrCode.add(parseCode(code, "selectedLine", "selectedLine.$original", context, configuration));
                        }
                    }
                }

                // Compute highlights
                for (Highlight highlight : element.getHighlight()) {
                    final JsExpression expression = addResultColumnForExpressionIfNecessary(highlight.getIf(), "line", "line.$record", configuration, context);
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
                                addNewIdResultColumnIfNecessary(entityAttribute, false, context, configuration);
                            }
                        }
                    });
                }

                if (element.isEditable()) {
                    // Parse the potential editable-if expression of displayed columns
                    for (DisplayedGridColumn displayedColumn : displayedColumns) {
                        displayedColumn.setEditableIfExpression(addResultColumnForExpressionIfNecessary(displayedColumn.getElement().getEditableIf(), "line", "line.$original", configuration, context));
                    }

                    context.waitForEvent(new TargetPreparedWaiter<EntityTargetFile, EntityTargetFileId, JAGeneratorContext>(EntityTargetFile.class, entityTargetFileId, GridModel.this) {
                        @Override
                        protected void onTargetPrepared(EntityTargetFile entityTargetFile) throws Exception {
                            // If the grid is editable, we must be sure that id columns & version columns of the entity is selected

                            // Add id columns if not specified on selected columns
                            for (EntityAttribute entityAttribute : entityTargetFile.getIdAttributes()) {
                                addNewIdResultColumnIfNecessary(entityAttribute, true, context, configuration);
                            }
                            final EntityAttribute versionAttribute = entityTargetFile.getVersionAttribute();
                            if (versionAttribute != null) {
                                final GridColumn gridColumn = addNewResultColumnIfNecessary(versionAttribute, true, context, configuration);
                                versionColumn = gridColumn;
                            }
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

    private String parseCode(String code, String dollarLineReplacement, String dollarRecordReplacement, JAGeneratorContext context, JAConfiguration configuration) throws Exception {
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
                addResultColumnForRefIfNecessary(fieldsRef, configuration, context); // add them as a select if necessary
                refBuilder.append("." + fieldsRef.replaceAll("\\.", "_"));
            }
            if (ref.startsWith("$line")) {
                refBuilder.insert(0, dollarLineReplacement);
            } else if (ref.startsWith("$record")) {
                refBuilder.insert(0, dollarRecordReplacement);
            }
            matcher.appendReplacement(codeBuffer, refBuilder.toString());
        }
        matcher.appendTail(codeBuffer);
        return codeBuffer.toString();
    }

    private void prepareNewGridColumn(GridColumn gridColumn, boolean editable, boolean filter, boolean result, boolean sortable, JAGeneratorContext context, JAConfiguration configuration) throws Exception {
        gridColumnsByRef.put(gridColumn.ref, gridColumn);

        if (gridColumn.entityAttribute == null) {
            // entity attribute is not yet resolved, result (depending on transient), filter (depending on the field type + transient) and sortable (depending on transient) should be resolved later, but added new anyways
            if (filter) {
                gridColumn.filterToResolve = true;
            }
            if (result) {
                gridColumn.resultToResolve = true;
            }
            if (sortable) {
                gridColumn.sortableToResolve = true;
            }
        }

        updateLists(gridColumn, editable, filter, result, sortable);

        if (gridColumn.entityAttribute == null) {
            // Ask to resolve later
            Deque<String> refPath = new LinkedList<>(Arrays.asList(gridColumn.ref.split("\\.")));
            waitToPrepareGridColumnRecursive(refPath, entityTargetFileId, gridColumn, context, configuration);
        }
    }

    private void updateLists(GridColumn gridColumn, boolean editable, boolean filter, boolean result, boolean sortable) {
        if (editable) {
            editableGridColumns.add(gridColumn);
        }
        if (filter) {
            filterGridColumns.add(gridColumn);
        }
        if (result) {
            resultGridColumns.add(gridColumn);
        }
        if (sortable) {
            sortableGridColumns.add(gridColumn);
        }
    }

    private static final Set<String> FILTERABLE_IVIF_TYPES = new HashSet<>(Lists.newArrayList(new String[]{"string", "integer", "enum"}));
    private boolean isFilterable(GridColumn gridColumn) {
        return gridColumn.entityAttribute != null && FILTERABLE_IVIF_TYPES.contains(gridColumn.entityAttribute.getElement().getName().getLocalPart());
    }

    private boolean isTransient(DisplayedGridColumn displayedGridColumn) {
        final EntityAttribute entityAttribute = displayedGridColumn.getEntityAttribute();
        return entityAttribute != null && entityAttribute.getElement().getValue().isTransient();
    }

    private GridColumn addNewIdResultColumnIfNecessary(EntityAttribute entityAttribute, boolean editable, JAGeneratorContext context, JAConfiguration configuration) throws Exception {
        final GridColumn gridColumn = addNewResultColumnIfNecessary(entityAttribute, editable, context, configuration);
        idColumns.add(gridColumn);
        return gridColumn;
    }

    private GridColumn addNewResultColumnIfNecessary(EntityAttribute entityAttribute, boolean editable, JAGeneratorContext context, JAConfiguration configuration) throws Exception {
        final String attributeName = entityAttribute.getElement().getValue().getName();
        GridColumn gridColumn = gridColumnsByRef.get(attributeName);
        if (gridColumn == null) {
            // Add this non already added id column
            gridColumn = new GridColumn(entityAttribute);
            prepareNewGridColumn(gridColumn, editable, false, true, false, context, configuration);
        } else {
            updateLists(gridColumn, editable, false, true, false);
        }
        return gridColumn;
    }

    private JsExpression addResultColumnForExpressionIfNecessary(String expression, String dollarLineReplacement, String dollarRecordReplacement, JAConfiguration configuration, JAGeneratorContext context) throws Exception {
        if (StringUtils.isNotBlank(expression)) {
            final JsExpression jsExpression = JsExpressionParser.parseExpression(expression, dollarLineReplacement, dollarRecordReplacement);

            addResultColumnsForExpressionIfNecessary(jsExpression, configuration, context);
            return jsExpression;
        } else {
            return null;
        }
    }

    private void addResultColumnsForExpressionIfNecessary(JsExpression jsExpression, JAConfiguration configuration, JAGeneratorContext context) throws Exception {
        for (LineRef lineRef : jsExpression.getLineRefs()) {
            final String ref = lineRef.getRef();
            addResultColumnForRefIfNecessary(ref, configuration, context);
        }
    }

    private void addResultColumnForRefIfNecessary(String ref, JAConfiguration configuration, JAGeneratorContext context) throws Exception {
        addColumnForRefIfNecessary(ref, false, false, true, configuration, context);
    }

    private void addColumnForRefIfNecessary(String ref, boolean editable, boolean filter, boolean result, JAConfiguration configuration, JAGeneratorContext context) throws Exception {
        GridColumn gridColumn = gridColumnsByRef.get(ref);
        if (gridColumn == null) {
            // Add this non already added id column
            gridColumn = new GridColumn(ref);
            prepareNewGridColumn(gridColumn, editable, filter, result, false, context, configuration);
        } else {
            updateLists(gridColumn, editable, filter, result, false);
        }
    }

    private JsExpression addResultColumnForActionIfNecessary(String expression, String dollarLineReplacement, String dollarRecordReplacement, JAConfiguration configuration, JAGeneratorContext context) throws Exception {
        if (StringUtils.isNotBlank(expression)) {
            final JsExpression jsExpression = JsExpressionParser.parseActions(expression, dollarLineReplacement, dollarRecordReplacement);

            addResultColumnsForExpressionIfNecessary(jsExpression, configuration, context);
            return jsExpression;
        } else {
            return null;
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
                    // Now, fix the repartition of that grid column if previously not resolved and put in wrong lists
                    // It was previously considered as not transient and not filtered. Must change lists if now it is not the case anymore
                    if (entityAttribute.getElement().getValue().isTransient()) {
                        // It has become transient, must remove from the filters & results & sortable
                        if (gridColumn.filterToResolve) {
                            filterGridColumns.remove(gridColumn);
                        }
                        if (gridColumn.resultToResolve) {
                            resultGridColumns.remove(gridColumn);
                        }
                        if (gridColumn.sortableToResolve) {
                            sortableGridColumns.remove(gridColumn);
                        }
                    } else if (isFilterable(gridColumn)) {
                        // It has become filterable, add it to filters
                        if (gridColumn.filterToResolve) {
                            filterGridColumns.add(gridColumn);
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

    public LinkedHashSet<GridColumn> getEditableOnlyAndResultIntersectionGridColumns() {
        if (editableOnlyAndResultIntersectionGridColumns == null) {
            // compute it
            editableOnlyAndResultIntersectionGridColumns = new LinkedHashSet<>(editableGridColumns);
            editableOnlyAndResultIntersectionGridColumns.removeAll(filterGridColumns);
        }
        return editableOnlyAndResultIntersectionGridColumns;
    }

    public LinkedHashSet<GridColumn> getResultOnlyAndEditableIntersectionGridColumns() {
        if (resultOnlyAndEditableIntersectionGridColumns == null) {
            // compute it
            resultOnlyAndEditableIntersectionGridColumns = new LinkedHashSet<>(resultGridColumns);
            resultOnlyAndEditableIntersectionGridColumns.removeAll(filterGridColumns);
        }
        return resultOnlyAndEditableIntersectionGridColumns;
    }

    public LinkedHashSet<GridColumn> getFilterResultIntersectionGridColumns() {
        if (filterResultIntersectionGridColumns == null) {
            // compute it
            filterResultIntersectionGridColumns = new LinkedHashSet<>(filterGridColumns);
            filterResultIntersectionGridColumns.retainAll(resultGridColumns);
            filterResultIntersectionGridColumns.removeAll(editableGridColumns);
        }
        return filterResultIntersectionGridColumns;
    }

    public LinkedHashSet<GridColumn> getFilterOnlyGridColumns() {
        if (filterOnlyGridColumns == null) {
            // compute it
            filterOnlyGridColumns = new LinkedHashSet<>(filterGridColumns);
            filterOnlyGridColumns.removeAll(editableGridColumns);
            filterOnlyGridColumns.removeAll(resultGridColumns);
        }
        return filterOnlyGridColumns;
    }

    public LinkedHashSet<GridColumn> getEditableResultFilterIntersectionGridColumns() {
        if (editableResultFilterIntersectionGridColumns == null) {
            // compute it
            editableResultFilterIntersectionGridColumns = new LinkedHashSet<>(editableGridColumns);
            editableResultFilterIntersectionGridColumns.retainAll(filterGridColumns);
            editableResultFilterIntersectionGridColumns.retainAll(resultGridColumns);
        }
        return editableResultFilterIntersectionGridColumns;
    }

    public LinkedHashSet<GridColumn> getEditableOnlyAndEditableResultFilterIntersectionGridColumns() {
        if (editableOnlyAndEditableResultFilterIntersectionGridColumns == null) {
            // compute it
            editableOnlyAndEditableResultFilterIntersectionGridColumns = new LinkedHashSet<>(editableGridColumns);
            editableOnlyAndEditableResultFilterIntersectionGridColumns.removeAll(getResultGridColumns());
            editableOnlyAndEditableResultFilterIntersectionGridColumns.addAll(getEditableResultFilterIntersectionGridColumns());
        }
        return editableOnlyAndEditableResultFilterIntersectionGridColumns;
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

    public List<DisplayedGridColumn> getDisplayedColumns() {
        return displayedColumns;
    }

    public Grid getElement() {
        return element;
    }

    public LinkedHashSet<GridColumn> getIdColumns() {
        return idColumns;
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

    public String getServiceSaveClassName() {
        return serviceSaveClassName;
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

    public LinkedHashSet<GridColumn> getEditableGridColumns() {
        return editableGridColumns;
    }

    public LinkedHashSet<GridColumn> getFilterGridColumns() {
        return filterGridColumns;
    }

    public LinkedHashSet<GridColumn> getResultGridColumns() {
        return resultGridColumns;
    }

    public LinkedHashSet<GridColumn> getSortableGridColumns() {
        return sortableGridColumns;
    }

    public List<GridColumnFilterParam> getColumnFilterParams() {
        return columnFilterParams;
    }

    public String getServiceSearchClassName() {
        return serviceSearchClassName;
    }

    public String getServiceSearchMethod() {
        return serviceSearchMethod;
    }

    public List<Object> getToolbarButtonsOrCode() {
        return toolbarButtonsOrCode;
    }
}
