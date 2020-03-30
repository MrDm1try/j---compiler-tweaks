// Copyright 2011 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a return-statement. If the enclosing method in non-void,
 * then there is a value to return, so we keep track of the expression denoting
 * that value and its type.
 */

class JThrowStatement extends JStatement {

	/** The returned expression. */
	private JExpression expr;

	/**
	 * Construct an AST node for a return-statement given its line number, and the
	 * expression that is returned.
	 * 
	 * @param line line in which the return-statement appears in the source file.
	 * @param expr the returned expression.
	 */

	public JThrowStatement(int line, JExpression expr) {
		super(line);
		this.expr = expr;
	}

	/**
	 * Analysis distinguishes between our being in a constructor or in a regular
	 * method in checking return types. In the case of a return expression, analyze
	 * it and check types. Determine the (possibly void) return type.
	 * 
	 * @param context context in which names are resolved.
	 * @return the analyzed (and possibly rewritten) AST subtree.
	 */

	public JStatement analyze(Context context) {
		// todo type check
		return this;
	}

	public void codegen(CLEmitter output) {
		// todo
	}

	/**
	 * @inheritDoc
	 */

	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JThrowStatement line=\"%d\">\n", line());
		p.indentRight();
		expr.writeToStdOut(p);
		p.indentLeft();
		p.printf("</JThrowStatement>\n");
	}
}
