// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

/**
 * The AST node for a constructor declaration. A constructor looks very much
 * like a method.
 */

class JInitializationBlockDeclaration extends JMethodDeclaration implements JMember {

    /**
     * Construct an AST node for a constructor declaration given the line
     * number, modifiers, constructor name, formal parameters, and the
     * constructor body.
     * 
     * @param line
     *            line in which the constructor declaration occurs in the source
     *            file.
     * @param mods
     *            modifiers.
     * @param name
     *            constructor name.
     * @param params
     *            the formal parameters.
     * @param body
     *            constructor body.
     */

    public JInitializationBlockDeclaration(int line, ArrayList<String> mods, JBlock body)

    {
        super(line, mods, null, Type.INITIALIZATION_BLOCK, null, body);
    }

    /**
     * Declare this constructor in the parent (class) context.
     * 
     * @param context
     *            the parent (class) context.
     * @param partial
     *            the code emitter (basically an abstraction for producing the
     *            partial class).
     */

    public void preAnalyze(Context context, CLEmitter partial) {
        if (isPrivate) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Initialization block cannot be declared private");
        } else if (isAbstract) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Initialization block cannot be declared abstract");
        } else if (mods.contains("protected")) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Initialization block cannot be declared protected");
        } else if (mods.contains("public")) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Initialization block cannot be declared public");
        }
    }

    /**
     * TODO
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JAST analyze(Context context) {
        // Record the defining class declaration.
        MethodContext methodContext = new MethodContext(context, isStatic, returnType);
        this.context = methodContext;

        if (!isStatic) {
            // Offset 0 is used to address "this"
            this.context.nextOffset();
        }

        if (body != null) {
            body = body.analyze(this.context);
        }
        return this;

    }

    /**
     * TODO
     * 
     * @param context
     *            the parent (class) context.
     * @param partial
     *            the code emitter (basically an abstraction for producing the
     *            partial class).
     */

    public void partialCodegen(Context context, CLEmitter partial) {
    	// TODO
    }

    /**
     * Generate code for the initialization block declaration.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
    	// TODO
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JInitializationBlockDeclaration line=\"%d\" " + "name=\"%s\">\n",
                line(), name);
        p.indentRight();
        if (context != null) {
            context.writeToStdOut(p);
        }
        if (mods != null) {
            p.println("<Modifiers>");
            p.indentRight();
            for (String mod : mods) {
                p.printf("<Modifier name=\"%s\"/>\n", mod);
            }
            p.indentLeft();
            p.println("</Modifiers>");
        }
        if (body != null) {
            p.println("<Body>");
            p.indentRight();
            body.writeToStdOut(p);
            p.indentLeft();
            p.println("</Body>");
        }
        p.indentLeft();
        p.println("</JInitializationBlockDeclaration>");
    }

}
