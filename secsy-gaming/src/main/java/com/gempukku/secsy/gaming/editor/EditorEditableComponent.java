package com.gempukku.secsy.gaming.editor;

import com.gempukku.secsy.entity.Component;

import java.util.List;

public interface EditorEditableComponent extends Component {
    List<String> getEditableComponents();
}
