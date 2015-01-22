package com.iorga.ivif.tag;

import com.iorga.ivif.tag.TargetPart.TargetPartId;

public interface TargetPart<I, T extends Target<TI, C>, TI, C extends GeneratorContext<C>> extends Identifiable<TargetPartId<I, T, TI, C>> {
    public T getTarget();
    public I getPartId();

    public static class TargetPartId<I, T extends Target<TI, C>, TI, C extends GeneratorContext<C>> {
        private I partId;
        private Class<? extends T> targetClass;
        private TI targetId;

        public TargetPartId(I partId, Class<? extends T> targetClass, TI targetId) {
            this.targetClass = targetClass;
            this.targetId = targetId;
            this.partId = partId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TargetPartId that = (TargetPartId) o;

            if (partId != null ? !partId.equals(that.partId) : that.partId != null) return false;
            if (!targetClass.equals(that.targetClass)) return false;
            if (targetId != null ? !targetId.equals(that.targetId) : that.targetId != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = partId != null ? partId.hashCode() : 0;
            result = 31 * result + targetClass.hashCode();
            result = 31 * result + (targetId != null ? targetId.hashCode() : 0);
            return result;
        }
    }

}
