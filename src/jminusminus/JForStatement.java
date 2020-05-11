// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;
import java.util.ArrayList;

/**
 * The AST node for a for-statement.
 */

class JForStatement extends JStatement {

	/** Loop variable - only one is used. */
	private JVariableDeclaration varDecl;
	private JStatement initializer;

	/** Test expression. */
	private JExpression termination;

	/** Variable increment expressions. */
	private ArrayList<JStatement> incrementList;

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

	public JForStatement(int line, JStatement initializer, JExpression termination, ArrayList<JStatement> incrementList,
			JStatement body) {
		super(line);
		this.initializer = initializer;
		this.varDecl = null;
		this.termination = termination;
		this.incrementList = incrementList;
		this.body = body;
	}

	public JForStatement(int line, JVariableDeclaration varDecl, JExpression termination, ArrayList<JStatement> incrementList,
			JStatement body) {
		super(line);
		this.initializer = null;
		this.varDecl = varDecl;
		this.termination = termination;
		this.incrementList = incrementList;
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

		if (initializer != null) {
			initializer = (JStatement) initializer.analyze(this.context);
		} else if (varDecl != null){
			varDecl = (JVariableDeclaration) varDecl.analyze(this.context);
		}

		if (termination != null) {
			termination = termination.analyze(this.context);
			termination.type().mustMatchExpected(line(), Type.BOOLEAN);
		}

		for (int i = 0; i<incrementList.size(); i++)
			incrementList.set(i, (JStatement) incrementList.get(i).analyze(this.context));

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
		if (initializer != null) {
			initializer.codegen(output);
		} else if (varDecl != null){
			varDecl.codegen(output);
		}

		String repeatLable = output.createLabel();
		String outLable = output.createLabel();
		
		output.addLabel(repeatLable);
		if (termination != null) {
			termination.codegen(output, outLable, false);
		}
        body.codegen(output);
		for (JStatement incrementExpr : incrementList)
			incrementExpr.codegen(output);
		output.addBranchInstruction(GOTO, repeatLable);
		output.addLabel(outLable);
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
		if (initializer != null) {
			p.printf("<Initializer>\n");
			p.indentRight();
			initializer.writeToStdOut(p);
			p.indentLeft();
			p.printf("</Initializer>\n");
		} else if (varDecl != null) {
			p.printf("<VarDecl>\n");
			p.indentRight();
			varDecl.writeToStdOut(p);
			p.indentLeft();
			p.printf("</VarDecl>\n");
		}
		p.printf("<Termination>\n");
		p.indentRight();
		if (termination != null)
			termination.writeToStdOut(p);
		p.indentLeft();
		p.printf("</Termination>\n");
		p.printf("<IncrementList>\n");
		p.indentRight();
		for (JStatement increment : incrementList) {
			increment.writeToStdOut(p);
		}
		p.indentLeft();
		p.printf("</IncrementList>\n");
		p.printf("<Body>\n");
		p.indentRight();
		body.writeToStdOut(p);
		p.indentLeft();
		p.printf("</Body>\n");
		p.indentLeft();
		p.printf("</JForStatement>\n");
	}

}
