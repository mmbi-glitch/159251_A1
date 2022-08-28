package com.c159251.a1.jtexteditor;

import java.util.regex.Pattern;

public final class SyntaxHighlighter {

    private static final String[] CPP_KEYWORDS = new String[] {
            "asm", "bool", "catch", "class", "const_cast", "delete",
            "dynamic_cast", "explicit", "false", "friend", "inline",
            "mutable", "namespace", "new", "operator", "private",
            "public", "protected", "reinterpret_cast", "static_cast",
            "template", "this", "throw", "true", "try", "typeid", "typename",
            "using", "virtual", "wchar_t", "cin", "cout", "endl", "include",
            "NULL", "string", "auto", "break", "case", "char",
            "continue", "default", "do", "double", "else", "else if", "entry",
            "extern", "float", "for", "goto", "if", "int", "long", "register",
            "return", "short", "sizeof", "static", "struct", "switch",
            "typedef", "union", "unsigned", "void", "while",
    };

    private static final String[] JAVA_KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "String", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String CPP_KEYWORD_PATTERN = "\\b(" + String.join("|", CPP_KEYWORDS) + ")\\b";
    private static final String CPP_LIB_PATTERN = "\\<(.*?)\\>";
    private static final String NUMBER_PATTERN = "[0-9]+";
    private static final String JAVA_KEYWORD_PATTERN = "\\b(" + String.join("|", JAVA_KEYWORDS) + ")\\b";
    private static final String JAVA_LIB_PATTERN = "(\\w+)(?=[.])";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String CAP_WORD_PATTERN = "\\b[A-Z].*?\\b";
    private static final String ANNOTATION_PATTERN = "\\B\\@\\w+";
    private static final String FUNCTION_PATTERN = "(\\w+)(?=\\s?\\()";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    public static final Pattern CPP_PATTERN = Pattern.compile(
            "(?<KEYWORD>" + CPP_KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
                    + "|(?<CAPWORD>" + CAP_WORD_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                    + "|(?<ANNOTATION>" + ANNOTATION_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    public static final Pattern JAVA_PATTERN = Pattern.compile(
            "(?<KEYWORD>" + JAVA_KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                    + "|(?<LIBRARY>" + JAVA_LIB_PATTERN + ")"
                    + "|(?<CAPWORD>" + CAP_WORD_PATTERN + ")"
                    + "|(?<ANNOTATION>" + ANNOTATION_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
}
