package com.gempukku.secsy.gaming.animation;

public class AnimationFrame {
    private String frame;
    private long duration;

    public AnimationFrame(String frame, long duration) {
        this.frame = frame;
        this.duration = duration;
    }

    public String getFrame() {
        return frame;
    }

    public long getDuration() {
        return duration;
    }
}
