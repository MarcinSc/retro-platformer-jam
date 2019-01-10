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

public class BooleanFieldEditor implements TypeFieldEditor<Boolean> {
    @Inject
    private InternalComponentManager internalComponentManager;

    @Override
    public Actor getFieldEditor(Skin skin, final EntityRef entityRef, final Class<? extends Component> componentClass, final String fieldName) {
        Table result = new Table();

        String displayName = getFieldDisplayName(internalComponentManager, componentClass, fieldName);

        CommonEditors.appendCheckbox(result, skin, displayName, entityRef,
                new Function<EntityRef, Boolean>() {
                    @Nullable
                    @Override
                    public Boolean apply(@Nullable EntityRef entityRef) {
                        return getValueToDisplay(entityRef, componentClass, fieldName);
                    }
                },
                new Function<Boolean, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Boolean value) {
                        Component component = entityRef.getComponent(componentClass);
                        internalComponentManager.setComponentFieldValue(component, fieldName, value, false);
                        entityRef.saveChanges();
                        return null;
                    }
                });

        return result;
    }

    private boolean getValueToDisplay(EntityRef entityRef, Class<? extends Component> componentClass, String fieldName) {
        Component component = entityRef.getComponent(componentClass);
        return internalComponentManager.getComponentFieldValue(component, fieldName, Boolean.class);
    }
}
