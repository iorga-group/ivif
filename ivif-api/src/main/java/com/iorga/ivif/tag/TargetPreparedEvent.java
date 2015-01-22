package com.iorga.ivif.tag;

import com.iorga.ivif.tag.TargetPreparedEvent.TargetPreparedEventId;

public class TargetPreparedEvent<T extends Target<I, C>, I, C extends GeneratorContext<C>> extends AbstractEvent<TargetPreparedEventId<T, I, C>> {

    private final T target;

    public static class TargetPreparedEventId<T extends Target<I, C>, I, C extends GeneratorContext<C>> {
        private final Class<? extends T> targetClass;
        private final I targetId;

        public TargetPreparedEventId(Class<? extends T> targetClass, I targetId) {
            this.targetClass = targetClass;
            this.targetId = targetId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TargetPreparedEventId that = (TargetPreparedEventId) o;

            if (!targetClass.equals(that.targetClass)) return false;
            if (targetId != null ? !targetId.equals(that.targetId) : that.targetId != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = targetClass.hashCode();
            result = 31 * result + (targetId != null ? targetId.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "TargetPreparedEventId{" +
                    "targetClass=" + targetClass +
                    ", targetId=" + targetId +
                    '}';
        }
    }

    public TargetPreparedEvent(T target) {
        super(new TargetPreparedEventId<T, I, C>((Class<? extends T>)target.getClass(), target.getId()));
        this.target = target;
    }

    public T getTarget() {
        return target;
    }
}
