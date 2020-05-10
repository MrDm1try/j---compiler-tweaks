// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

import java.util.ArrayList;

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

    public JStatement analyze(Context context) {
    	this.context = new LocalContext(context);
        String generatedName = context.compilationUnitContext().generateVariableName();
        
        enumeration = (JExpression) enumeration.analyze(this.context);
        
        JStatement replacement = null;
        
        if (enumeration.type().isArray()) {
            String generatedName2 = context.compilationUnitContext().generateVariableName();
            
        	// Init variable decl
        	ArrayList<JVariableDeclarator> initDecls = new ArrayList<JVariableDeclarator>();
        	initDecls.add(new JVariableDeclarator(line, generatedName, Type.INT, new JLiteralInt(line, "0")));
        	JVariableDeclaration initDeclaration = new JVariableDeclaration(line, new ArrayList<String>(), initDecls);
        	
        	// Assign variable to array expression
        	ArrayList<JVariableDeclarator> arrayDecls = new ArrayList<JVariableDeclarator>();
        	arrayDecls.add(new JVariableDeclarator(line, generatedName2, enumeration.type(), enumeration));
        	JVariableDeclaration arrayDeclaration = new JVariableDeclaration(line, new ArrayList<String>(), arrayDecls);
        	
        	// Variable object
        	JVariable loopCountVar = new JVariable(line, generatedName);
        	JVariable enumerationVar = new JVariable(line, generatedName2);
        	
        	// Termination
        	JExpression termination = new JGreaterThanOp(line, new JFieldSelection(line, enumerationVar, "length"), loopCountVar);
        	
        	// Increment 
        	ArrayList<JStatement> incrementList = new ArrayList<JStatement>();
        	incrementList.add(new JPostIncrementOp(line, loopCountVar));
        	((JPostIncrementOp) incrementList.get(0)).isStatementExpression = true;
        	
        	// Select current
        	ArrayList<JVariableDeclarator> nextDecls = new ArrayList<JVariableDeclarator>();
        	JExpression nextInitializerLhs = new JVariable(line, variable.name());
        	nextDecls.add(new JVariableDeclarator(line, variable.name(), variable.type(), new JArrayExpression(line, enumerationVar, loopCountVar)));
        	JVariableDeclaration nextDeclaration = new JVariableDeclaration(line, new ArrayList<String>(), nextDecls);
        	
        	// Add to loop body
        	ArrayList<JStatement> newStatements = new ArrayList<JStatement>();
        	newStatements.add(nextDeclaration);
        	if (body instanceof JBlock)
        		newStatements.addAll(((JBlock) body).statements());
        	else
        		newStatements.add(body);        	
        	JBlock newLoopBody = new JBlock(line, newStatements);
        	
        	ArrayList<JStatement> replacementBlockStatements = new ArrayList<JStatement>();
        	replacementBlockStatements.add(arrayDeclaration);
        	replacementBlockStatements.add(new JForStatement(line, initDeclaration, termination, incrementList, newLoopBody)); 
        	
        	replacement = new JBlock(line, replacementBlockStatements); 
        } else if (enumeration.type().isJavaInterfaceImplemented(Type.ITERABLE)) {
        	// Init variable decl
        	ArrayList<JVariableDeclarator> initDecls = new ArrayList<JVariableDeclarator>();
        	JMessageExpression iteratorMethod = new JMessageExpression(line, enumeration, "iterator", new ArrayList<JExpression>());
        	JExpression initializerLhs = new JVariable(line, generatedName);
        	JExpression initializer = new JAssignOp(line, initializerLhs, iteratorMethod);
        	initDecls.add(new JVariableDeclarator(line, generatedName, Type.ITERATOR, initializer));
        	JVariableDeclaration initDeclaration = new JVariableDeclaration(line, new ArrayList<String>(), initDecls);
        	
        	// Termination
        	JExpression termination = new JMessageExpression(line, initializerLhs, "hasNext", new ArrayList<JExpression>());
        	
        	// Variable for next
        	ArrayList<JVariableDeclarator> loopDecls = new ArrayList<JVariableDeclarator>();
        	JMessageExpression enumerationSelector = new JMessageExpression(line, initializerLhs, "next", new ArrayList<JExpression>());
        	JExpression variableInitializer = new JAssignOp(line, new JVariable(line, variable.name()), enumerationSelector);
        	loopDecls.add(new JVariableDeclarator(line, variable.name(), variable.type(), variableInitializer));
        	JVariableDeclaration loopDeclaration = new JVariableDeclaration(line, new ArrayList<String>(), loopDecls);
        	
        	// Add to loop body
        	ArrayList<JStatement> newStatements = new ArrayList<JStatement>();
        	newStatements.add(loopDeclaration);
        	if (body instanceof JBlock)
        		newStatements.addAll(((JBlock) body).statements());
        	else
        		newStatements.add(body);        	
        	JBlock newBody = new JBlock(line, newStatements);
        	
        	replacement = new JForStatement(line, initDeclaration, termination, new ArrayList<JStatement>(), newBody);
        } else {
			JAST.compilationUnit.reportSemanticError(line(), "Enumeration is neither an array nor iterable.");
        }
    	
    	return (JStatement) replacement.analyze(this.context);
    }

    /**
     * Generate code for the for-each-loop.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        // Handled elsewhere
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
