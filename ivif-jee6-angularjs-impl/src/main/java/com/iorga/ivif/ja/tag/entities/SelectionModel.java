package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.AbstractIntegerEnumUserType;
import com.iorga.ivif.ja.AbstractStringEnumUserType;
import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.tag.AbstractTarget;
import com.iorga.ivif.tag.bean.Option;
import com.iorga.ivif.tag.bean.Selection;
import com.iorga.ivif.util.TargetFileUtils;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class SelectionModel extends AbstractTarget<String, JAGeneratorContext> {
    private final Selection element;
    private final JAGeneratorContext context;

    private List<Option> options;
    private final String fromTypeClassName;
    private final String userTypeSuperClassName;

    public SelectionModel(Selection element, JAGeneratorContext context) {
        super(element.getName());
        this.element = element;
        this.context = context;

        final String fromType = element.getFromType();
        if ("string".equals(fromType)) {
            fromTypeClassName = String.class.getName();
            userTypeSuperClassName = AbstractStringEnumUserType.class.getName();
        } else if ("integer".equals(fromType)) {
            fromTypeClassName = Integer.class.getName();
            userTypeSuperClassName = AbstractIntegerEnumUserType.class.getName();

        } else {
            throw new NotImplementedException("Not yet supported: from-type=" + fromType);
        }

        // Compute options
        options = new ArrayList<>(element.getOption().size());
        for (Option option : element.getOption()) {
            final Option finalOption = new Option();
            final String name = option.getName();
            finalOption.setName(name);
            // compute value
            final String value = option.getValue();
            if (value != null) {
                finalOption.setValue(value);
            } else {
                finalOption.setValue(name);
            }
            // compute title
            final String title = option.getTitle();
            if (title != null) {
                finalOption.setTitle(title);
            } else {
                finalOption.setTitle(TargetFileUtils.getTitleFromCamelCasedName(name));
            }
            options.add(finalOption);
        }
    }

    public List<Option> getOptions() {
        return options;
    }

    public Selection getElement() {
        return element;
    }

    public String getFromTypeClassName() {
        return fromTypeClassName;
    }

    public String getUserTypeSuperClassName() {
        return userTypeSuperClassName;
    }
}
