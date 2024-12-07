import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    private final SeparateChainingHashST<String, Integer> chainTableOld;
    private final SeparateChainingHashST<String, Integer> chainTableNew;
    private final LinearProbingHashST<String, Integer> probeTableOld;
    private final LinearProbingHashST<String, Integer> probeTableNew;

    private static final int CHAIN_SIZE = 1000;
    private static final int PROBE_SIZE = 20000;

    public Main() {
        chainTableOld = new SeparateChainingHashST<>(CHAIN_SIZE);
        chainTableNew = new SeparateChainingHashST<>(CHAIN_SIZE);
        probeTableOld = new LinearProbingHashST<>(PROBE_SIZE);
        probeTableNew = new LinearProbingHashST<>(PROBE_SIZE);
    }

    private void loadDictionary(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/wordlist.10000.txt"))) {
            String word;
            int lineNumber = 1;
            while ((word = reader.readLine()) != null) {
                chainTableOld.put(word, lineNumber);
                chainTableNew.put(word, lineNumber);
                probeTableOld.put(word, lineNumber);
                probeTableNew.put(word, lineNumber);
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Error reading dictionary file: " + e.getMessage());
        }
    }

    public void checkPassword(String password) {
        System.out.println("\nChecking password: " + password);

        if (password.length() < 8) {
            System.out.println("Result: WEAK - Less than 8 characters");
            return;
        }

        // Reset all comparison counters
        chainTableOld.resetComparisons();
        chainTableNew.resetComparisons();
        probeTableOld.resetComparisons();
        probeTableNew.resetComparisons();

        // Check if password is in dictionary
        boolean inDictChainOld = (chainTableOld.getWithOldHash(password) != null);
        int chainOldComparisons = chainTableOld.getComparisons();

        boolean inDictChainNew = (chainTableNew.get(password) != null);
        int chainNewComparisons = chainTableNew.getComparisons();

        boolean inDictProbeOld = (probeTableOld.getWithOldHash(password) != null);
        int probeOldComparisons = probeTableOld.getComparisons();

        boolean inDictProbeNew = (probeTableNew.get(password) != null);
        int probeNewComparisons = probeTableNew.getComparisons();

        if (inDictChainOld || inDictChainNew || inDictProbeOld || inDictProbeNew) {
            System.out.println("Result: WEAK - Found in dictionary");
        } else {
            // Check if password is word + digit
            String baseWord = password.substring(0, password.length() - 1);
            char lastChar = password.charAt(password.length() - 1);

            if (Character.isDigit(lastChar)) {
                // Reset counters for base word check
                chainTableOld.resetComparisons();
                chainTableNew.resetComparisons();
                probeTableOld.resetComparisons();
                probeTableNew.resetComparisons();

                // Check base word
                boolean baseInDict = chainTableOld.contains(baseWord) ||
                        chainTableNew.contains(baseWord) ||
                        probeTableOld.contains(baseWord) ||
                        probeTableNew.contains(baseWord);

                // Add the comparisons from base word check
                chainOldComparisons += chainTableOld.getComparisons();
                chainNewComparisons += chainTableNew.getComparisons();
                probeOldComparisons += probeTableOld.getComparisons();
                probeNewComparisons += probeTableNew.getComparisons();

                if (baseInDict) {
                    System.out.println("Result: WEAK - Word followed by digit");
                } else {
                    System.out.println("Result: STRONG");
                }
            } else {
                System.out.println("Result: STRONG");
            }
        }

        // Report search costs
        System.out.println("\nSearch costs:");
        System.out.println("Separate Chaining (Old hash): " + chainOldComparisons + " comparisons");
        System.out.println("Separate Chaining (New hash): " + chainNewComparisons + " comparisons");
        System.out.println("Linear Probing (Old hash): " + probeOldComparisons + " comparisons");
        System.out.println("Linear Probing (New hash): " + probeNewComparisons + " comparisons");
    }

    public static void main(String[] args) {
        Main checker = new Main();
        checker.loadDictionary("wordlist.10000");

        String[] testPasswords = {
                "account8",
                "accountability",
                "9a$D#qW7!uX&Lv3zT",
                "B@k45*W!c$Y7#zR9P",
                "X$8vQ!mW#3Dz&Yr4K5"
        };

        for (String password : testPasswords) {
            checker.checkPassword(password);
        }
    }
