// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a try-statement.
 */

class JTryCatchFinally extends JStatement {

    /** Catch blocks. */
    private ArrayList<JStatement> catchBlocks;

    /** Catch blocks. */
    private ArrayList<JVariableDeclaration> exceptionsToCatch;

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

    public JTryCatchFinally(int line, JStatement tryBlock, ArrayList<JStatement> catchBlocks, 
            ArrayList<JVariableDeclaration> exceptionsToCatch, JStatement finallyBlock) {
        super(line);
        this.tryBlock = tryBlock;
        this.catchBlocks = catchBlocks;
        this.exceptionsToCatch = exceptionsToCatch;
        this.finallyBlock = finallyBlock;
    }


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

    public JTryCatchFinally(int line, JStatement tryBlock, ArrayList<JStatement> catchBlocks, 
            ArrayList<JVariableDeclaration> exceptionsToCatch, JStatement finallyBlock) {
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

    public JTryCatchFinally analyze(Context context) {
        
        tryBlock = (JStatement) tryBlock.analyze(context);
        finallyBlock = (JStatement) finallyBlock.analyze(context);
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
        
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JTryCatchFinally line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<TryExpression>\n");
        p.indentRight();
        condition.writeToStdOut(p);
        p.indentLeft();
        p.printf("</TryExpression>\n");
        p.printf("<CatchBlock>\n");
        p.indentRight();
        for(int i =0; i < catches.size(); i++){
            p.printf("<CatchClause >\n");
            p.indentRight();
            p.printf("<Exception>\n");
            p.indentRight();
            exceptions.get(i).writeToStdOut(p);
            p.indentLeft();
            p.printf("</Exception>\n");
            p.indentRight();
            p.printf("<CatchBlock>\n");
            catches.get(i).writeToStdOut(p);
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
