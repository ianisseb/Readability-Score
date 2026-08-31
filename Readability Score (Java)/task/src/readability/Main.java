package readability;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

public class Main {
    private final Scanner sc = new Scanner(System.in);
    private int totalWords = 0;
    private int sentenceCount = 0;
    private int charactersCount = 0;
    private int syllables = 0;
    private int polysyllables = 0;


    public static void main(String[] args) {
        Main ready = new Main();
        ready.run(args[0]);
    }

    public void run(String args) {

        File file = new File(args);

        System.out.println("The text is:");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                System.out.println(line);
                charactersCount += line.replaceAll("\\s", "").length();
                String[] sentences = line.split("[.!?]");
                for (String sentence : sentences) {
                    sentence = sentence.trim();
                    if (sentence.isEmpty()) {
                        continue;
                    }
                    sentenceCount++;
                    String[] words = sentence.split("\\s+");
                    totalWords += words.length;

                    for (String word : words) {
                        syllables += countSyllables(word);
                        if (isPolysyllable(word)) {
                            polysyllables++;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No file found: in.txt");
        }

        System.out.println("Words: " + totalWords);
        System.out.println("Sentences: " + sentenceCount);
        System.out.println("Characters: " + charactersCount);
        System.out.println("Syllables: " + syllables);
        System.out.println("Polysyllables: " + polysyllables);
        options();
    }

    public static String getAge(int score) {
        String[] ages = {
                "",       // index 0 (unused)
                "5-6",    // 1
                "6-7",    // 2
                "7-8",    // 3
                "8-9",    // 4
                "9-10",   // 5
                "10-11",  // 6
                "11-12",  // 7
                "12-13",  // 8
                "13-14",  // 9
                "14-15",  // 10
                "15-16",  // 11
                "16-17",  // 12
                "17-18",  // 13
                "18-22"   // 14
        };

        if (score >= 1 && score <= 14) {
            return ages[score];
        }
        return score > 14 ? "18-22" : "Unknown";
    }

    public static int countSyllables(String word) {
        word = word.toLowerCase().replaceAll("[^a-z0-9]", ""); // keep word chars or clean punctuation
        if (word.isEmpty()) {
            return 1;
        }

        String letters = word.replaceAll("[^a-z]", "");
        if (letters.isEmpty()) {
            return 1;
        }

        Matcher matcher = Pattern.compile("[aeiouy]+").matcher(letters);
        int count = 0;
        while (matcher.find()) {
            count++;
        }

        if (letters.endsWith("e")) {
            count--;
        }

        return Math.max(1, count);
    }

    public static boolean isPolysyllable(String word) {
        return countSyllables(word) > 2;
    }

    public void options() {
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        double ariScore = 4.71 * ((double) charactersCount / totalWords)
                + 0.5 * ((double) totalWords / sentenceCount) - 21.43;
        double fkScore = 0.39 * ((double) totalWords / sentenceCount) + 11.8 * ((double) syllables / totalWords) - 15.59;
        double smogScore = 1.043 * Math.sqrt(polysyllables * (30.0 / sentenceCount)) + 3.1291;
        double clScore = 0.0588 * ((double) charactersCount / totalWords * 100) - 0.296 * ((double) sentenceCount / totalWords * 100) - 15.8;

        String ariAge = getAge((int) Math.round(ariScore));
        String fkAge = getAge((int) Math.round(fkScore));
        String smogAge = getAge((int) Math.round(smogScore));
        String clAge = getAge((int) Math.round(clScore));

        String option = sc.nextLine();

        switch (option.toUpperCase()) {
            case "ARI":
                System.out.printf("Automated Readability Index: %.2f (about %s-year-olds).%n", ariScore, ariAge);
                break;
            case "FK":
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s-year-olds).%n", fkScore, fkAge);
                break;
            case "SMOG":
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s-year-olds).%n", smogScore, smogAge);
                break;
            case "CL":
                System.out.printf("Coleman–Liau index: %.2f (about %s-year-olds).%n", clScore, clAge);
                break;
            case "ALL":
                System.out.printf("Automated Readability Index: %.2f (about %s-year-olds).%n", ariScore, ariAge);
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s-year-olds).%n", fkScore, fkAge);
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s-year-olds).%n", smogScore, smogAge);
                System.out.printf("Coleman–Liau index: %.2f (about %s-year-olds).%n", clScore, clAge);

                double totalAge = parseAge(ariAge)
                        + parseAge(fkAge)
                        + parseAge(smogAge)
                        + parseAge(clAge);
                double avgAge = totalAge / 4.0;

                System.out.println();
                System.out.printf("This text should be understood in average by %.2f-year-olds.%n", avgAge);
                break;
            default:
                break;
        }
    }

    private static double parseAge(String ageStr) {
        if (ageStr.contains("-")) {
            return Double.parseDouble(ageStr.split("-")[1]) + 1;
        }
        return Double.parseDouble(ageStr.replaceAll("[^0-9]", ""));
    }
}
