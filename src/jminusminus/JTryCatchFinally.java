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
    
    /** Offsets for storing exception types in stack */
    private int offset;
    private int finallyOffset;

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
        this.offset = ((LocalContext) context).nextOffset();
        this.finallyOffset = ((LocalContext) context).nextOffset();
        tryBlock = (JStatement) tryBlock.analyze(context);
        for (int i = 0; i < catchBlocks.size(); i++)
        	catchBlocks.set(i, (JStatement) catchBlocks.get(i).analyze(context));
        if (finallyBlock != null) {
			finallyBlock = (JStatement) finallyBlock.analyze(context);
        }

        for (int i = 0; i < exceptionsToCatch.size(); i++) {
        	exceptionsToCatch.get(i).setType(exceptionsToCatch.get(i).type().resolve(context));
            if (!Type.EXCEPTION.isJavaAssignableFrom(exceptionsToCatch.get(i).type())) {
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
        String endLabel = output.createLabel();
        String tryStart = output.createLabel();
        String tryEnd = output.createLabel();
		String finallyStart = output.createLabel();
		String finallyEnd = output.createLabel();

		// to label start of every catch clause
        ArrayList<String> startCatchLabels = new ArrayList<String>();
		// to label end of every catch clause
        ArrayList<String> endCatchLabels = new ArrayList<String>();
        
        /* Generating instructions */
        
		output.addLabel(tryStart);
        tryBlock.codegen(output);
		output.addLabel(tryEnd);
        if (finallyBlock != null) {
			finallyBlock.codegen(output);
        }
		output.addBranchInstruction(GOTO, endLabel);

        for(int i = 0; i < catchBlocks.size(); i++) {
        	startCatchLabels.add(output.createLabel());
        	endCatchLabels.add(output.createLabel());

			output.addLabel(startCatchLabels.get(i));

			switch (this.offset) {
				case 0:
					output.addNoArgInstruction(ASTORE_0);
					break;
				case 1:
					output.addNoArgInstruction(ASTORE_1);
					break;
				case 2:
					output.addNoArgInstruction(ASTORE_2);
					break;
				case 3:
					output.addNoArgInstruction(ASTORE_3);
					break;
				default:
					output.addOneArgInstruction(ASTORE, this.offset);
					break;
			}
			catchBlocks.get(i).codegen(output);
			if (finallyBlock != null) {
				finallyBlock.codegen(output);
			}
			// GOTO instruction after the last catch block can be skipped
			// unless there is a finally block
			if ((finallyBlock != null) || (i < catchBlocks.size() - 1))
				output.addBranchInstruction(GOTO, endLabel);

			output.addLabel(endCatchLabels.get(i));
        }

		if (finallyBlock != null) {
			output.addLabel(finallyStart);
			switch (this.finallyOffset) {
				case 0:
					output.addNoArgInstruction(ASTORE_0);
					break;
				case 1:
					output.addNoArgInstruction(ASTORE_1);
					break;
				case 2:
					output.addNoArgInstruction(ASTORE_2);
					break;
				case 3:
					output.addNoArgInstruction(ASTORE_3);
					break;
				default:
					output.addOneArgInstruction(ASTORE, this.finallyOffset);
					break;
			}
			finallyBlock.codegen(output);
			switch (this.finallyOffset) {
				case 0:
					output.addNoArgInstruction(ALOAD_0);
					break;
				case 1:
					output.addNoArgInstruction(ALOAD_1);
					break;
				case 2:
					output.addNoArgInstruction(ALOAD_2);
					break;
				case 3:
					output.addNoArgInstruction(ALOAD_3);
					break;
				default:
					output.addOneArgInstruction(ALOAD, this.finallyOffset);
					break;
			}
			output.addNoArgInstruction(ATHROW);
			output.addLabel(finallyEnd);
		}

		output.addLabel(endLabel);

        /* Filling in the exception table */
        
		// Directing exceptions from try block to designated catch blocks 
        for(int i = 0; i < exceptionsToCatch.size(); i++) {
        	output.addExceptionHandler(tryStart, tryEnd, startCatchLabels.get(i), exceptionsToCatch.get(i).type().jvmName());
        }

		// Directing exceptions from try block, all catch blocks and finally block to finally block 
        // to ensure that if an exception is raised in any of these, the code for the finally block is executed
		if (finallyBlock != null) {
        	output.addExceptionHandler(tryStart, tryEnd, finallyStart, null);
			for(int i = 0; i < startCatchLabels.size(); i++) {
				output.addExceptionHandler(startCatchLabels.get(i), endCatchLabels.get(i), finallyStart, null);			
			}
        	output.addExceptionHandler(finallyStart, finallyEnd, finallyStart, null);
		}
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
