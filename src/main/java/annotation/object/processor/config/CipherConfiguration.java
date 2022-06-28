package annotation.object.processor.config;

import annotation.object.processor.beans.ProcessableObjectFilter;
import annotation.object.processor.components.AnnotationBeansManajer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CipherConfiguration {
  @Value("${annotation.object.processor.base.package:#{null}}")
  private String basePackage;
  
  @Bean
  public AnnotationBeansManajer getCipherBeansManajer(@Autowired(required = false) List<ProcessableObjectFilter> filters) {
    if (this.basePackage == null || this.basePackage.isEmpty() || filters == null)
      return AnnotationBeansManajer.getInstance(); 
    return AnnotationBeansManajer.getInstance(this.basePackage, filters);
  }
}
