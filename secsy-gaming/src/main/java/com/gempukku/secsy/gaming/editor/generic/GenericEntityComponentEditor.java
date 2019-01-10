package com.gempukku.secsy.gaming.editor.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.component.ComponentFieldConverter;
import com.gempukku.secsy.entity.component.InternalComponentManager;
import com.gempukku.secsy.gaming.editor.EntityComponentEditor;
import com.gempukku.secsy.gaming.editor.component.CommonEditors;

import java.util.Arrays;
import java.util.Map;

public class GenericEntityComponentEditor implements EntityComponentEditor {
    private String componentName;
    private Class<? extends Component> componentClass;

    @Inject
    private InternalComponentManager internalComponentManager;
    @Inject
    private TypeFieldEditors typeFieldEditors;
    @Inject
    private ComponentFieldConverter componentFieldConverter;

    public GenericEntityComponentEditor(String componentName, Class<? extends Component> componentClass) {
        this.componentName = componentName;
        this.componentClass = componentClass;
    }

    @Override
    public void appendEditor(Table table, Skin skin, final EntityRef entityRef, PositionUpdateCallback positionUpdateCallback) {
        Table groupTable = new Table(skin);
        CommonEditors.initializeGroupTable(groupTable, skin);

        groupTable.add(new Label(getDisplayName(), skin)).growX().colspan(2);
        groupTable.row();

        Map<String, Class<?>> componentFieldTypes = internalComponentManager.getComponentFieldTypes(componentClass);

        for (String fieldName : getFieldsToDisplay(componentFieldTypes)) {
            final Class<?> fieldClass = componentFieldTypes.get(fieldName);

            TypeFieldEditor<?> fieldEditor = typeFieldEditors.getEditorForField(fieldClass);
            Actor editorActor = fieldEditor.getFieldEditor(skin, entityRef, componentClass, fieldName);
            groupTable.add(editorActor).growX();
            groupTable.row();
        }

        table.add(groupTable).growX();
        table.row();
    }

    private String getDisplayName() {
        EditorName editorName = componentClass.getAnnotation(EditorName.class);
        if (editorName != null)
            return editorName.value();

        return componentName;
    }

    private Iterable<String> getFieldsToDisplay(Map<String, Class<?>> componentFieldTypes) {
        EditorFields editorFields = componentClass.getAnnotation(EditorFields.class);
        Iterable<String> fieldNames;
        if (editorFields != null)
            fieldNames = Arrays.asList(editorFields.value());
        else
            fieldNames = componentFieldTypes.keySet();
        return fieldNames;
    }

    @Override
    public void entityMoved(EntityRef entityRef, float x, float y) {

    }

    @Override
    public void serializeChanges(EntityRef entityRef, Map<String, Object> changes, Map<String, Map<String, Object>> extraChanges) {
        Component component = entityRef.getComponent(componentClass);
        Map<String, Class<?>> componentFieldTypes = internalComponentManager.getComponentFieldTypes(componentClass);
        for (String fieldName : getFieldsToDisplay(componentFieldTypes)) {
            Class<?> fieldClass = componentFieldTypes.get(fieldName);

            Object value = internalComponentManager.getComponentFieldValue(component, fieldName, fieldClass);
            changes.put(fieldName, value);
        }
    }
}
