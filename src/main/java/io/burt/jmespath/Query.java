package io.burt.jmespath;

import io.burt.jmespath.ast.JmesPathNode;

public class Query {
  private final JmesPathNode expression;

  public Query(JmesPathNode expression) {
    this.expression = expression;
  }

  protected JmesPathNode expression() {
    return expression;
  }

  @Override
  public String toString() {
    return String.format("Query(%s)", expression);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Query)) {
      return false;
    }
    Query other = (Query) o;
    return this.expression().equals(other.expression());
  }

  @Override
  public int hashCode() {
    int h = 1;
    h = h * 31 + expression.hashCode();
    return h;
  }
}