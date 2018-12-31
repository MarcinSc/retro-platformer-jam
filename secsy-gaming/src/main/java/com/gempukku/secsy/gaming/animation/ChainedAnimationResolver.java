package com.gempukku.secsy.gaming.animation;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

public class ChainedAnimationResolver implements AnimationResolver {
    private Deque<String> animationQueue = new LinkedList<String>();
    private long currentAnimationStart;

    public ChainedAnimationResolver(String animation) {
        replaceAnimations(animation);
    }

    @Override
    public AnimationFrame getAnimationFrame(Map<String, AnimationFrames> animationFramesMap, long currentTime) {
        if (currentAnimationStart == -1)
            currentAnimationStart = currentTime;

        while (animationQueue.size() > 1) {
            String firstAnimation = animationQueue.peek();
            AnimationFrames firstAnimationFrames = animationFramesMap.get(firstAnimation);
            long firstAnimationLength = firstAnimationFrames.getLength();

            long timeInAnimation = currentTime - currentAnimationStart;
            if (timeInAnimation < firstAnimationLength) {
                return new DefaultAnimationFrame(firstAnimation, timeInAnimation);
            } else {
                currentAnimationStart += firstAnimationLength;
                animationQueue.pop();
            }
        }

        String onlyAnimation = animationQueue.peek();
        AnimationFrames onlyAnimationFrames = animationFramesMap.get(onlyAnimation);
        long onlyAnimationLength = onlyAnimationFrames.getLength();
        long timeInAnimation = currentTime - currentAnimationStart;
        if (timeInAnimation >= onlyAnimationLength) {
            currentAnimationStart += (timeInAnimation / onlyAnimationLength) * onlyAnimationLength;
        }
        return new DefaultAnimationFrame(onlyAnimation, currentTime - currentAnimationStart);
    }

    public String getLastAnimation() {
        return animationQueue.peekLast();
    }

    public void appendAnimation(String animation) {
        animationQueue.push(animation);
    }

    public void replaceAnimations(String animation) {
        animationQueue.clear();
        currentAnimationStart = -1;
        animationQueue.push(animation);
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
