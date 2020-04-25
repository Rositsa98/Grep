Multithreaded Grep
Each operating system offers string search functionality: such as the console grep command in Unix / Linux. We will create a directory tree search program with text files for a given string.

A directory tree may have:

- subdirectories, at any depth of insertion
- text files
The purpose of the program is to find all occurrences of a character string in the text files in that tree.

For each such appointment, the console or in a specific file (depending on the parameters submitted) should be displayed in the following format:

path: line_number: line_text
where:

path is the relative path of the file to the home directory
line_number is the line number in the file, starting counting from 1
line_text is the contents of the line including the searched string
For the sake of efficiency, the solution must be multi-threaded, ie. several parallel running threads should distribute the work, synchronizing appropriately so that the result is correct.

Let the main class be called Main and it is bg.sofia.uni.fmi.mjt.grep. It will read and execute grep commands from the console. Each grep command has the following form:

grep * [- w | -i] [string_to_find] [path_to_directory_tree] [number_of_parallel_threads] * [path_to_output_file]

-w | -i - optional parameters
-w - instructs the grep command to search for entire words only (ie "hi" will not match-not "hippo", but will match-not "hi there")
-i - instructs the grep command to ignore case sensitivity (ie "hi" is the same as "Hi")
string_to_find - string search
path_to_directory_tree - The path to the root of the directory tree
number_of_parallel_threads - the maximum number of threads running in parallel
path_to_output_file (optional parameter) - path to a file where the output of the grep command execution is written. If this parameter is not specified, then exit the program from the console.
* optional parameters

Notes:

There are various valid ways to parallelize the algorithm, ie. to take advantage of having multiple threads
Depending on the operating system, the type of storage used by the file system (HDD, SSD), etc. you will notice that the number of parallel threads from one moment onwards does not improve the performance and may even slow it down. This is expected and is not a sign that your decision is incorrect.
After performing the grep operation, your java process must complete and release the allocated resources.
Make sure your solutions work correctly under any operating system. That is, make sure that you are not a hard-code-something operating system-specific (such as a path separator).
Examples
grep foo / Users / my-user / git / java-course 2
01-intro-to-java / lab / 02-anagrams / AnagramTest.java: 73: assertFalse (anagram.isAnagram ("food fd"));
06-io-streams / lecture / PITCHME.md: 633: // -> "Unable to open file 'food': No such file or directory"
# [...]

grep -wi foo / Users / my-user / git / java-course 4 /Users/my-user/output.txt
