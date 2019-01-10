package com.gempukku.secsy.gaming.editor.generic;

public interface TypeFieldEditors {
    <T> TypeFieldEditor<T> getEditorForField(Class<T> clazz);
}
