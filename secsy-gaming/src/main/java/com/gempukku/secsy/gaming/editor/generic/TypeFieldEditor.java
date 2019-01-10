package com.gempukku.secsy.gaming.editor.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityRef;

public interface TypeFieldEditor<T> {
    Actor getFieldEditor(Skin skin, EntityRef entityRef, Class<? extends Component> componentClass, String fieldName);
}
