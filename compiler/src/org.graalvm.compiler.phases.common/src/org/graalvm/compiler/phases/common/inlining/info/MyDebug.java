package org.graalvm.compiler.phases.common.inlining.info;

public class MyDebug {

    static int ExactInlineCounter = 0;
    static int AssumptionInlineCounter = 0;
    static int MultitypeInlineCounter = 0;
    static int TypeguardInlineCounter = 0;

    static int Monomorph = 0;
    static int Biomorph = 0;
    static int Polymorph = 0;

    static int notinlinecounter = 0;

    public void incExactInline() {
        ExactInlineCounter++;
    }

    public void incAssumpInline() {
        AssumptionInlineCounter++;
    }

    public void incMultiInline() {
        MultitypeInlineCounter++;
    }

    public void incTypeGuardInline() {
        TypeguardInlineCounter++;
    }

    public void incMonomorph() {
        Monomorph++;
    }

    public void incBimorph() {
        Biomorph++;
    }

    public void incPolymorph() {
        Polymorph++;
    }

    public void incnotinline() {
        notinlinecounter++;
    }

    public void printData() {
        System.out.println("**********************************************");
        System.out.println("Exactinline called is : " + ExactInlineCounter);
        System.out.println("Assumptioninline called is : " + AssumptionInlineCounter);
        System.out.println("Multitypeinline called is : " + MultitypeInlineCounter);
        System.out.println("TypeGuardinline called is : " + TypeguardInlineCounter);
        System.out.println("Monomorphic calls : " + Monomorph + " Bimorphic calls : " + Biomorph + " Polymorphic Calls : " + Polymorph);
        System.out.println("**************************************");
        System.out.println("Inlining can't be done for : " + TypeguardInlineCounter);
    }
}
