package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.JavaTargetFile.JavaTargetFileId;

public class WSTargetFileId extends JavaTargetFileId {

    public WSTargetFileId(String simpleOrFullClassName, String packageNameOrNull, JAGeneratorContext context) {
        super(simpleOrFullClassName, packageNameOrNull, "ws", context);
    }
}
