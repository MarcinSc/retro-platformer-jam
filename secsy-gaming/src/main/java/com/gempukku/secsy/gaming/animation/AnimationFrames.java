package com.gempukku.secsy.gaming.animation;

public class AnimationFrames {
    private AnimationFrame[] frames;
    private long length;

    public AnimationFrames(AnimationFrame[] frames) {
        this.frames = frames;
        for (AnimationFrame frame : frames) {
            length += frame.getDuration();
        }
    }

    public String getAnimationFrame(long animationIndex) {
        animationIndex = animationIndex % length;
        for (AnimationFrame frame : frames) {
            animationIndex -= frame.getDuration();
            if (animationIndex < 0)
                return frame.getFrame();
        }

        return null;
    }

    public long getLength() {
        return length;
    }
}
