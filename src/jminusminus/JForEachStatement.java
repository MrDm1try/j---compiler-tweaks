// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a for-each-statement.
 */

class JForEachStatement extends JStatement {

    /** Loop variable. */
    private JStatement variable;

    /** Statement with enumerated element (that goes for-each). */
    private JExpression enumeration;

    /** The body. */
    private JStatement body;

    /**
     * Construct an AST node for a for-each-statement given its line number, the
     * test expression, and the body.
     * 
     * @param line
     *            line in which the for-each-statement occurs in the source file.
     * @param condition
     *            test expression.
     * @param body
     *            the body.
     */

    public JForEachStatement(int line, JStatement variable, JExpression enumeration, JStatement body) {
        super(line);
        this.variable = variable;
        this.enumeration = enumeration;
        this.body = body;
    }

    /**
     * Analysis involves analyzing the test, checking its type and analyzing the
     * body statement.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JForEachStatement analyze(Context context) {
        variable = variable.analyze(context);
        enumeration = enumeration.analyze(context);

        body = body.analyze(context);
        return this;
    }

    /**
     * Generate code for the for-each-loop.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {

        // starting with the loop-variable
        variable.codegen(output);

        // Need two labels
        String test = output.createLabel();
        String out = output.createLabel();

        output.addLabel(test);

        // Codegen body
        body.codegen(output);
        enumeration.codegen(output);

        // Unconditional jump back up to test
        output.addBranchInstruction(GOTO, test);

        // The label below and outside the loop
        output.addLabel(out);
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JForEachStatement line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<InitVariable>\n");
        p.indentRight();
        condition.writeToStdOut(p);
        p.indentLeft();
        p.printf("</InitVariable>\n");
        p.printf("<Enumeration>\n");
        p.indentRight();
        condition.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Enumeration>\n");
        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.indentLeft();
        p.printf("</JForEachStatement>\n");
    }

}
