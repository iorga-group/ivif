package com.iorga.ivif.ja.tag;

public class RenderPart {
    protected String freemarkerTemplateName;
    protected Object model;

    public RenderPart() {}

    public RenderPart(String freemarkerTemplateName, Object model) {
        this.freemarkerTemplateName = freemarkerTemplateName;
        this.model = model;
    }

    public String getFreemarkerTemplateName() {
        return freemarkerTemplateName;
    }

    public void setFreemarkerTemplateName(String freemarkerTemplateName) {
        this.freemarkerTemplateName = freemarkerTemplateName;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }
}
