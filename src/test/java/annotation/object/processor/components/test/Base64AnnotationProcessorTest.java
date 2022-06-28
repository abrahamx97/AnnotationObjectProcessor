package bin.test.annotation.object.processor.components.test;

import annotation.object.processor.AnnotationObjectProcessorAppTest;
import annotation.object.processor.components.impl.Base64AnnotationProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {AnnotationObjectProcessorAppTest.class})
public class Base64AnnotationProcessorTest {
  @Autowired
  private Base64AnnotationProcessor base64Processor;
  
  @Test
  public void base64EncodeTest() {
    Assertions.assertNotNull("");
  }
}