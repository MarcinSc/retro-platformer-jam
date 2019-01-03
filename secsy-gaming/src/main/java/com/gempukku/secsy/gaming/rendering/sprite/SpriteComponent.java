package com.gempukku.secsy.gaming.rendering.sprite;

import com.gempukku.secsy.gaming.editor.EditableWith;
import com.gempukku.secsy.gaming.editor.component.SpriteEditor;

@EditableWith(SpriteEditor.class)
public interface SpriteComponent extends DisplayableSpriteComponent {
    String getFileName();

    void setFileName(String fileName);
}
