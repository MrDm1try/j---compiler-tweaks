// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a for-each-statement.
 */

class JForEachStatement extends JStatement {

    /** Loop variable. */
    private JFormalParameter variable;

    /** Statement with enumerated element (that goes for-each). */
    private JExpression enumeration;

    /** The body. */
    private JStatement body;

    /** Context for this loop. */
    private LocalContext context;

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

    public JForEachStatement(int line, JFormalParameter variable, JExpression enumeration, JStatement body) {
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
        // we define a new context for the whole loop
        this.context = new LocalContext(context);

        // registering the variable in the current context
		LocalVariableDefn defn = new LocalVariableDefn(variable.type().resolve(this.context), 
				this.context.nextOffset());
		defn.initialize();
		this.context.addEntry(variable.line(), variable.name(), defn);

        variable = (JFormalParameter) variable.analyze(this.context);
        enumeration = enumeration.analyze(this.context);

        body = (JStatement) body.analyze(this.context);
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
        if (context != null) {
            p.indentRight();
            context.writeToStdOut(p);
            p.indentLeft();
        }
        p.printf("<Variable>\n");
        p.indentRight();
        variable.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Variable>\n");
        p.printf("<Enumeration>\n");
        p.indentRight();
        enumeration.writeToStdOut(p);
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
