// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;
import java.util.ArrayList;

/**
 * The AST node for a try-statement.
 */

class JTryCatchFinallyStatement extends JStatement {

    /** Catch blocks. */
    private ArrayList<JStatement> catchBlocks;

    /** Catch expressions. */
    private ArrayList<JFormalParameter> exceptionsToCatch;

    /** Try block. */
    private JStatement tryBlock;

    /** Finally block. */
    private JStatement finallyBlock;

    /**
     * Construct an AST node for a try-statement given its line number, the
     * test expression, and the body.
     * 
     * @param line
     *            line in which the try-statement occurs in the source file.
     * @param condition
     *            test expression.
     * @param body
     *            the body.
     */

    public JTryCatchFinallyStatement(int line, JStatement tryBlock, ArrayList<JStatement> catchBlocks, 
            ArrayList<JFormalParameter> exceptionsToCatch, JStatement finallyBlock) {
        super(line);
        this.tryBlock = tryBlock;
        this.catchBlocks = catchBlocks;
        this.exceptionsToCatch = exceptionsToCatch;
        this.finallyBlock = finallyBlock;
    }

    /**
     * Analysis involves analyzing the test, checking its type and analyzing the
     * body statement.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JTryCatchFinallyStatement analyze(Context context) {
        tryBlock = (JStatement) tryBlock.analyze(context);
        for (int i = 0; i < catchBlocks.size(); i++)
        	catchBlocks.set(i, (JStatement) catchBlocks.get(i).analyze(context));
        finallyBlock = (JStatement) finallyBlock.analyze(context);
        

        for (JFormalParameter catchException : exceptionsToCatch) {
        	catchException.setType(catchException.type().resolve(context));
        }
        
        for (JFormalParameter catchException : exceptionsToCatch) {
            if (!Type.EXCEPTION.isJavaAssignableFrom(catchException.type())) {
    			JAST.compilationUnit.reportSemanticError(line(), "Catch expression is not an exception.");
            }
        }
        
        return this;
    }

    /**
     * Generate code for the try-catch-finally statement loop.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        //todo
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JTryCatchFinallyStatement line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<TryExpression>\n");
        p.indentRight();
        tryBlock.writeToStdOut(p);
        p.indentLeft();
        p.printf("</TryExpression>\n");
        p.printf("<CatchBlock>\n");
        p.indentRight();
        for(int i = 0; i < catchBlocks.size(); i++){
            p.printf("<CatchClause>\n");
            p.indentRight();
            p.printf("<Exception>\n");
            p.indentRight();
            exceptionsToCatch.get(i).writeToStdOut(p);
            p.indentLeft();
            p.printf("</Exception>\n");
            p.printf("<CatchBlock>\n");
            p.indentRight();
            catchBlocks.get(i).writeToStdOut(p);
            p.indentLeft();
            p.print("</CatchBlock>\n");
            p.indentLeft();
            p.printf("</CatchClause >\n");
        }
        p.indentLeft();
        p.printf("</CatchBlock>\n");
        if(finallyBlock != null){
            p.indentLeft();
            p.printf("<FinallyBlock>\n");
            p.indentRight();
            p.printf("<FinallyClause>\n");
            p.indentRight();
            finallyBlock.writeToStdOut(p);
            p.indentLeft();
            p.printf("</FinallyClause>\n");
            p.indentLeft();
            p.printf("</FinallyBlock>\n");
        }
        p.indentLeft();
        p.printf("</JTryCatchFinally>\n");
    }

}
