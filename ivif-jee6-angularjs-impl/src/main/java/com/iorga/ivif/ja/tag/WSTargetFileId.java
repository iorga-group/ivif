package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.JavaTargetFile.JavaTargetFileId;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;

public class WSTargetFileId extends JavaTargetFileId {

    public WSTargetFileId(String simpleOrFullClassName, JAConfiguration configuration) {
        this(simpleOrFullClassName, null, configuration);
    }

    public WSTargetFileId(String simpleOrFullClassName, String packageNameOrNull, JAConfiguration configuration) {
        super(simpleOrFullClassName, packageNameOrNull, "ws", configuration);
    }
}
