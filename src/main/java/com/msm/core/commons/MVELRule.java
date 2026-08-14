package com.msm.core.commons;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.BasicRule;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MVELRule extends BasicRule {

    private Serializable compiledCondition;
    private final List<Serializable> compiledActions = new ArrayList<>();
    private final ParserContext parserContext;

    public MVELRule() {
        this(new ParserContext());
    }

    public MVELRule(ParserContext parserContext) {
        super(Rule.DEFAULT_NAME, Rule.DEFAULT_DESCRIPTION, Rule.DEFAULT_PRIORITY);
        this.parserContext = parserContext;
    }

    public MVELRule name(String name) {
        this.name = name;
        return this;
    }

    public MVELRule description(String description) {
        this.description = description;
        return this;
    }

    public MVELRule priority(int priority) {
        this.priority = priority;
        return this;
    }

    public MVELRule when(String condition) {
        // Biên dịch tĩnh biểu thức MVEL để tăng tốc độ xử lý và tối ưu hóa bộ nhớ
        this.compiledCondition = MVEL.compileExpression(condition, this.parserContext);
        return this;
    }

    public MVELRule then(String action) {
        this.compiledActions.add(MVEL.compileExpression(action, this.parserContext));
        return this;
    }

    @Override
    public boolean evaluate(Facts facts) {
        if (compiledCondition == null) return false;
        Object result = MVEL.executeExpression(compiledCondition, facts.asMap());
        return result instanceof Boolean && (Boolean) result;
    }

    @Override
    public void execute(Facts facts) throws Exception {
        for (Serializable compiledAction : compiledActions) {
            MVEL.executeExpression(compiledAction, facts.asMap());
        }
    }
}
