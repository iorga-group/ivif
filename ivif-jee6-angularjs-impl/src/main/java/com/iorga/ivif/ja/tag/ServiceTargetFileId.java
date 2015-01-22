package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.JavaTargetFile.JavaTargetFileId;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;

public class ServiceTargetFileId extends JavaTargetFileId {

    public ServiceTargetFileId(String simpleOrFullClassName, String packageNameOrNull, JAConfiguration configuration) {
        super(simpleOrFullClassName, packageNameOrNull, "service", configuration);
    }

}
