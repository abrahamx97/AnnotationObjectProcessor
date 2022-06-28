package bin.main.annotation.object.processor.beans;

import java.lang.annotation.Annotation;

public class ProcessableObjectFilter {
  private final Class<? extends Annotation> objectAnnotation;
  
  private final Class<? extends Annotation> fieldAnnotation;
  
  public ProcessableObjectFilter(Class<? extends Annotation> beanAnnotation, Class<? extends Annotation> fieldAnnotation) {
    this.objectAnnotation = beanAnnotation;
    this.fieldAnnotation = fieldAnnotation;
  }
  
  public Class<? extends Annotation> getObjectAnnotation() {
    return this.objectAnnotation;
  }
  
  public Class<? extends Annotation> getFieldAnnotation() {
    return this.fieldAnnotation;
  }
}
