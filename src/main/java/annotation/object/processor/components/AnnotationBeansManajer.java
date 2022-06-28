package bin.main.annotation.object.processor.components;

import annotation.object.processor.beans.ProcessableField;
import annotation.object.processor.beans.ProcessableFieldType;
import annotation.object.processor.beans.ProcessableObjectFilter;
import annotation.object.processor.beans.ProcessableObjectFilterResult;
import annotation.object.processor.components.AnnotationScanner;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

public class AnnotationBeansManajer {
  private static Logger LOGGER = LoggerFactory.getLogger(annotation.object.processor.components.AnnotationBeansManajer.class);
  
  private static annotation.object.processor.components.AnnotationBeansManajer instance = null;
  
  private final Map<String, ProcessableObjectFilterResult> enabledFilters = new HashMap<>();
  
  public static annotation.object.processor.components.AnnotationBeansManajer getInstance() {
    if (instance == null)
      instance = new annotation.object.processor.components.AnnotationBeansManajer(); 
    return instance;
  }
  
  public static annotation.object.processor.components.AnnotationBeansManajer getInstance(String basePackage, List<ProcessableObjectFilter> annotationTypes) {
    if (instance == null) {
      instance = new annotation.object.processor.components.AnnotationBeansManajer();
      instance.scan(basePackage, annotationTypes);
    } 
    return instance;
  }
  
  public Map<String, ProcessableObjectFilterResult> getEnabledFilters() {
    return this.enabledFilters;
  }
  
  @Nullable
  public Map<String, List<ProcessableField>> getClassesAnnotatedFields(String cipherAnnotationName) {
    ProcessableObjectFilterResult filterResult = this.enabledFilters.get(cipherAnnotationName);
    return (filterResult != null) ? filterResult.getBeanCipherFields() : null;
  }
  
  public Map<String, List<ProcessableField>> scanClass(Class<?> candidateClass, ProcessableObjectFilter cipherFilter) {
    ProcessableObjectFilterResult filterResult = this.enabledFilters.get(cipherFilter.getObjectAnnotation().getName());
    if (filterResult == null) {
      filterResult = new ProcessableObjectFilterResult();
      filterResult.setBeanCipherFields(new ConcurrentHashMap<>());
    } 
    int countInitialClasses = filterResult.getBeanCipherFields().size();
    Map<String, List<ProcessableField>> beanCipherFields = filterResult.getBeanCipherFields();
    ProcessableObjectFilterResult classFilterResult = collectBeansFields(beanCipherFields, cipherFilter.getFieldAnnotation(), new Class[] { candidateClass });
    filterResult.setBeanCipherFields(classFilterResult.getBeanCipherFields());
    this.enabledFilters.put(cipherFilter.getObjectAnnotation().getName(), filterResult);
    LOGGER.info("Clase escaneada: {} ; Anotacion: {} ; Conteo inicial: {} ; Conteo final: {}", new Object[] { candidateClass.getName(), 
          cipherFilter.getObjectAnnotation().getName(), Integer.valueOf(countInitialClasses), Integer.valueOf(filterResult.getBeanCipherFields().size()) });
    return filterResult.getBeanCipherFields();
  }
  
  private void scan(String basePackage, List<ProcessableObjectFilter> cipherFilters) {
    for (ProcessableObjectFilter cipherFilter : cipherFilters) {
      long inicio = System.currentTimeMillis();
      Class[] beanClasses = AnnotationScanner.findAnnotatedClassesInPackage(basePackage, cipherFilter.getObjectAnnotation());
      ProcessableObjectFilterResult cipherFilterResult = collectBeansFields(new ConcurrentHashMap<>(), cipherFilter.getFieldAnnotation(), beanClasses);
      cipherFilterResult.setBeanCipherFields(cleanEmptyClasses(cipherFilterResult.getBeanCipherFields()));
      cipherFilterResult.setCipherFilter(cipherFilter);
      cipherFilterResult.setScanPackage(basePackage);
      this.enabledFilters.put(cipherFilter.getObjectAnnotation().getName(), cipherFilterResult);
      LOGGER.info("CipherBeansScan. Time: {} ms; Package: {}; ClassAnnotation: {}; FieldAnnotation: {}; TotalClasses: {}", new Object[] { Long.valueOf(System.currentTimeMillis() - inicio), basePackage, cipherFilter.getObjectAnnotation().getSimpleName(), 
            cipherFilter.getFieldAnnotation().getSimpleName(), Integer.valueOf(cipherFilterResult.getBeanCipherFields().size()) });
    } 
  }
  
  private ProcessableObjectFilterResult collectBeansFields(Map<String, List<ProcessableField>> beanClassesFields, Class<? extends Annotation> fieldAnnotation, Class... beanClasses) {
    ProcessableObjectFilterResult cipherFilterResult = new ProcessableObjectFilterResult();
    List<Class<?>> listBeanClasses = new ArrayList<>(Arrays.asList(beanClasses));
    ListIterator<Class<?>> iterator = listBeanClasses.listIterator();
    boolean hasPrevious = false;
    while ((hasPrevious = iterator.hasPrevious()) || iterator.hasNext()) {
      Class<?> beanClass = hasPrevious ? iterator.previous() : iterator.next();
      iterator.remove();
      if (beanClassesFields.containsKey(beanClass.getName())) {
        LOGGER.info("La clase '{}' ya fue escaneada, se omite su scaneo para la anotacion '{}'", 
            beanClass.getName(), fieldAnnotation.getName());
        continue;
      } 
      List<ProcessableField> classFields = new ArrayList<>();
      ResolveClassFieldsResult classFieldsResult = resolveClassFields(beanClass, fieldAnnotation);
      classFieldsResult.getFirstLevelFields().forEach(cipherField -> {
          
          });
      for (ProcessableField fieldWithReference : classFieldsResult.getFieldsWithReferencedClass())
        iterator.add(fieldWithReference.getReferencedType()); 
      beanClassesFields.put(beanClass.getName(), classFields);
    } 
    cipherFilterResult.setBeanCipherFields(beanClassesFields);
    return cipherFilterResult;
  }
  
  private Map<String, List<ProcessableField>> cleanEmptyClasses(Map<String, List<ProcessableField>> beanClassesFields) {
    List<String> emptyClasses = (List<String>)beanClassesFields.entrySet().stream().filter(entry -> ((List)entry.getValue()).isEmpty())
      .map(entry -> (String)entry.getKey()).collect(Collectors.toList());
    while (!emptyClasses.isEmpty()) {
      for (String emptyClass : emptyClasses) {
        beanClassesFields.remove(emptyClass);
        for (Map.Entry<String, List<ProcessableField>> entry : beanClassesFields.entrySet()) {
          if (emptyClasses.contains(entry.getKey()))
            continue; 
          List<ProcessableField> withoutEmptyReferences = (List<ProcessableField>)((List)entry.getValue()).stream()
            .filter(cipherField -> !(cipherField.getReferencedType() != null && paramString.equals(cipherField.getReferencedType().getName())))
            
            .collect(Collectors.toList());
          beanClassesFields.replace(entry.getKey(), withoutEmptyReferences);
        } 
      } 
      emptyClasses = (List<String>)beanClassesFields.entrySet().stream().filter(entry -> ((List)entry.getValue()).isEmpty())
        .map(entry -> (String)entry.getKey()).collect(Collectors.toList());
    } 
    return beanClassesFields;
  }
  
  private ResolveClassFieldsResult resolveClassFields(Class<?> classType, Class<? extends Annotation> fieldAnnotation) {
    List<ProcessableField> cipherFields = new ArrayList<>();
    List<ProcessableField> fieldsWithReferencedClass = new ArrayList<>();
    for (Field field : getClassDeclaredFields(classType)) {
      Optional<ProcessableField> optCipherField = getCipherFieldIfValid(field);
      if (!optCipherField.isPresent())
        continue; 
      ProcessableField cipherField = optCipherField.get();
      if (cipherField.getType().isComplex()) {
        ProcessableField nestedField = new ProcessableField(field, cipherField.getType(), cipherField.getReferencedType());
        cipherFields.add(nestedField);
        fieldsWithReferencedClass.add(cipherField);
        continue;
      } 
      if (field.getAnnotation(fieldAnnotation) != null)
        cipherFields.add(cipherField); 
    } 
    return new ResolveClassFieldsResult(this, cipherFields, fieldsWithReferencedClass);
  }
  
  private Collection<Field> getClassDeclaredFields(Class<?> classType) {
    Collection<Field> classAttributes = new ArrayList<>(Arrays.asList(classType.getDeclaredFields()));
    Class<? extends Object> objectSuperclass = (Class)classType.getSuperclass();
    while (true) {
      Field[] fieldsSup = objectSuperclass.getDeclaredFields();
      classAttributes.addAll(Arrays.asList(fieldsSup));
      objectSuperclass = (Class)objectSuperclass.getClass().getSuperclass();
      if (classType.getSuperclass() != null)
        return classAttributes; 
    } 
  }
  
  private Optional<ProcessableField> getCipherFieldIfValid(Field field) {
    Optional<ProcessableFieldType> optCipherFieldType = getCipherFieldType(field);
    if (!optCipherFieldType.isPresent())
      return Optional.empty(); 
    ProcessableFieldType cipherFieldType = optCipherFieldType.get();
    Class<?> referencedType = null;
    switch (cipherFieldType) {
      case COMPLEX_ARRAY:
        referencedType = field.getType().getComponentType();
        break;
      case COMPLEX_LIST:
        referencedType = getListGenericType(field);
        break;
      case COMPLEX_BEAN:
        referencedType = field.getType();
        break;
    } 
    return Optional.of(new ProcessableField(field, cipherFieldType, referencedType));
  }
  
  private Optional<ProcessableFieldType> getCipherFieldType(Field field) {
    Class<?> fieldClass = field.getType();
    ProcessableFieldType cipherType = null;
    if (fieldClass.isAssignableFrom(String.class)) {
      cipherType = ProcessableFieldType.STRING;
    } else if (fieldClass.isAssignableFrom(List.class)) {
      cipherType = getListCipherType(field);
    } else if (fieldClass.isArray()) {
      cipherType = getArrayCipherType(fieldClass);
    } else if (!ClassUtils.isPrimitiveOrWrapper(fieldClass) && !isDate(fieldClass)) {
      cipherType = ProcessableFieldType.COMPLEX_BEAN;
    } 
    return Optional.ofNullable(cipherType);
  }
  
  @Nullable
  private ProcessableFieldType getListCipherType(Field field) {
    ProcessableFieldType cipherType = null;
    Class<?> genericType = getListGenericType(field);
    if (genericType == null)
      return cipherType; 
    if (String.class.isAssignableFrom(genericType)) {
      cipherType = ProcessableFieldType.LIST_OF_STRING;
    } else if (!ClassUtils.isPrimitiveOrWrapper(genericType)) {
      cipherType = ProcessableFieldType.COMPLEX_LIST;
    } 
    return cipherType;
  }
  
  @Nullable
  private Class<?> getListGenericType(Field field) {
    ParameterizedType listType = (ParameterizedType)field.getGenericType();
    Type type = listType.getActualTypeArguments()[0];
    Class<?> classType = null;
    try {
      classType = Class.forName(type.getTypeName());
    } catch (ClassNotFoundException e) {
      LOGGER.info("No se pudo obtener la clase especificada: {}", type.getTypeName());
    } 
    return classType;
  }
  
  @Nullable
  private ProcessableFieldType getArrayCipherType(Class<?> fieldClass) {
    ProcessableFieldType cipherType = null;
    if (String.class.isAssignableFrom(fieldClass.getComponentType())) {
      cipherType = ProcessableFieldType.ARRAY_OF_STRING;
    } else if (!ClassUtils.isPrimitiveOrWrapper(fieldClass.getComponentType())) {
      cipherType = ProcessableFieldType.COMPLEX_ARRAY;
    } 
    return cipherType;
  }
  
  private boolean isDate(Class<?> classType) {
    return !(!classType.isAssignableFrom(Date.class) && !classType.isAssignableFrom(LocalDate.class) && 
      !classType.isAssignableFrom(LocalDateTime.class));
  }
}
