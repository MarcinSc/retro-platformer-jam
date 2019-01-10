package com.gempukku.secsy.gaming.editor.generic.type;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.gempukku.secsy.context.SystemContext;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.component.InternalComponentManager;
import com.gempukku.secsy.gaming.editor.component.CommonEditors;
import com.gempukku.secsy.gaming.editor.generic.TypeFieldEditor;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

import static com.gempukku.secsy.gaming.editor.generic.type.FieldEditorUtil.getFieldDisplayName;

public class StringFieldEditor implements TypeFieldEditor<String> {
    @Inject
    private InternalComponentManager internalComponentManager;
    @Inject
    private SystemContext systemContext;

    @Override
    public Actor getFieldEditor(Skin skin, final EntityRef entityRef, final Class<? extends Component> componentClass, final String fieldName) {
        Table result = new Table();

        Method getterMethod = internalComponentManager.getGetterMethod(componentClass, fieldName);
        StringValidator validator = getterMethod.getAnnotation(StringValidator.class);
        StringValueValidator stringValueValidator = null;
        if (validator != null) {
            try {
                stringValueValidator = validator.value().newInstance();
                systemContext.initializeObject(stringValueValidator);
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to create StringValueValidator", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to create StringValueValidator", e);
            }
        }

        final StringValueValidator finalStringValueValidator = stringValueValidator;

        String displayName = getFieldDisplayName(internalComponentManager, componentClass, fieldName);

        CommonEditors.appendLabel(result, skin, displayName);
        CommonEditors.appendStringField(result, skin, entityRef, null,
                new Function<EntityRef, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable EntityRef entityRef) {
                        return getValueToDisplay(entityRef, componentClass, fieldName);
                    }
                },
                new Function<TextField, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TextField textField) {
                        String value = textField.getText();
                        if (finalStringValueValidator == null || finalStringValueValidator.isValid(value)) {
                            Component component = entityRef.getComponent(componentClass);
                            internalComponentManager.setComponentFieldValue(component, fieldName, value, false);
                            entityRef.saveChanges();
                            textField.setColor(Color.WHITE);
                        } else {
                            textField.setColor(Color.RED);
                        }

                        return null;
                    }
                });

        return result;
    }

    private String getValueToDisplay(EntityRef entityRef, Class<? extends Component> componentClass, String fieldName) {
        Component component = entityRef.getComponent(componentClass);
        return internalComponentManager.getComponentFieldValue(component, fieldName, String.class);
    }
}
