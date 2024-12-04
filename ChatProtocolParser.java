/**
 * ChatProtocolParser is responsible for parsing chat commands and messages.
 */
public class ChatProtocolParser {

    /**
     * CommandType represents the different types of commands that can be parsed.
     */
    public enum CommandType {
        NICKNAME, // #nickname
        BAN,      // #ban
        STAT,     // #stat
        MENTION,  // @user
        MESSAGE   // classical message
    }

    /**
     * ParsedCommand represents a parsed command with its type, value, and message.
     */
    public static class ParsedCommand {
        public CommandType type;
        public String value;
        public String message;

        /**
         * Constructs a ParsedCommand with the specified type, value, and message.
         *
         * @param type the type of the command
         * @param value the value associated with the command
         * @param message the message associated with the command
         */
        public ParsedCommand(CommandType type, String value, String message) {
            this.type = type;
            this.value = value;
            this.message = message;
        }

        @Override
        public String toString() {
            return "Type: " + type + ", value: '" + value + "'" + ", message: '" + message + "'";
        }
    }

    /**
     * Parses a line of text and returns a ParsedCommand object.
     *
     * @param line the line of text to parse
     * @return a ParsedCommand object representing the parsed command
     */
    public ParsedCommand parseLine(String line) {
        if (line.startsWith("#nickname")) {
            return new ParsedCommand(CommandType.NICKNAME, line.substring(9).trim(), "");
        } else if (line.startsWith("#ban")) {
            return new ParsedCommand(CommandType.BAN, line.substring(4).trim(), "");
        } else if (line.startsWith("#stat")) {
            return new ParsedCommand(CommandType.STAT, "", "");
        } else if (line.startsWith("@")) {
            String[] parts = line.split("\\s+", 2);
            String mentionUser = parts[0].substring(1);
            String message = parts.length > 1 ? parts[1] : "";
            return new ParsedCommand(CommandType.MENTION, mentionUser, message);
        } else {
            return new ParsedCommand(CommandType.MESSAGE, "", line.trim());
        }
    }
}