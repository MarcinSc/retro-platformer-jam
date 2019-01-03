package com.gempukku.secsy.gaming.movement;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;
import com.gempukku.secsy.gaming.easing.EasedValue;
import com.gempukku.secsy.gaming.editor.EditableWith;

@EditableWith(OscillatingEditor.class)
public interface OscillatingComponent extends Component {
    Vector2 getStartingPosition();

    void setStartingPosition(Vector2 startingPosition);

    @DefaultValue("0,0")
    Vector2 getDistance();

    void setDistance(Vector2 distance);

    long getCycleLength();

    void setCycleLength(long cycleLength);

    @DefaultValue("1*0-1-0")
    EasedValue getDistanceTimeFunction();

    void setDistanceTimeFunction(EasedValue distanceTimeFunction);
}
