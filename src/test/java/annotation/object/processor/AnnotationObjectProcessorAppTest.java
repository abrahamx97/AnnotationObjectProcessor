package bin.test.annotation.object.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class})
@ComponentScan({"annotation.object.processor"})
public class AnnotationObjectProcessorAppTest {
  private static final Logger log = LoggerFactory.getLogger(annotation.object.processor.AnnotationObjectProcessorAppTest.class);
  
  public static void main(String[] args) {
    try {
      SpringApplication.run(annotation.object.processor.AnnotationObjectProcessorAppTest.class, args);
    } catch (Exception e) {
      log.error("Error: {}", e.getMessage());
    } 
  }
}