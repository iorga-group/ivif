<#assign selectionModel=model.selectionModel>
<#assign selection=selectionModel.element>
@${util.useClass("javax.xml.bind.annotation.XmlEnum", false)}
public enum ${selection.name} implements ${util.useClass("com.iorga.ivif.ja.Valuable", false)}<${util.useClass("java.lang.String", false)}> {
<#list selectionModel.options as option>
    @${util.useClass("javax.xml.bind.annotation.XmlEnumValue")}("${option.value}")
    ${option.name}("${option.value}")<#if option_has_next>,<#else>;</#if>
</#list>

    private ${util.useClass("java.lang.String")} value;
    private static ${util.useClass("java.util.Map")}<${util.useClass("java.lang.String")}, ${selection.name}> selectionByValue = new ${util.useClass("java.util.HashMap")}<>();

    static {
<#list selectionModel.options as option>
        selectionByValue.put("${option.value}", ${option.name});
</#list>
    }

    public static class UserType extends ${util.useClass("com.iorga.ivif.ja.AbstractStringEnumUserType")}<${selection.name}> {

        @${util.useClass("java.lang.Override")}
        protected ${selection.name} getByValue(String value) {
            return ${selection.name}.byValue(value);
        }
    }


    ${selection.name}(String value) {
        this.value = value;
    }

    @${util.useClass("org.codehaus.jackson.annotate.JsonCreator")}
    public static ${selection.name} byValue(String value) {
        return selectionByValue.get(value);
    }

    @${util.useClass("org.codehaus.jackson.annotate.JsonValue")}
    public String value() {
        return value;
    }

}