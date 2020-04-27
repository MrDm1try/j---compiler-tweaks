// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a for-statement.
 */

class JForStatement extends JStatement {

	/** Loop variable - only one is used. */
	private JVariableDeclaration varDecl;
	private JExpression initializer;
	
	/** Used if loop variable was declared in the loop statement. */
	private JStatement analyzedVarDecl;

	/** Test expression. */
	private JExpression termination;

	/** Variable increment expression. */
	private JExpression increment;

	/** The body. */
	private JStatement body;

    /** Context for this loop. */
    private LocalContext context;

	/**
	 * Construct an AST node for a for-statement given its line number, the test
	 * expression, and the body.
	 * 
	 * @param line      line in which the for-statement occurs in the source file.
	 * @param condition test expression.
	 * @param body      the body.
	 */

	public JForStatement(int line, JExpression initializer, JExpression termination, JExpression increment,
			JStatement body) {
		super(line);
		this.initializer = initializer;
		this.varDecl = null;
		this.analyzedVarDecl = null;
		this.termination = termination;
		this.increment = increment;
		this.body = body;
	}

	public JForStatement(int line, JVariableDeclaration varDecl, JExpression termination, JExpression increment,
			JStatement body) {
		super(line);
		this.initializer = null;
		this.varDecl = varDecl;
		this.analyzedVarDecl = null;
		this.termination = termination;
		this.increment = increment;
		this.body = body;
	}

	/**
	 * Analysis involves analyzing the test, checking its type and analyzing the
	 * body statement.
	 * 
	 * @param context context in which names are resolved.
	 * @return the analyzed (and possibly rewritten) AST subtree.
	 */

	public JForStatement analyze(Context context) {
        // we define a new context for the whole loop
        this.context = new LocalContext(context);

		if (varDecl == null) {
			initializer = initializer.analyze(this.context);
		} else {
			analyzedVarDecl = varDecl.analyze(this.context);
		}

		termination = termination.analyze(this.context);
		termination.type().mustMatchExpected(line(), Type.BOOLEAN);

		increment = increment.analyze(this.context);

		body = (JStatement) body.analyze(this.context);
		return this;
	}

	/**
	 * Generate code for the for-loop.
	 * 
	 * @param output the code emitter (basically an abstraction for producing the
	 *               .class file).
	 */

	public void codegen(CLEmitter output) {
		// TODO fix

		// starting with the loop-variable
		// variable.codegen(output);

		// Need two labels
		String test = output.createLabel();
		String out = output.createLabel();

		// Branch out of the loop on the test condition
		// being false
		output.addLabel(test);
		termination.codegen(output, out, false);

		// Codegen body
		body.codegen(output);
		increment.codegen(output);

		// Unconditional jump back up to test
		output.addBranchInstruction(GOTO, test);

		// The label below and outside the loop
		output.addLabel(out);
	}

	/**
	 * @inheritDoc
	 */

	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JForStatement line=\"%d\">\n", line());
		p.indentRight();
        if (context != null) {
            p.indentRight();
            context.writeToStdOut(p);
            p.indentLeft();
        }
		if (varDecl == null) {
			p.printf("<Initializer>\n");
			p.indentRight();
			initializer.writeToStdOut(p);
			p.indentLeft();
			p.printf("</Initializer>\n");
		} else {
			p.printf("<VarDecl>\n");
			p.indentRight();
			varDecl.writeToStdOut(p);
			p.indentLeft();
			p.printf("</VarDecl>\n");
		}
		p.printf("<Termination>\n");
		p.indentRight();
		termination.writeToStdOut(p);
		p.indentLeft();
		p.printf("</Termination>\n");
		p.printf("<Increment>\n");
		p.indentRight();
		increment.writeToStdOut(p);
		p.indentLeft();
		p.printf("</Increment>\n");
		p.printf("<Body>\n");
		p.indentRight();
		body.writeToStdOut(p);
		p.indentLeft();
		p.printf("</Body>\n");
		p.indentLeft();
		p.printf("</JForStatement>\n");
	}

}
