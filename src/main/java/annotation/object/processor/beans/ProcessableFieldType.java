package annotation.object.processor.beans;

public enum ProcessableFieldType {
  STRING, COMPLEX_BEAN, LIST_OF_STRING, COMPLEX_LIST, ARRAY_OF_STRING, COMPLEX_ARRAY, ROOT_CLASS;
  
  public boolean isComplex() {
    return !(this != COMPLEX_BEAN && this != COMPLEX_LIST && 
      this != COMPLEX_ARRAY);
  }
}
