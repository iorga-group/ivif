package com.iorga.ivif.tag;

import com.iorga.ivif.tag.TargetPart.TargetPartId;
import com.iorga.ivif.tag.TargetPartPreparedEvent.TargetPartPreparedEventId;

public class TargetPartPreparedEvent<P extends TargetPart<I, T, TI, C>, I, T extends Target<TI, C>, TI, C extends GeneratorContext<C>> extends AbstractEvent<TargetPartPreparedEventId<P, I, T, TI, C>> {

    private final P targetPart;

    public static class TargetPartPreparedEventId<P extends TargetPart<I, T, TI, C>, I, T extends Target<TI, C>, TI, C extends GeneratorContext<C>> {
        private final Class<? extends P> targetPartClass;
        private final I targetPartId;
        private final Class<? extends T> targetClass;
        private final TI targetId;

        public TargetPartPreparedEventId(Class<? extends P> targetPartClass, I targetPartId, Class<? extends T> targetClass, TI targetId) {
            this.targetPartClass = targetPartClass;
            this.targetPartId = targetPartId;
            this.targetClass = targetClass;
            this.targetId = targetId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TargetPartPreparedEventId that = (TargetPartPreparedEventId) o;

            if (!targetClass.equals(that.targetClass)) return false;
            if (targetId != null ? !targetId.equals(that.targetId) : that.targetId != null) return false;
            if (!targetPartClass.equals(that.targetPartClass)) return false;
            if (targetPartId != null ? !targetPartId.equals(that.targetPartId) : that.targetPartId != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = targetPartClass.hashCode();
            result = 31 * result + targetPartId.hashCode();
            result = 31 * result + targetClass.hashCode();
            result = 31 * result + targetId.hashCode();
            return result;
        }

		@Override
		public String toString() {
			return "TargetPartPreparedEventId [targetClass=" + targetClass
					+ ", targetId=" + targetId + ", targetPartClass="
					+ targetPartClass + ", targetPartId=" + targetPartId + "]";
		}
        
        
    }

    public TargetPartPreparedEvent(P targetPart) {
        super(new TargetPartPreparedEventId<P, I, T, TI, C>((Class<? extends P>)targetPart.getClass(), targetPart.getPartId(), (Class<? extends T>)targetPart.getTarget().getClass(), targetPart.getTarget().getId()));
        this.targetPart = targetPart;
    }

    public P getTargetPart() {
        return targetPart;
    }
}
