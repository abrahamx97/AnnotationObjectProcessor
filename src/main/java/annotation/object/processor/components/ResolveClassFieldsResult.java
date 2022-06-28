package annotation.object.processor.components;

import java.util.List;

import annotation.object.processor.beans.ProcessableField;

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
