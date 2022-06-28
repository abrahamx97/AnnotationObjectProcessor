package annotation.object.processor.components;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import annotation.object.processor.beans.ProcessableField;
import annotation.object.processor.beans.ProcessableFieldType;
import annotation.object.processor.beans.ProcessableObjectFilter;
import annotation.object.processor.interfaces.AnnotationObjectProcessor;
import annotation.object.processor.interfaces.ProcessableFieldFunction;

public abstract class AbstractAnnotationObjectProcessor implements AnnotationObjectProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAnnotationObjectProcessor.class);
  
  private AnnotationBeansManajer annotationBeansManager;
  
  private Map<String, List<ProcessableField>> classAnnotatedFields;
  
  private ProcessableObjectFilter processableFilter;
  
  protected AbstractAnnotationObjectProcessor(ProcessableObjectFilter processableFilter, AnnotationBeansManajer annotationBeansManager) {
    this.processableFilter = processableFilter;
    this.annotationBeansManager = annotationBeansManager;
    init(processableFilter.getObjectAnnotation());
  }
  
  public Object processObject(Object initialObject, ProcessableFieldFunction function) {
    if (initialObject == null)
      return initialObject; 
    if (!existsBeanClass(initialObject.getClass().getName()))
      this.classAnnotatedFields = this.annotationBeansManager.scanClass(initialObject.getClass(), this.processableFilter); 
    List<ProcessableField> processableFields = this.classAnnotatedFields.get(initialObject.getClass().getName());
    for (ProcessableField cipherField : processableFields) {
      try {
        Field field = cipherField.getField();
        field.setAccessible(true);
        Object fieldValue = field.get(initialObject);
        Annotation annotation = field.getAnnotation(this.processableFilter.getFieldAnnotation());
        if (fieldValue == null)
          continue; 
        Object processedValue = null;
        if (cipherField.getType().isComplex()) {
          processedValue = cipherComplexField(cipherField.getType(), fieldValue, function);
        } else {
          processedValue = cipherSimpleField(cipherField.getType(), fieldValue, annotation, function);
        } 
        field.set(initialObject, processedValue);
      } catch (IllegalArgumentException|IllegalAccessException e) {
        e.printStackTrace();
      } 
    } 
    return initialObject;
  }
  
  private void init(Class<? extends Annotation> enableCipherClass) {
    String enableObjectAnnotationName = enableCipherClass.getName();
    this.classAnnotatedFields = this.annotationBeansManager.getClassesAnnotatedFields(enableObjectAnnotationName);
    if (this.classAnnotatedFields == null) {
      this.classAnnotatedFields = new HashMap<>();
      LOGGER.info("No se encontro escaneo inicial para la anotacion: {}", enableObjectAnnotationName);
    } 
  }
  
  private boolean existsBeanClass(String beanClassName) {
    boolean exists = (this.classAnnotatedFields != null && this.classAnnotatedFields.containsKey(beanClassName));
    if (!exists)
      LOGGER.info("No se encontro datos de cifrado para la clase: {}", beanClassName); 
    return exists;
  }
  
  @SuppressWarnings({ "incomplete-switch", "unchecked" })
private Object cipherSimpleField(ProcessableFieldType cipherFieldType, Object fieldValue, Annotation annotation, ProcessableFieldFunction function) {
    Object processedValue = null;
    switch (cipherFieldType) {
      case STRING:
        processedValue = function.apply(fieldValue, (Annotation)annotation);
        break;
      case LIST_OF_STRING:
        processedValue = processSimpleList((List<String>)fieldValue, annotation, function);
        break;
      case ARRAY_OF_STRING:
    	  processedValue = processSimpleArray((String[])fieldValue, annotation, function);
        break;
    } 
    return processedValue;
  }
  
  @SuppressWarnings({ "unchecked", "incomplete-switch" })
private Object cipherComplexField(ProcessableFieldType cipherFieldType, Object fieldValue, ProcessableFieldFunction function) {
    Object processedValue = null;
    switch (cipherFieldType) {
      case COMPLEX_ARRAY:
        processedValue = processComplexArray((Object[])fieldValue, function);
        break;
      case COMPLEX_LIST:
        processedValue = processComplexList((List<Object>)fieldValue, function);
        break;
      case COMPLEX_BEAN:
        processedValue = processObject(fieldValue, function);
        break;
    } 
    return processedValue;
  }
  
  private List<String> processSimpleList(List<String> list, Annotation annotation, ProcessableFieldFunction function) {
    List<String> newValues = new ArrayList<>();
    for (String item : list) {
      String newValue = (String)function.apply(item, (Annotation)annotation);
      newValues.add(newValue);
    } 
    return newValues;
  }
  
  private List<Object> processComplexList(List<Object> list, ProcessableFieldFunction function) {
    List<Object> newValues = new ArrayList<>();
    for (Object item : list) {
      Object newValue = (item == null) ? null : processObject(item, function);
      newValues.add(newValue);
    } 
    return newValues;
  }
  
  private String[] processSimpleArray(String[] array, Annotation annotation, ProcessableFieldFunction function) {
    for (int i = 0; i < array.length; i++) {
      String item = array[i];
      String newValue = (String)function.apply(item, (Annotation)annotation);
      array[i] = newValue;
    } 
    return array;
  }
  
  private Object[] processComplexArray(Object[] array, ProcessableFieldFunction function) {
    for (int i = 0; i < array.length; i++) {
      Object item = array[i];
      Object newValue = (item == null) ? null : processObject(item, function);
      array[i] = newValue;
    } 
    return array;
  }
}
