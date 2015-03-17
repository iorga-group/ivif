<#assign selectionModel=model.selectionModel>
<#assign selection=selectionModel.element>
<#assign isString=(selection.fromType == "string")>
@${util.useClass("javax.xml.bind.annotation.XmlEnum", false)}
public enum ${selection.name} implements ${util.useClass("com.iorga.ivif.ja.Valuable", false)}<${util.useClass(selectionModel.fromTypeClassName, false)}> {
<#list selectionModel.options as option>
    @${util.useClass("javax.xml.bind.annotation.XmlEnumValue")}("${option.value}")
    ${option.name}(<#if isString>"</#if>${option.value}<#if isString>"</#if>)<#if option_has_next>,<#else>;</#if>
</#list>

    private ${util.useClass(selectionModel.fromTypeClassName)} value;
    private static ${util.useClass("java.util.Map")}<${util.useClass(selectionModel.fromTypeClassName)}, ${selection.name}> selectionByValue = new ${util.useClass("java.util.HashMap")}<>();

    static {
<#list selectionModel.options as option>
        selectionByValue.put(<#if isString>"</#if>${option.value}<#if isString>"</#if>, ${option.name});
</#list>
    }

    public static class UserType extends ${util.useClass(selectionModel.userTypeSuperClassName)}<${selection.name}> {

        @${util.useClass("java.lang.Override")}
        protected ${selection.name} getByValue(${util.useClass(selectionModel.fromTypeClassName)} value) {
            return ${selection.name}.byValue(value);
        }
    }


    ${selection.name}(${util.useClass(selectionModel.fromTypeClassName)} value) {
        this.value = value;
    }

    @${util.useClass("org.codehaus.jackson.annotate.JsonCreator")}
    public static ${selection.name} byValue(${util.useClass(selectionModel.fromTypeClassName)} value) {
        return selectionByValue.get(value);
    }

    @${util.useClass("org.codehaus.jackson.annotate.JsonValue")}
    public ${util.useClass(selectionModel.fromTypeClassName)} value() {
        return value;
    }

}