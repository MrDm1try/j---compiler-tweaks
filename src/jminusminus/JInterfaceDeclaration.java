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

    /** extends */
    private ArrayList<Type> extendsTypes;

    /** This class type. */
    private Type thisType;

    /** Interface block. */
    private ArrayList<JMember> interfaceBlock;    

    /** Context for this interface. */
    private ClassContext context;

    /** Static fields of this interface. */
    private ArrayList<JFieldDeclaration> staticFieldInitializations;


    /**
     * Construct an AST node for an interface declaration 
     */

    public JInterfaceDeclaration(int line, ArrayList<String> mods, String name,
    		ArrayList<Type> extendsTypes, ArrayList<JMember> interfaceBlock) {
        super(line);
        this.mods = mods;
        this.name = name;
        this.extendsTypes = extendsTypes;
        this.interfaceBlock = interfaceBlock;
        staticFieldInitializations = new ArrayList<JFieldDeclaration>();
        
        if (!this.mods.contains("interface"))
        	this.mods.add("interface");
        if (!this.mods.contains("abstract"))
        	this.mods.add("abstract");
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
        return Type.OBJECT;
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
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), null, false); // Object for superClass, just for now
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
        
        // Resolve interfaces
        ArrayList<String> interfaceTypeNames = new ArrayList<String>();
    	for (int i = 0; i < extendsTypes.size(); i++) {
    		extendsTypes.set(i, extendsTypes.get(i).resolve(this.context));
			thisType.checkAccess(line, extendsTypes.get(i));

			if (!extendsTypes.get(i).isInterface())
	            JAST.compilationUnit.reportSemanticError(line,
	                    "Extended type is not an interface: %s", extendsTypes.get(i).toString());
        }

        CLEmitter partial = new CLEmitter(false);

        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        partial.addClass(mods, qualifiedName, superType().jvmName(), interfaceTypeNames, false);

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
        ArrayList<String> disallowedMethodMods = new ArrayList<String>();
        disallowedMethodMods.add("static");
        disallowedMethodMods.add("final");
        
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
            } else if (member instanceof JMethodDeclaration) {
            	JMethodDeclaration methodDecl = (JMethodDeclaration) member;
                ArrayList<String> modsCopy = new ArrayList<String>(methodDecl.mods());
                modsCopy.retainAll(disallowedMethodMods);
                
                if (!modsCopy.isEmpty()) {
                    JAST.compilationUnit.reportSemanticError(line,
                            "Interface method has illegal modifiers: %s", modsCopy.toString());
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
        p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\">\n", line(), name);
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
        if (extendsTypes != null) {
        	p.println("<ExtendsTypes>");
            p.indentRight();
        	for (Type extendsType : extendsTypes) {
                p.printf("<ExtendsType name=\"%s\"/>\n", extendsType.toString());
        	}
            p.indentLeft();
        	p.println("<ExtendsTypes>");
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
