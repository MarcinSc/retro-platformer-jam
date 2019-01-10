package com.gempukku.secsy.gaming.editor.generic.type;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.component.InternalComponentManager;
import com.gempukku.secsy.gaming.editor.component.CommonEditors;
import com.gempukku.secsy.gaming.editor.generic.TypeFieldEditor;
import com.google.common.base.Function;

import javax.annotation.Nullable;

import static com.gempukku.secsy.gaming.editor.generic.type.FieldEditorUtil.getFieldDisplayName;

public class FloatFieldEditor implements TypeFieldEditor<Float> {
    @Inject
    private InternalComponentManager internalComponentManager;

    @Override
    public Actor getFieldEditor(Skin skin, final EntityRef entityRef, final Class<? extends Component> componentClass, final String fieldName) {
        Table result = new Table();

        String displayName = getFieldDisplayName(internalComponentManager, componentClass, fieldName);

        CommonEditors.appendLabel(result, skin, displayName);
        CommonEditors.appendFloatField(result, skin, entityRef,
                new Function<EntityRef, Float>() {
                    @Nullable
                    @Override
                    public Float apply(@Nullable EntityRef entityRef) {
                        Component component = entityRef.getComponent(componentClass);
                        return internalComponentManager.getComponentFieldValue(component, fieldName, Float.class);
                    }
                },
                new Function<Float, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Float value) {
                        Component component = entityRef.getComponent(componentClass);
                        internalComponentManager.setComponentFieldValue(component, fieldName, value, false);
                        entityRef.saveChanges();
                        return null;
                    }
                });

        return result;
    }

}
