package com.gempukku.retro.render;

import com.gempukku.secsy.gaming.editor.generic.EditorFields;
import com.gempukku.secsy.gaming.editor.generic.EditorName;
import com.gempukku.secsy.gaming.editor.generic.type.EditorField;
import com.gempukku.secsy.gaming.rendering.sprite.SpriteComponent;

@EditorFields({"fileName", "bobbingAmplitude", "priority", "leftPerc", "rightPerc", "downPerc", "upPerc"})
@EditorName("Bobbing Sprite")
public interface BobbingSpriteComponent extends SpriteComponent {
    @EditorField("Amplitude")
    float getBobbingAmplitude();

    void setBobbingAmplitude(float bobbingAmplitude);
}
