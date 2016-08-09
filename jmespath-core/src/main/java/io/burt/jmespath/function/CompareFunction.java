package io.burt.jmespath.function;

import java.util.List;
import java.util.Iterator;

import io.burt.jmespath.Adapter;
import io.burt.jmespath.JmesPathType;

public abstract class CompareFunction extends ArrayMathFunction {
  public CompareFunction() {
    super(ArgumentConstraints.typeOf(JmesPathType.NUMBER, JmesPathType.STRING));
  }

  protected abstract boolean sortsBefore(int compareResult);

  @Override
  protected <T> T performMathOperation(Adapter<T> runtime, List<T> values) {
    if (values.isEmpty()) {
      return runtime.createNull();
    } else {
      Iterator<T> vs = values.iterator();
      T result = vs.next();
      while (vs.hasNext()) {
        T candidate = vs.next();
        if (sortsBefore(runtime.compare(candidate, result))) {
          result = candidate;
        }
      }
      return result;
    }
  }
}
