package com.gempukku.retro.model;

import com.gempukku.retro.render.PlatformEditor;
import com.gempukku.secsy.entity.component.DefaultValue;
import com.gempukku.secsy.gaming.component.Bounds2DComponent;
import com.gempukku.secsy.gaming.editor.EditableWith;

@EditableWith(PlatformEditor.class)
public interface PlatformComponent extends Bounds2DComponent {
    boolean isHorizontal();

    String getTextureAtlasId();

    @DefaultValue("")
    String getBeginningImage();

    void setBeginningImage(String beginningImage);

    @DefaultValue("")
    String getCenterImage();

    void setCenterImage(String centerImage);

    @DefaultValue("")
    String getEndingImage();

    void setEndingImage(String endingImage);
}
