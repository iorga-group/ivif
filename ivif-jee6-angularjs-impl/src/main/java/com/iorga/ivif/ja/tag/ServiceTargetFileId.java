package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.JavaTargetFile.JavaTargetFileId;

public class ServiceTargetFileId extends JavaTargetFileId {

    public ServiceTargetFileId(String simpleOrFullClassName, String packageNameOrNull, JAGeneratorContext context) {
        super(simpleOrFullClassName, packageNameOrNull, "service", context);
    }

}
