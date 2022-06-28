package annotation.object.processor.beans;

import java.lang.reflect.Field;

public class ProcessableField {
  private final Field field;
  
  private final ProcessableFieldType type;
  
  private final Class<?> referencedType;
  
  public ProcessableField(Field field, ProcessableFieldType type) {
    this.field = field;
    this.type = type;
    this.referencedType = null;
  }
  
  public ProcessableField(Field field, ProcessableFieldType type, Class<?> referencedType) {
    this.field = field;
    this.type = type;
    this.referencedType = referencedType;
  }
  
  public Field getField() {
    return this.field;
  }
  
  public ProcessableFieldType getType() {
    return this.type;
  }
  
  public Class<?> getReferencedType() {
    return this.referencedType;
  }
}
