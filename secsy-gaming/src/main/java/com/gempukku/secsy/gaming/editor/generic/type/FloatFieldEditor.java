package com.gempukku.secsy.gaming.editor.generic.type;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
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

public class FloatFieldEditor implements TypeFieldEditor<Float> {
    @Inject
    private InternalComponentManager internalComponentManager;

    private boolean settingValue;

    @Override
    public Actor getFieldEditor(Skin skin, final EntityRef entityRef, final Class<? extends Component> componentClass, final String fieldName) {
        Table result = new Table();

        String displayName = getFieldDisplayName(internalComponentManager, componentClass, fieldName);

        Method getterMethod = internalComponentManager.getGetterMethod(componentClass, fieldName);
        final FloatRange floatRange = getterMethod.getAnnotation(FloatRange.class);
        Function<EntityRef, Float> fieldValueFunction = getFieldValueFunction(componentClass, fieldName);
        if (floatRange == null) {
            createTextFieldEditor(result, skin, entityRef, componentClass, fieldName, displayName, fieldValueFunction);
        } else {
            CommonEditors.appendLabel(result, skin, displayName);
            final Slider floatSlider = getFloatSlider(skin, entityRef, floatRange, fieldValueFunction);
            final TextField floatField = getFloatField(skin, entityRef, fieldValueFunction);

            floatSlider.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            if (!settingValue) {
                                settingValue = true;
                                float value = floatSlider.getValue();
                                setValueOnEntity(value, entityRef, componentClass, fieldName);
                                floatField.setText(String.valueOf(value));
                                settingValue = false;
                            }
                        }
                    });
            floatField.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            if (!settingValue) {
                                settingValue = true;
                                try {
                                    float value = Float.parseFloat(floatField.getText());
                                    if (floatRange.min() <= value && value <= floatRange.max()) {
                                        setValueOnEntity(value, entityRef, componentClass, fieldName);
                                        floatField.setColor(Color.WHITE);
                                        floatSlider.setValue(value);
                                    } else {
                                        floatField.setColor(Color.RED);
                                    }
                                } catch (NumberFormatException exp) {
                                    // Ignore
                                    floatField.setColor(Color.RED);
                                }
                                settingValue = false;
                            }
                        }
                    });

            result.add(floatSlider).growX();
            result.add(floatField);
        }

        return result;
    }

    private void setValueOnEntity(float value, EntityRef entityRef, Class<? extends Component> componentClass, String fieldName) {
        Component component = entityRef.getComponent(componentClass);
        internalComponentManager.setComponentFieldValue(component, fieldName, value, false);
        entityRef.saveChanges();
    }

    private void createTextFieldEditor(Table result, Skin skin, final EntityRef entityRef, final Class<? extends Component> componentClass, final String fieldName, String displayName, Function<EntityRef, Float> fieldValue) {
        CommonEditors.appendLabel(result, skin, displayName);
        final TextField floatField = getFloatField(skin, entityRef, fieldValue);
        floatField.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        try {
                            float value = Float.parseFloat(floatField.getText());
                            setValueOnEntity(value, entityRef, componentClass, fieldName);
                            floatField.setColor(Color.WHITE);
                        } catch (NumberFormatException exp) {
                            // Ignore
                            floatField.setColor(Color.RED);
                        }
                    }
                });
        result.add(floatField).growX();
    }

    private Function<EntityRef, Float> getFieldValueFunction(final Class<? extends Component> componentClass, final String fieldName) {
        return new Function<EntityRef, Float>() {
            @Nullable
            @Override
            public Float apply(@Nullable EntityRef entityRef) {
                Component component = entityRef.getComponent(componentClass);
                return internalComponentManager.getComponentFieldValue(component, fieldName, Float.class);
            }
        };
    }

    private Slider getFloatSlider(Skin skin, EntityRef entityRef, FloatRange floatRange,
                                  Function<EntityRef, Float> fieldValue) {
        float diff = floatRange.max() - floatRange.min();
        float stepSize = diff / 100;
        final Slider slider = new Slider(floatRange.min(), floatRange.max(), stepSize, false, skin);
        slider.setValue(fieldValue.apply(entityRef));
        return slider;
    }

    private TextField getFloatField(Skin skin, EntityRef entityRef, Function<EntityRef, Float> fieldValue) {
        final TextField editor = new TextField(fieldValue.apply(entityRef).toString(), skin) {
            @Override
            public float getPrefWidth() {
                return 80;
            }
        };
        editor.setTextFieldFilter(CommonEditors.FLOAT_FILTER);
        editor.setAlignment(Align.right);
        return editor;
    }
}
