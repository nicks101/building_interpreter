import java.util.ArrayList;
import java.util.List;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    // First character in the lexeme being scanned
    private int start = 0;

    // Current character being considered
    private int current = 0;

    // Source line [current] is on
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken(){

        /// Single character tokens
        char c = advance();
        switch(c){
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRANCE); break;
            case ',': addToken(TokenType.COMMA); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case '*': addToken(TokenType.STAR); break;

            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); 
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;

            case '/':
                if(match('/')){
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else{
                    addToken(TokenType.SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n': line++; break;

            case '"': string(); break;

            default:
                Lox.error(line,  "Unexpected character.");
                break;
        }
    }

    private void string(){
        while(peek() != '"' && !isAtEnd()){
            if(peek() == '\n') line++;
            advance();
        }

        if(isAtEnd()){
            Lox.error(line, "Unterminated string");
            return;
        }

        advance();

        /// Trim the surrounding qoutes.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private boolean match(char expected){
        if(isAtEnd()) return false;
        if(source.charAt(current)!= expected) return false;

        current++;
        return true;
    }

    private char peek(){
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isAtEnd(){
        return current >= source.length();
    }

    /// Consumes next string in the source file and returns it
    private char advance(){
        return source.charAt(current++);
    }

    /// Grabs the text of the current lexeme and creates a token for it.
    private void addToken(TokenType type){
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

}
