package duke;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;


/**
 * The parser for Duke.
 */
public class Parser {

    /**
     * Returns a string array from the parsed user input string.
     * @param rawInput The user input.
     * @return String array from the parsed input [COMMAND, arguments...].
     * @throws DukeException An invalid user input will produce this exception.
     */
    public String[] parseInput(String rawInput) throws DukeException {
        String[] inputs = rawInput.split("\\s+");
        if (inputs.length < 1) {
            throw new DukeException(DukeException.Errors.INVALID_COMMAND.toString());
        }
        Constant.Command command;
        String commandStr = inputs[0].toUpperCase();
        try {
            command = Constant.Command.valueOf(commandStr);
        } catch (Exception e) {
            throw new DukeException(DukeException.Errors.INVALID_COMMAND.toString());
        }
        switch (command) {
        case LIST:
            if (inputs.length != 1) {
                throw new DukeException(DukeException.Errors.INVALID_ARGUMENT.toString()
                        + " `list` command has no arguments");
            }
            return new String[] {commandStr};

        case DONE:
            if (inputs.length != 2) {
                throw new DukeException(DukeException.Errors.INVALID_ARGUMENT.toString()
                        + " (example: 'done 5')");
            }
            try {
                // Checks if the argument is a number.
                Integer.parseInt(inputs[1]);
            } catch (Exception e) {
                throw new DukeException(DukeException.Errors.WRONG_ARGUMENT_TYPE.toString()
                        + " (example: 'done 5')");
            }
            return new String[] {commandStr, inputs[1]};

        case TODO:
            if (inputs.length < 2) {
                throw new DukeException(DukeException.Errors.MISSING_DESCRIPTION.toString()
                        + " (example: 'todo watch Borat')");
            }
            String description = combineStringArray(inputs, 1, inputs.length);
            return new String[] {commandStr, description};

        case DEADLINE:
            if (inputs.length < 2) {
                throw new DukeException(DukeException.Errors.MISSING_DESCRIPTION.toString() + " (example: 'deadline watch Borat /by 2021-08-21 18:00')");
            }
            String argument = combineStringArray(inputs, 1, inputs.length);
            String[] arguments = argument.split(" /by ");
            if (arguments.length < 2) {
                throw new DukeException(DukeException.Errors.MISSING_DATE.toString()
                        + " (example: 'deadline watch Borat /by 2021-08-21 18:00')");
            } else if (arguments.length > 2) {
                throw new DukeException(DukeException.Errors.INVALID_DATE.toString()
                        + " (example: 'deadline watch Borat /by 2021-08-21 18:00')");
            }
            String date = parseDate(arguments[1]);
            return new String[] {commandStr, arguments[0], date};

        case EVENT:
            if (inputs.length < 2) {
                throw new DukeException(DukeException.Errors.MISSING_DESCRIPTION.toString()
                        + " (example: 'event Borat concert /at 2021-08-21 18:00')");
            }
            String arg = combineStringArray(inputs, 1, inputs.length);
            String[] args = arg.split(" /at ");
            if (args.length < 2) {
                throw new DukeException(DukeException.Errors.MISSING_DATE.toString()
                        + " (example: 'event watch Borat /at 2021-08-21 18:00')");
            } else if (args.length > 2) {
                throw new DukeException(DukeException.Errors.INVALID_DATE.toString()
                        + " (example: 'event watch Borat /at 2021-08-21 18:00')");
            }
            String dateTest = parseDate(args[1]);
            return new String[] {commandStr, args[0], dateTest};

        case BYE:
            if (inputs.length != 1) {
                throw new DukeException(DukeException.Errors.INVALID_ARGUMENT.toString()
                        + " `bye` command has no arguments");
            }
            return new String[] {commandStr};

        case DELETE:
            if (inputs.length != 2) {
                throw new DukeException(DukeException.Errors.INVALID_ARGUMENT.toString()
                        + " (example: 'delete 5')");
            }
            try {
                // Check if the argument is a number
                Integer.parseInt(inputs[1]);
            } catch (Exception e) {
                throw new DukeException(DukeException.Errors.WRONG_ARGUMENT_TYPE.toString()
                        + " (example: 'delete 5')");
            }
            return new String[] {commandStr, inputs[1]};

        case HELP:
            if (inputs.length != 1) {
                throw new DukeException(DukeException.Errors.INVALID_ARGUMENT.toString()
                        + " `help` command has no arguments");
            }
            return new String[] {commandStr};

        case DATES:
            if (inputs.length != 1) {
                throw new DukeException(DukeException.Errors.INVALID_ARGUMENT.toString()
                        + " `dates` command has no arguments");
            }
            return new String[] {commandStr};

        case FIND:
            if (inputs.length < 2) {
                throw new DukeException(DukeException.Errors.MISSING_DESCRIPTION.toString()
                        + " (example: 'find book')");
            }
            String keyword = combineStringArray(inputs, 1, inputs.length);
            return new String[] {commandStr, keyword};

        default:
            // Invalid command
            throw new DukeException(DukeException.Errors.INVALID_ARGUMENT.toString());
        }
    }

    /**
     * Returns an integer from a number in string form.
     * @param number A number in string form.
     * @return An integer form of the given string.
     * @throws DukeException when the given argument is not a number.
     */
    public int convertToInt(String number) throws DukeException {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            throw new DukeException(DukeException.Errors.INVALID_ARGUMENT.toString());
        }
    }

    /**
     * Returns a valid date as a string from a raw date string.
     * @param input The raw date string.
     * @return A string valid as a date.
     * @throws DukeException Thrown if the input is an invalid date.
     */
    private String parseDate(String input) throws DukeException {
        String[] dateTime = input.split("\\s+");
        int len = dateTime.length;
        String formatPattern = "yyyy-MM-dd";
        if (len < 1 || len > 2) {
            throw new DukeException(DukeException.Errors.INVALID_DATE.toString());
        }
        String result = "";
        if (dateTime[0].equals("today")) {
            LocalDate date = LocalDate.now();
            result += date.format(DateTimeFormatter.ofPattern(formatPattern));
        } else if (dateTime[0].equals("tomorrow")) {
            LocalDate date = LocalDate.now().plusDays(1);
            result += date.format(DateTimeFormatter.ofPattern(formatPattern));
        } else {
            // Date
            String[] date1 = dateTime[0].split("-");
            String[] date2 = dateTime[0].split("/");
            if (date1.length == 3 || date2.length == 3) {
                result = date1.length == 3 ? stringToDate(date1) : stringToDate(date2);
            } else {
                throw new DukeException(DukeException.Errors.INVALID_DATE.toString());
            }
        }

        // Parse given time.
        if (dateTime.length == 2) {
            result += "T" + stringToTime(dateTime[1]);
        } else {
            result += "T23:59";
        }

        // Test for validity
        try {
            LocalDateTime.parse(result);
        } catch (DateTimeParseException e) {
            throw new DukeException(DukeException.Errors.INVALID_DATE.toString());
        }
        return result;
    }


    /**
     * Returns a valid time as a string.
     * @param time The time in the form of a string
     * @return The string representation of the time
     * @throws DukeException Thrown if the time is invalid
     */
    private String stringToTime(String time) throws DukeException {
        String[] splitTime = time.split(":");
        if (splitTime.length > 2 || splitTime.length < 1) {
            throw new DukeException(DukeException.Errors.INVALID_TIME.toString());
        }
        for (String s : splitTime) {
            try {
                // Check if all the string are numbers:
                Integer.parseInt(s);
            } catch (Exception e) {
                System.out.println("Time is not a number");
                throw new DukeException(DukeException.Errors.INVALID_TIME.toString());
            }
        }
        if (splitTime.length == 2) {
            // in the form of [hh, mm]
            if (
                    (splitTime[0].length() == 2 || splitTime[0].length() == 1) &&
                            (splitTime[1].length() == 2)
            ) {
                String hh = String.format("%02d", Integer.parseInt(splitTime[0]));
                String mm = String.format("%02d", Integer.parseInt(splitTime[1]));
                return hh + ":" + mm;
            }
        } else {
            // in the form of [hhmm]
            if (
                    (splitTime[0].length() == 3 || splitTime[0].length() == 4)
            ) {
                String hh = splitTime[0].length() == 3 ? splitTime[0].substring(0, 1) : splitTime[0].substring(0, 2);
                String mm = splitTime[0].length() == 3 ? splitTime[0].substring(1, 3) : splitTime[0].substring(2, 4);
                hh = String.format("%02d", Integer.parseInt(hh));
                mm = String.format("%02d", Integer.parseInt(mm));
                return hh + ":" + mm;
            }
        }
        throw new DukeException(DukeException.Errors.INVALID_TIME.toString());
    }

    /**
     * Returns a valid date string from a date array.
     * @param date A String array e.g. [yyyy, mm, dd].
     * @return Null if invalid, else a string representation of the date -> yyyy-mm-dd.
     * @throws DukeException An invalid date will produce this
     */
    private String stringToDate(String[] date) throws DukeException{
        // can be [yyyy, mm, dd] or [dd, mm, yyyy]
        try {
            // Check if all the string are numbers:
            Integer.parseInt(date[0]);
            Integer.parseInt(date[1]);
            Integer.parseInt(date[2]);
        } catch (Exception e) {
            throw new DukeException(DukeException.Errors.INVALID_DATE.toString() + " Date is not a number.");
        }

        if (
                date[0].length() == 4 &&
                        (date[1].length() == 1 || date[1].length() == 2) &&
                        (date[2].length() == 1 || date[2].length() == 2)
        ) {
            // In the form of [yyyy, mm, dd]
            String year = date[0];
            String month = String.format("%02d", Integer.parseInt(date[1]));
            String day = String.format("%02d", Integer.parseInt(date[2]));
            return year + "-" + month + "-" + day;
        } else if (
                (date[0].length() == 1 || date[0].length() == 2) &&
                        (date[1].length() == 1 || date[1].length() == 2) &&
                        (date[2].length() == 4)
        ) {
            // In the form of [dd, mm, yyyy]
            String year = date[2];
            String month = String.format("%02d", Integer.parseInt(date[1]));
            String day = String.format("%02d", Integer.parseInt(date[0]));
            return year + "-" + month + "-" + day;
        }
        throw new DukeException(DukeException.Errors.INVALID_DATE.toString());
    }


    /**
     * Returns a string by combining an array of strings into a space separated sentence.
     * @param arr The string array.
     * @param start The starting index to be combined (inclusive).
     * @param exclusiveEnd The ending index (exclusive).
     * @return The sentence.
     */
    private String combineStringArray(String[] arr, int start, int exclusiveEnd) {
        StringBuilder tmp = new StringBuilder();
        if (exclusiveEnd > arr.length) {
            exclusiveEnd = arr.length;
        }
        for (int i = start; i < exclusiveEnd; ++i) {
            if (i + 1 >= exclusiveEnd) {
                tmp.append(arr[i]);
            } else {
                tmp.append(arr[i]).append(" ");
            }
        }
        return tmp.toString();
    }

}
