package com.example.surveyapp.global.aop;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELParser {
    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    public static String parseSpEL(String expression, String[] parameterNames, Object[] args) {
        if (expression == null || expression.isEmpty()) {
            return "";
        }

        EvaluationContext context = new StandardEvaluationContext();

        // 파라미터 이름과 값을 컨텍스트에 등록
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return expressionParser.parseExpression(expression).getValue(context, String.class);
    }
}