// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a ternary expression. A ternary expression has two operators
 * and three operands.
 */

abstract class JTernaryExpression extends JExpression {

	/** The binary operator. */
	protected String op1, op2;

	/** The lhs operand. */
	protected JExpression exp1, exp2, exp3;

	protected JTernaryExpression(int line, String op1, String op2, JExpression exp1, JExpression exp2,
			JExpression exp3) {
		super(line);
		this.op1 = op1;
		this.op2 = op2;
		this.exp1 = exp1;
		this.exp2 = exp2;
		this.exp3 = exp3;
	}

	/**
	 * @inheritDoc
	 */

	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JTernaryExpression line=\"%d\" type=\"%s\" " + "op1=\"%s\" " + "op2=\"%s\">\n", line(),
				((type == null) ? "" : type.toString()), Util.escapeSpecialXMLChars(op1),
				Util.escapeSpecialXMLChars(op2));
		p.indentRight();
		p.printf("<Exp1>\n");
		p.indentRight();
		exp1.writeToStdOut(p);
		p.indentLeft();
		p.printf("</Exp1>\n");
		p.printf("<Exp2>\n");
		p.indentRight();
		exp2.writeToStdOut(p);
		p.indentLeft();
		p.printf("</Exp2>\n");
		p.printf("<Exp3>\n");
		p.indentRight();
		exp3.writeToStdOut(p);
		p.indentLeft();
		p.printf("</Exp3>\n");
		p.indentLeft();
		p.printf("</JTernaryExpression>\n");
	}

}

/**
 * The AST node for a conditional (cond ? thenBranch : elseBranch ) expression.
 */

class JConditionalExpression extends JTernaryExpression {

	/**
	 * Construct an AST node for conditional expression given its line number, and
	 * three subexpressions
	 * 
	 * @param line line in which the addition expression occurs in the source file.
	 * @param exp1 the exp1 operand.
	 * @param exp2 the exp2 operand.
	 * @param exp3 the exp3 operand.
	 */

	public JConditionalExpression(int line, JExpression exp1, JExpression exp2, JExpression exp3) {
		super(line, "?", ":", exp1, exp2, exp3);
	}

	/**
	 * Analysis involves first analyzing the operands. If this is a string
	 * concatenation, we rewrite the subtree to make that explicit (and analyze
	 * that). Otherwise we check the types of the operands and compute the result
	 * type.
	 * 
	 * @param context context in which names are resolved.
	 * @return the analyzed (and possibly rewritten) AST subtree.
	 */

	public JExpression analyze(Context context) {
		exp1 = (JExpression) exp1.analyze(context);
		exp2 = (JExpression) exp2.analyze(context);
		exp3 = (JExpression) exp3.analyze(context);
		// The condition should be boolean
		exp1.type().mustMatchExpected(line(), Type.BOOLEAN);
		
		// Exp2 and exp3 are branches and should have matching types
        if (!exp2.type().matchesExpected(exp3.type()) && !exp3.type().matchesExpected(exp2.type()))
        	 JAST.compilationUnit.reportSemanticError(line(), "If-else expressions should have matching types");
		
        // The type is determined by the type of the branches
        type = exp2.type();
        
		return this;
	}

	/**
	 * ToDo
	 * 
	 * @param output the code emitter (basically an abstraction for producing the
	 *               .class file).
	 */

	public void codegen(CLEmitter output) {
		/* ToDo */
	}

}