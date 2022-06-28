package bin.main.annotation.object.processor.components;

import annotation.object.processor.beans.ProcessableField;
import annotation.object.processor.components.AnnotationBeansManajer;
import java.util.List;

class ResolveClassFieldsResult {
  private final List<ProcessableField> firstLevelFields;
  
  private final List<ProcessableField> fieldsWithReferencedClass;
  
  public ResolveClassFieldsResult(List<ProcessableField> firstLevelFields, List<ProcessableField> fieldsWithReferencedClass) {
    this.firstLevelFields = firstLevelFields;
    this.fieldsWithReferencedClass = fieldsWithReferencedClass;
  }
  
  public List<ProcessableField> getFirstLevelFields() {
    return this.firstLevelFields;
  }
  
  public List<ProcessableField> getFieldsWithReferencedClass() {
    return this.fieldsWithReferencedClass;
  }
}
