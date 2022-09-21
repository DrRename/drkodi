package drrename;

public class Styles {
    static String defaultStyle() {
        // System.err.println("Return default style");
        return null;
    }

    static String filteredStyle() {
        // System.err.println("Return filtered style");
        // return "-fx-background-color: linear-gradient(#E4EAA2, #9CD672);";
        return "-fx-text-fill: #a9a9a9;";
    }

    static String directoryStyle() {
        return "-fx-font-weight: bold;";
    }

    static String externalChangedStyle() {
        // System.err.println("Return filtered style");
        // return "-fx-background-color: linear-gradient(#E4EAA2, #9CD672);";
        return "-fx-text-fill: red;";
    }

    static String changingStyle() {
        // System.err.println("Return filtered style");
        // return "-fx-background-color: linear-gradient(#E4EAA2, #9CD672);";
        return "-fx-background-color: limegreen; -fx-font-weight: bold;";
        // return "-fx-text-fill: #a9a9a9;";
    }

    static String externalChangingStyle() {
        // System.err.println("Return filtered style");
        // return "-fx-background-color: linear-gradient(#E4EAA2, #9CD672);";
        return "-fx-background-color: red; -fx-font-weight: bold;";
        // return "-fx-text-fill: #a9a9a9;";
    }
}
