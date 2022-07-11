/*
    Created by: Adrian Clinansmith
    File Name: CM.java
*/
     
import java.io.*;
import java.util.Arrays;

import absyn.*;
     
class CM {

    // ************************************************************************
    // Main
    // ************************************************************************

    public static void main(String[] argv) {        
        /* Start the parser */
        if (argv.length == 0) {
            System.err.println("usage: java CM inputFile [flags] outputFile");
            return;
        }
        String inputFile = argv[0];
        String outputFile = argv.length > 2 ? argv[2] : null;
        boolean shouldShowTree = Arrays.asList(argv).contains("-a");
        boolean shouldShowTable = Arrays.asList(argv).contains("-s");
        boolean shouldShowCode = Arrays.asList(argv).contains("-c");
        try {
            parser parserObject = initParser(inputFile);
            Absyn ast = (Absyn)(parserObject.parse().value);          
            if (shouldShowTree) {
                showTree(ast);
            } else if (shouldShowTable) {
                showTable(ast);
            } else if (shouldShowCode) {
                showCode(ast, outputFile);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not find file \"" + inputFile + "\"");
        } catch (Exception e) {
            /* do cleanup here -- possibly rethrow e */
            e.printStackTrace();
        }
    }

    // ************************************************************************
    // Private Methods
    // ************************************************************************

    /*
        Initialize the parser with the given file.
    */
    private static parser initParser(String filename) throws Exception {
        return new parser(new Lexer(new FileReader(filename)));
    }

    private static void showTree(Absyn ast) {
        if (ast == null) {
            return;
        }
        System.out.println("\n");
        ShowTreeVisitor visitor = new ShowTreeVisitor();
        ast.accept(visitor, 0); 
        System.out.println("\n");
        ast.outputErrors();
    }

    private static void showTable(Absyn ast) {
        if (ast == null) {
            return;
        } else if  (ast.hasError()) {
            System.err.println("Could not perform semantic analysis.");
            ast.outputErrors();
            return;
        }
        System.out.println("\n");
        SemanticAnalyzer semAnalyzer = new SemanticAnalyzer();
        boolean success = semAnalyzer.analyze(ast, true);
        System.out.println("\n");
        if (!success) {
            System.err.println("errors:");
        }
        for (String error : semAnalyzer.errors) {
            System.err.println(error);
        }
    }

    private static void showCode(Absyn ast, String outputFile) {
        if (ast == null) {
            return;
        } else if (ast.hasError()) {
            ast.outputErrors();
            return;
        }
        SemanticAnalyzer semAnalyzer = new SemanticAnalyzer();
        boolean success = semAnalyzer.analyze(ast, false);
        if (!success) {
            System.err.println("semantic errors:");
            for (String error : semAnalyzer.errors) {
                System.err.println(error);
            }
            return;
        }
        CodeGenerator codeGen = new CodeGenerator(outputFile);
        codeGen.generate(ast);
    }
}


