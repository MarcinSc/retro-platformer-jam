package com.gempukku.secsy.gaming.input2d;

import com.gempukku.secsy.entity.Component;
import com.gempukku.secsy.entity.component.DefaultValue;

public interface ControlledByInputComponent extends Component {
    float getJumpImpulse();
    float getMoveSpeed();

    float getJumpAcceleration();

    @DefaultValue("1")
    int getJumpMaxCount();

    void setJumpMaxCount(int jumpMaxCount);

    int getJumpCount();

    void setJumpCount(int jumpCount);

    boolean isJustJumped();

    void setJustJumped(boolean justJumped);

    long getPhysicsJumpTime();

    void setPhysicsJumpTime(long jumpTime);

    long getJumpLength();

    @DefaultValue("200")
    long getJumpGracePeriod();
}
