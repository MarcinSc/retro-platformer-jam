package com.gempukku.secsy.gaming.ai;

import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.gaming.editor.generic.type.StringValueValidator;

public class HasBehaviorValidator implements StringValueValidator {
    @Inject
    private AIEngine aiEngine;

    @Override
    public boolean isValid(String value) {
        return aiEngine.hasBehavior(value);
    }
}
