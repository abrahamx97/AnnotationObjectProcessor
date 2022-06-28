package annotation.object.processor.components.impl;

import annotation.object.processor.beans.ProcessableObjectFilter;
import annotation.object.processor.components.AbstractAnnotationObjectProcessor;
import annotation.object.processor.components.AnnotationBeansManajer;
import annotation.object.processor.components.annotations.Base64DTO;
import annotation.object.processor.components.annotations.Base64Field;
import java.lang.annotation.Annotation;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Base64AnnotationProcessor extends AbstractAnnotationObjectProcessor {
  public Base64AnnotationProcessor(@Autowired AnnotationBeansManajer annotationBeansManager) {
    super(new ProcessableObjectFilter(Base64DTO.class, Base64Field.class), annotationBeansManager);
  }
  
  public Object base64Fields(Object object) {
    processObject(object, this::toBase64);
    return object;
  }
  
  private String toBase64(Object data, Annotation annotation) {
    return Base64.getEncoder().encodeToString(data.toString().getBytes());
  }
}