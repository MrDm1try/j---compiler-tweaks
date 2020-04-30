package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

/**
 * A class declaration has a list of modifiers, a name, a super class and a
 * class block; it distinguishes between instance fields and static (class)
 * fields for initialization, and it defines a type. It also introduces its own
 * (class) context.
 */

class JInterfaceDeclaration extends JAST implements JTypeDecl {

    /** Interface modifiers. */
    private ArrayList<String> mods;

    /** Class name. */
    private String name;

    /** Super class type. */
    private Type superType;

    /** This class type. */
    private Type thisType;

    /** Interface block. */
    private ArrayList<JMember> interfaceBlock;    

    /** Context for this interface. */
    private ClassContext context;

    /** Static fields of this class. */
    private ArrayList<JFieldDeclaration> staticFieldInitializations;


    /**
     * Construct an AST node for a class declaration given the line number, list
     * of class modifiers, name of the class, its super class type, and the
     * class block.
     * 
     * @param line
     *            line in which the class declaration occurs in the source file.
     * @param mods
     *            class modifiers.
     * @param name
     *            class name.
     * @param superType
     *            super class type.
     * @param interfaceBlock
     *            class block.
     */

    public JInterfaceDeclaration(int line, ArrayList<String> mods, String name,
            Type superType, ArrayList<JMember> interfaceBlock) {
        super(line);
        this.mods = mods;
        this.name = name;
        this.superType = superType;
        this.interfaceBlock = interfaceBlock;
        staticFieldInitializations = new ArrayList<JFieldDeclaration>();
    }

    /**
     * Return the class name.
     * 
     * @return the class name.
     */

    public String name() {
        return name;
    }

    /**
     * Return the class' super class type.
     * 
     * @return the super class type.
     */

    public Type superType() {
        return superType;
    }

    /**
     * Return the type that this class declaration defines.
     * 
     * @return the defined type.
     */

    public Type thisType() {
        return thisType;
    }

    /**
     * Declare this interface in the parent (compilation unit) context.
     * 
     * @param context
     *            the parent (compilation unit) context.
     */

    public void declareThisType(Context context) {
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        CLEmitter partial = new CLEmitter(false);
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), null,
                false); // Object for superClass, just for now
        thisType = Type.typeFor(partial.toClass());
        context.addType(line, thisType);
    }

    /**
     * Pre-analyze the members of this declaration in the parent context.
     * Pre-analysis extends to the member headers (including method headers) but
     * not into the bodies.
     * 
     * @param context
     *            the parent (compilation unit) context.
     */

    public void preAnalyze(Context context) {        
        this.context = new ClassContext(this, context);
        superType = superType.resolve(this.context);

        thisType.checkAccess(line, superType);
        if (superType.isFinal()) {
            JAST.compilationUnit.reportSemanticError(line,
                    "Cannot extend a final type: %s", superType.toString());
        }

        CLEmitter partial = new CLEmitter(false);

        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        partial.addClass(mods, qualifiedName, superType.jvmName(), null, false);

        for (JMember member : interfaceBlock) {
            member.preAnalyze(this.context, partial);
        }

        Type id = this.context.lookupType(name);
        if (id != null && !JAST.compilationUnit.errorHasOccurred()) {
            id.setClassRep(partial.toClass());
        }
    }

    /**
     * Perform semantic analysis on the class and all of its members within the
     * given context. Analysis includes field initializations and the method
     * bodies.
     * 
     * @param context
     *            the parent (compilation unit) context. Ignored here.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JAST analyze(Context context) {        
        // Analyze all members
        for (JMember member : interfaceBlock) {
            ((JAST) member).analyze(this.context);
        }

        // Copy declared fields for purposes of initialization.
        ArrayList<String> allowedFieldMods = new ArrayList<String>();
        allowedFieldMods.add("public");
        allowedFieldMods.add("static");
        allowedFieldMods.add("final");
        
        for (JMember member : interfaceBlock) {
            if (member instanceof JFieldDeclaration) {
                JFieldDeclaration fieldDecl = (JFieldDeclaration) member;
                ArrayList<String> modsCopy = new ArrayList<String>(fieldDecl.mods());
                modsCopy.removeAll(allowedFieldMods);
                if (!modsCopy.isEmpty()) {
                    JAST.compilationUnit.reportSemanticError(line,
                            "Interface field has illegal modifiers: %s", modsCopy.toString());
                } else {
                    staticFieldInitializations.add(fieldDecl);
                }
            }
        }
        
        return this;
    }

    /**
     * Generate code for the class declaration.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        // The class header
        
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\""
                + " super=\"%s\">\n", line(), name, superType.toString());
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
        if (interfaceBlock != null) {
            p.println("<InterfaceBlock>");
            for (JMember member : interfaceBlock) {
                ((JAST) member).writeToStdOut(p);
            }
            p.println("</InterfaceBlock>");
        }
        p.indentLeft();
        p.println("</JInterfaceDeclaration>");
    }

    
}
