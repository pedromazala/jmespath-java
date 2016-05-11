package io.burt.jmespath;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.burt.jmespath.Query;

public class Compliance {
  public static class TestGroup implements Iterable<TestCase> {
    private final JsonNode input;
    private final String comment;
    private final List<TestCase> testCases;

    @JsonCreator
    public TestGroup(
      @JsonProperty("given") JsonNode input,
      @JsonProperty("comment") String comment,
      @JsonProperty("cases") List<TestCase> testCases
    ) {
      this.input = input;
      this.comment = comment;
      this.testCases = testCases;
    }

    public Iterator<TestCase> iterator() { return testCases.iterator(); }
  }

  public static class TestCase {
    private final String expression;
    private final String comment;
    private final String description;
    private final String error;
    private final String bench;
    private final JsonNode result;

    @JsonCreator
    public TestCase(
      @JsonProperty("expression") String expression,
      @JsonProperty("comment") String comment,
      @JsonProperty("description") String description,
      @JsonProperty("error") String error,
      @JsonProperty("bench") String bench,
      @JsonProperty("result") JsonNode result
    ) {
      this.expression = expression;
      this.comment = comment;
      this.description = description;
      this.error = error;
      this.bench = bench;
      this.result = result;
    }

    public String expression() { return expression; }
    public String comment() { return comment; }
    public String error() { return error; }
  }

  public static void main(String[] args) throws IOException {
    runTests(loadTests(args));
  }

  private static List<TestGroup> loadTests(String[] paths) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<TestGroup> testGroups = new LinkedList<>();
    for (String path : paths) {
      List<TestGroup> groups = objectMapper.readValue(new File(path), new TypeReference<List<TestGroup>>(){});
      testGroups.addAll(groups);
    }
    return testGroups;
  }

  private static void runTests(List<TestGroup> testGroups) {
    for (TestGroup testGroup : testGroups) {
      for (TestCase testCase : testGroup) {
        String expectedError = testCase.error();
        try {
          if (expectedError == null || expectedError.equals("syntax")) {
            AstGenerator.fromString(testCase.expression());
            if (expectedError != null && expectedError.equals("syntax")) {
              System.out.println(String.format("The expression \"%s\" did not fail with a syntax error! (comment: \"%s\")", testCase.expression(), testCase.comment()));
            }
          } else {
            System.out.println(String.format("Unsure how evaluate \"%s\" (comment: \"%s\", error: \"%s\")", testCase.expression(), testCase.comment(), expectedError));
          }
        } catch (ParseException pe) {
          if (expectedError == null || !expectedError.equals("syntax")) {
            System.out.println(String.format("The expression \"%s\" failed with parse exception: \"%s\" (comment: \"%s\")", testCase.expression(), pe.getMessage(), testCase.comment()));
          }
        } catch (Exception e) {
          System.out.println(String.format("The expression \"%s\" failed with another exception: %s(\"%s\") (comment: \"%s\")", testCase.expression(), e.getClass().getName(), e.getMessage(), testCase.comment()));
        }
      }
    }
  }
}
