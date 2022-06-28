package bin.main.annotation.object.processor.interfaces;

import java.lang.annotation.Annotation;

public interface ProcessableFieldFunction {
  Object apply(Object paramObject, Annotation paramAnnotation);
}
