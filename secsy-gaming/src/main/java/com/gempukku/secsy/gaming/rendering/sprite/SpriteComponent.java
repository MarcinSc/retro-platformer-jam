package com.gempukku.secsy.gaming.rendering.sprite;

import com.gempukku.secsy.gaming.editor.generic.EditorFields;
import com.gempukku.secsy.gaming.editor.generic.type.FileExistsValidator;
import com.gempukku.secsy.gaming.editor.generic.type.StringValidator;

@EditorFields({"fileName", "priority", "leftPerc", "rightPerc", "downPerc", "upPerc"})
public interface SpriteComponent extends DisplayableSpriteComponent {
    @StringValidator(FileExistsValidator.class)
    String getFileName();

    void setFileName(String fileName);
}
