// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a for-statement.
 */
class JForStatement extends JStatement {
    // Initialization.
    private ArrayList<JStatement> init;

    // Test expression
    private JExpression condition;

    // Update.
    private ArrayList<JStatement> update;

    // The body.
    private JStatement body;

    public String breakLabel;
    public String continueLabel;

    /**
     * Constructs an AST node for a for-statement.
     *
     * @param line      line in which the for-statement occurs in the source file.
     * @param init      the initialization.
     * @param condition the test expression.
     * @param update    the update.
     * @param body      the body.
     */
    public JForStatement(int line, ArrayList<JStatement> init, JExpression condition,
                         ArrayList<JStatement> update, JStatement body) {
        super(line);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    /**
     * {@inheritDoc}
     */
    public JForStatement analyze(Context context) {
        // Project 5 Problem 6
        LocalContext localContext = new LocalContext(context);
        ArrayList<JStatement> analyzedInits = new ArrayList<>();
        if (init != null) {
            for (JStatement i : init) {
                i = (JStatement) i.analyze(localContext);
                analyzedInits.add(i);
            }
        }
        this.init = analyzedInits;
        if (condition != null) condition = condition.analyze(localContext);
        ArrayList<JStatement> analyzedUpdates = new ArrayList<>();
        if (update != null) {
            for (JStatement u : update) {
                u = (JStatement) u.analyze(localContext);
                analyzedUpdates.add(u);
            }
        }
        this.update = analyzedUpdates;
        body = (JStatement) body.analyze(localContext);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Project 5 Problem 6
        String conditionLabel = output.createLabel();
        this.breakLabel = output.createLabel();
        this.continueLabel = output.createLabel();
        if(init != null) {
            for (JStatement i : init) {
                i.codegen(output);
            }
        }
        output.addLabel(conditionLabel);
        if(condition != null) condition.codegen(output, breakLabel, false);
        if(body != null) body.codegen(output);
        output.addLabel(continueLabel);
        if(update != null) {
            for (JStatement u : update) {
                u.codegen(output);
            }
        }
        output.addBranchInstruction(GOTO, conditionLabel);
        output.addLabel(breakLabel);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JForStatement:" + line, e);
        if (init != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Init", e1);
            for (JStatement stmt : init) {
                stmt.toJSON(e1);
            }
        }
        if (condition != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Condition", e1);
            condition.toJSON(e1);
        }
        if (update != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Update", e1);
            for (JStatement stmt : update) {
                stmt.toJSON(e1);
            }
        }
        if (body != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Body", e1);
            body.toJSON(e1);
        }
    }
}
