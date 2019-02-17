package bg.sofia.uni.fmi.mjt.grep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Grep {

	private boolean isWholeWord;
	private boolean insensitiveCase;

	private String stringToFind;
	private String pathToDirTree;
	private String pathToOutputFile;
	private int numOfParalelThreads;

	private static ExecutorService pool;
	private static int counterRows;

	boolean doesRowContainWord(String row, String word) {
		String[] rowWords = row.split(" ");
		if (isWholeWord == true && insensitiveCase == true) {
			for (String w : rowWords) {
				if (w.toLowerCase().matches("\\b" + word.toLowerCase() + "\\b")) {
					return true;
				}
			}
		}

		if (isWholeWord == true && insensitiveCase == false) {
			for (String w : rowWords) {
				if (w.matches("\\b" + word + "\\b")) {
					return true;
				}
			}
		}

		if (isWholeWord == false && insensitiveCase == true) {
			for (String w : rowWords) {
				if (w.toLowerCase().contains(word.toLowerCase())) {
					return true;
				}
			}
		}

		if (isWholeWord == false && insensitiveCase == false) {
			if (row.contains(word)) {
				return true;
			}
		}
		return false;
	}

	void printFoundLine(File file, String row) throws InterruptedException, ExecutionException {
		System.out.println(
				pool.submit(() -> file.getParentFile().getName() + "/" + file.getName() + ":" + counterRows + ":" + row)
						.get());
	}

	void writeFoundLine(File file, String row) throws IOException {
		File outputFile = new File(pathToOutputFile);
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		bw.write(file.getParentFile().getName() + "/" + file.getName() + ":" + counterRows + ":" + row
				+ System.lineSeparator());
		bw.close();
	}

	void traverseFile(File file, String word) throws InterruptedException, ExecutionException, FileNotFoundException {
		counterRows = 1;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			br.lines().forEach(row -> {
				if (doesRowContainWord(row, word)) {
					try {
						if (pathToOutputFile == null) {
							printFoundLine(file, row);
						} else {
							writeFoundLine(file, row);
						}
					} catch (InterruptedException | ExecutionException | IOException e) {
						System.out.println("Exception when traversing file found.");
					}
				}
				counterRows++;
			});
		} catch (IOException e) {
			System.out.println("IOException when traversing file found.");
		}
	}

	void traverseFileSystemTree(File[] files, String word)
			throws IOException, InterruptedException, ExecutionException {
		if (files != null) {
			for (File file : files) {
				if (file.exists()) {
					if (file.isDirectory() && file.canExecute()) {
						traverseFileSystemTree(file.listFiles(), word);

					} else if (file.isFile()
							&& Files.isReadable(FileSystems.getDefault().getPath(file.getAbsolutePath()))) {
						traverseFile(file, word);
					}
				}
			}
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	// final numbers for splittedCommand array indexes and switch cases
	static final int ONE = 1;
	static final int TWO = 2;
	static final int THREE = 3;
	static final int FOUR = 4;
	static final int FIVE = 5;
	static final int SIX = 6;

	void splitCommand(String command) {
		String[] splittedCommand = command.split(" ");
		int size = splittedCommand.length;

		if (command.startsWith("grep")) {
			switch (size) {
			case FOUR: {
				stringToFind = splittedCommand[ONE];
				pathToDirTree = splittedCommand[TWO];
				numOfParalelThreads = Integer.parseInt(splittedCommand[THREE]);
				break;
			}
			case FIVE: {
				if (isInteger(splittedCommand[FOUR])) {
					if (splittedCommand[ONE].equals("w")) {
						isWholeWord = true;
					} else if (splittedCommand[ONE].equals("i")) {
						insensitiveCase = true;
					} else if (splittedCommand[ONE].equals("w|i")) {
						isWholeWord = true;
						insensitiveCase = true;
					}
					stringToFind = splittedCommand[TWO];
					pathToDirTree = splittedCommand[THREE];
					numOfParalelThreads = Integer.parseInt(splittedCommand[FOUR]);
				} else {
					stringToFind = splittedCommand[ONE];
					pathToDirTree = splittedCommand[TWO];
					numOfParalelThreads = Integer.parseInt(splittedCommand[THREE]);
					pathToOutputFile = splittedCommand[FOUR];
				}
				break;
			}
			case SIX: {
				if (splittedCommand[ONE].equals("w")) {
					isWholeWord = true;
				} else if (splittedCommand.equals("i")) {
					insensitiveCase = true;
				} else if (splittedCommand.equals("w|i")) {
					isWholeWord = true;
					insensitiveCase = true;
				}
				stringToFind = splittedCommand[TWO];
				pathToDirTree = splittedCommand[THREE];
				numOfParalelThreads = Integer.parseInt(splittedCommand[FOUR]);
				pathToOutputFile = splittedCommand[FIVE];
				break;
			}
			default: {
				System.out.println("Incorrect command!");
			}
				break;
			}
		}
	}

	static void executeCommand() throws IOException {
		System.out.println("Enter grep command or exit to stop.");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(System.in));

			String command = "";
			do {
				Grep m = new Grep();
				command = in.readLine();

				if (command.startsWith("grep")) {
					m.traverseExecution(command);
				} else {
					command = "exit";
					if (pool != null) {
						pool.shutdown();
					}
					break;
				}
			} while (!command.equals("exit"));
		} finally {
			in.close();
		}
	}

	void traverseExecution(String command) throws IOException {
		splitCommand(command);
		pool = Executors.newFixedThreadPool(numOfParalelThreads);

		File folder = new File(pathToDirTree);
		File[] files = folder.listFiles();

		try {
			traverseFileSystemTree(files, stringToFind);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		executeCommand();
	}
}