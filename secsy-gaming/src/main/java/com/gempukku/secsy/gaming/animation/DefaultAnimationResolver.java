package com.gempukku.secsy.gaming.animation;

public class DefaultAnimationResolver implements AnimationResolver {
    private String animation;
    private long startTime;

    public DefaultAnimationResolver(String animation, long startTime) {
        this.startTime = startTime;
        this.animation = animation;
    }

    @Override
    public AnimationFrame getAnimationFrame(long currentTime) {
        long timeFromStart = currentTime - startTime;
        return new DefaultAnimationFrame(animation, timeFromStart);
    }

    private class DefaultAnimationFrame implements AnimationFrame {
        private String animation;
        private long animationIndex;

        private DefaultAnimationFrame(String animation, long animationIndex) {
            this.animation = animation;
            this.animationIndex = animationIndex;
        }

        @Override
        public String getAnimation() {
            return animation;
        }

        @Override
        public long getAnimationIndex() {
            return animationIndex;
        }
    }
}
