public class ChatProtocolParser {

    public enum CommandType {
        NICKNAME, // #nickname
        BAN,      // #ban
        STAT,     // #stat
        MENTION,  // @user
        MESSAGE   // classical message
    }

    public static class ParsedCommand {
        public CommandType type;
        public String value;
        public String message;

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
