package annotation.object.processor.components;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.context.support.StandardServletEnvironment;

public class AnnotationScanner {
  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationScanner.class);
  
  public static Class<?>[] findAnnotatedClassesInPackage(String basePackage, Class<? extends Annotation> annotationType) {
    List<Class<?>> result = new LinkedList<>();
    ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
        false, (Environment)new StandardServletEnvironment());
    provider.addIncludeFilter((TypeFilter)new AnnotationTypeFilter(annotationType));
    Iterator<BeanDefinition> iterator = provider.findCandidateComponents(basePackage).iterator();
    while (iterator.hasNext()) {
      BeanDefinition beanDefinition = iterator.next();
      try {
        result.add(Class.forName(beanDefinition.getBeanClassName()));
      } catch (ClassNotFoundException e) {
        LOGGER.info("Could not resolve class object for bean definition");
      } 
    } 
    return result.toArray(new Class<?>[result.size()]);
  }
}
