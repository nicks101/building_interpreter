package lox;

import java.util.List;
import java.util.function.Supplier;

class Parser {

    private static class ParseError extends RuntimeException{}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    Expr parse(){
        try{
            return expression();
        } catch (ParseError error){
            return null;
        }
    }

    private Expr expression(){
        return equality();
    }

    private Expr equality(){
        // Expr expr = comparison();

        // while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)){
        //     Token operator = previous();
        //     Expr right = comparison();
        //     expr = new Expr.Binary(expr, operator, right);
        // }

        // return expr;

        return parseBinary(this::comparison, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL);
    }

    private Expr comparison(){
        return parseBinary(this::term, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL);
    }

    private Expr term(){
        return parseBinary(this::factor, TokenType.MINUS, TokenType.PLUS);
    }

    private Expr factor(){
        return parseBinary(this::unary, TokenType.SLASH, TokenType.STAR);
    }

    private Expr unary(){
        if(match(TokenType.BANG, TokenType.MINUS)){
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary(){
        if(match(TokenType.FALSE)) return new Expr.Literal(false);
                if(match(TokenType.TRUE)) return new Expr.Literal(true);
        if(match(TokenType.NIL)) return new Expr.Literal(null);

        if(match(TokenType.NUMBER, TokenType.STRING)){
            return new Expr.Literal(previous().literal);
        }
        
        if(match(TokenType.LEFT_PAREN)){
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");

    }

    // Helper method for parsing left-associative series of binary operators
    private Expr parseBinary(Supplier<Expr> operandExpr, TokenType... types){
        Expr expr = operandExpr.get();

        while(match(types)){
            Token operator = previous();
            Expr right = operandExpr.get();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private boolean match(TokenType... types){
        for (TokenType type: types){
            if(check(type)){
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message){
        if(check(type)) return advance();

        throw error(peek(), message);
    }


    private boolean check(TokenType type){
        if(isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance(){
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd(){
        return peek().type == TokenType.EOF;
    }

    private Token peek(){
        return tokens.get(current);
    }

    private Token previous(){
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message){
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize(){
        advance();

        while(!isAtEnd()){
            if(previous().type == TokenType.SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
                default:
                    break;
            }

            advance();
        }
    }

}
