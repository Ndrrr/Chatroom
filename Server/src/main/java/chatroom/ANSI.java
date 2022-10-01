package chatroom;

public enum ANSI {
    RESET_FORMAT("\u001B[0m"), RESET_CONSOLE("\033[H\033[2J"), BOLD("\033[0;1m");

    enum Colors {
        BRIGHT_RED("\u001B[91m"), BRIGHT_GREEN("\u001B[92m"), BRIGHT_YELLOW("\u001B[93m"),
        BRIGHT_BLUE("\u001B[94m"), BRIGHT_MAGENTA("\u001B[95m"), BRIGHT_CYAN("\u001B[96m"),
        RED("\u001B[31m"), GREEN("\u001B[32m"), YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"), MAGENTA("\u001B[35m"), CYAN("\u001B[36m");
        private final String value;
        Colors(String value){
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    private final String value;
    ANSI(String value){
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
