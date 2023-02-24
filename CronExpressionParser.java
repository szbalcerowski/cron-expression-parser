package com.balcerowski.cron.expression.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class CronExpressionParser {

  private static final String REGEXP = "((\\*|\\d+)(,|\\/|-|))+";


  public static void main(String[] args) {
    var arg = args[0];
    List<String> matches = new ArrayList<String>();
    Matcher m = Pattern.compile(REGEXP).matcher(arg);

    var command = arg;
    while (m.find()) {
      matches.add(m.group());
      command = command.replace(m.group(), "");
    }

    System.out.println("minute " + getParsed(matches.get(0), 60, 0));
    System.out.println("hour " + getParsed(matches.get(1), 24, 0));
    System.out.println("day of a month " + getParsed(matches.get(2), 32, 1));
    System.out.println("month " + getParsed(matches.get(3), 13, 1));
    System.out.println("day of a week " + getParsed(matches.get(4), 8, 1));
    System.out.println("command " + command.trim());
  }

  private static ArrayList<Integer> getParsed(String fullExpression, int max, int min) {
    ArrayList<Integer> result = new ArrayList<>();
    var subExpressions = fullExpression.split(",");

    for (var subExpression : subExpressions) {
      if (subExpression.contains("/")) {
        result.addAll(slashExpression(subExpression, min, max));
      } else if (subExpression.contains("-")) {
        result.addAll(dashExpression(subExpression, min, max));
      } else {
        result.addAll(singeExpression(subExpression, min, max));
      }
    }

    Collections.sort(result);
    return result;
  }

  static List<Integer> slashExpression(String subExpression, int min, int max) {
    ArrayList<Integer> result = new ArrayList<>();
    var slashExpression = subExpression.split("/");
    var slashExpression1 = slashExpression[0];
    var slashExpression2 = slashExpression[1];

    if (slashExpression1.equalsIgnoreCase("*")) {
      slashExpression1 = "0";
    }
    for (int i = Integer.parseInt(slashExpression1); i < max; i += Integer.parseInt(slashExpression2)) {
      result.add(i);
    }
    return result;
  }

  static List<Integer> dashExpression(String subExpression, int min, int max) {
    var dashExpression = subExpression.split("-");
    var dashExpression1 = Integer.parseInt(dashExpression[0]);
    var dashExpression2 = Integer.parseInt(dashExpression[1]);

    return IntStream.rangeClosed(dashExpression1, dashExpression2).boxed().toList();
  }

  static List<Integer> singeExpression(String subExpression, int min, int max) {
    if (subExpression.equalsIgnoreCase("*")) {
      return IntStream.rangeClosed(min, max - 1).boxed().toList();
    } else {
      return List.of(Integer.parseInt(subExpression));
    }
  }

  boolean nonValidMinMax(int value, int min, int max) {
    return value >= max || value < min;
  }

}
