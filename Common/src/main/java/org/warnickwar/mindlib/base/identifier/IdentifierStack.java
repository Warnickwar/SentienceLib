package org.warnickwar.mindlib.base.identifier;

import java.util.LinkedList;

public class IdentifierStack<T> {

    public static final String SEPARATOR = "\\.";

    private final LinkedList<AiIdentifier<T>> identifiers = new LinkedList<>();

    public void push(AiIdentifier<T> identifier) {
        identifiers.add(identifier);
    }

    public AiIdentifier<T> pop() {
        return identifiers.removeLast();
    }

    public AiIdentifier<T> peek() {
        return identifiers.peekLast();
    }

    public LinkedList<AiIdentifier<T>> getFullStack() {
        return new LinkedList<>(identifiers);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("IdentifierStack{");
        for (AiIdentifier<T> identifier : identifiers) {
            sb.append(identifier.toString()).append(SEPARATOR);
        }
        sb.deleteCharAt(sb.length()-1).append('}');
        return sb.toString();
    }

    public static <T> IdentifierStack<T> parse(String str) {
        String[] values = str.split(SEPARATOR);
        IdentifierStack<T> stack = new IdentifierStack<T>();
        for (String value : values) {
            stack.push(new AiIdentifier<>(value));
        }
        return stack;
    }

}
