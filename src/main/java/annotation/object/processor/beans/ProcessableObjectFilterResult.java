package annotation.object.processor.beans;

import java.util.List;
import java.util.Map;

public class ProcessableObjectFilterResult {
  private String scanPackage;
  
  private ProcessableObjectFilter cipherFilter;
  
  private Map<String, List<ProcessableField>> beanCipherFields;
  
  public String getScanPackage() {
    return this.scanPackage;
  }
  
  public void setScanPackage(String scanPackage) {
    this.scanPackage = scanPackage;
  }
  
  public ProcessableObjectFilter getCipherFilter() {
    return this.cipherFilter;
  }
  
  public void setCipherFilter(ProcessableObjectFilter cipherFilter) {
    this.cipherFilter = cipherFilter;
  }
  
  public Map<String, List<ProcessableField>> getBeanCipherFields() {
    return this.beanCipherFields;
  }
  
  public void setBeanCipherFields(Map<String, List<ProcessableField>> beanCipherFields) {
    this.beanCipherFields = beanCipherFields;
  }
}
