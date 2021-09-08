package duke;

/**
 * The Duke chat bot app.
 */
public class Duke {

    /** Duke UI */
    private final Ui ui;

    /** Hard disk storage */
    private final Storage storage;

    /** User input parser */
    private final Parser parser;

    /** The list of tasks */
    private TaskList taskList;

    /** True if Duke is still running */
    private boolean isRunning;


    /**
     * Duke class constructor.
     * @param directory Relative path to directory of the saved data.
     * @param file Name of the saved data file.
     */
    public Duke(String directory, String file) {
        ui = new Ui();
        storage = new Storage(directory, file);
        parser = new Parser();
        try {
            taskList = new TaskList(storage.load());
        } catch (DukeException e) {
            ui.showMessage(e.getMessage());
            taskList = new TaskList();
        }
        isRunning = true;
    }

    /**
     * Entry point of the Duke program.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new Duke("./data", "duke.txt").run();
    }


    /**
     * Displays the Duke UI and process user input commands.
     */
    public void run() {

        // Show Greetings.
        ui.showGreetings();

        // Get and process input.
        String rawInput = ui.getInput();
        String output = "Jak się masz? My name-a Borat. I like you.\nWhat I do for you?";
        while (isRunning) {
            // Gets user input
            ui.showMessage(output);
            rawInput = ui.getInput();
            output = getResponse(rawInput);
        }
        // Good bye message
        ui.showGoodBye();
    }

    /**
     * Returns a response by Duke given a user input.
     * @param input User input.
     * @return Duke's response.
     */
    public String getResponse(String input) {
        String output = "";
        try {
            // Parses user input.
            String[] inputs = parser.parseInput(input);
            Constant.Command command = Constant.Command.valueOf(inputs[0]);
            String task;

            // Process user input.
            switch (command) {
            case HELP:
                // Gets user manual.
                output = ui.getHelpMenu();

                break;
            case BYE:
                // Quits program.
                isRunning = false;

                break;
            case LIST:
                // Gets the string represented tasks in the task list.
                output = taskList.getAllTask();

                break;
            case DONE:
                // Marks a task as being completed.
                int index = parser.convertToInt(inputs[1]);
                output = taskList.markDone(index);

                // Edits the file content.
                task = storage.getFileLine(index - 1);
                task = task.substring(0, 4) + "1" + task.substring(5);
                storage.updateLineFile(index - 1, task);

                break;
            case TODO:
                // Adds a todo-typed task to the task list.
                output = taskList.addItem(new Todo(inputs[1]));

                // Add to file content.
                task = "T | 0 | " + inputs[1];
                storage.addToFile(task);

                break;
            case DEADLINE:
                // Adds a deadline-typed task in the task list.
                output = taskList.addItem(new Deadline(inputs[1], inputs[2]));

                // Add to file content.
                task = "D | 0 | " + inputs[1] + " | " + inputs[2];
                storage.addToFile(task);

                break;
            case EVENT:
                // Adds an event-typed task in the task list.
                output = taskList.addItem(new Event(inputs[1], inputs[2]));

                // Add to file content.
                task = "E | 0 | " + inputs[1] + " | " + inputs[2];
                storage.addToFile(task);

                break;
            case DELETE:
                // Deletes a task from the task list.
                int id = parser.convertToInt(inputs[1]);
                output = taskList.removeItem(id);

                // Remove from file content.
                storage.removeFromFile(id - 1);

                break;
            case DATES:
                // Gets the accepted date types.
                output = ui.getAllAcceptedDates();

                break;
            case FIND:
                // Gets the task matching the queried keyword.
                output = taskList.find(inputs[1]);

                break;
            default:
                output = "Invalid Message";
            }

            // Return the output message.
            return output;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Returns true when duke is awake and false otherwise.
     * @return True when duke is awake and false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }
}
