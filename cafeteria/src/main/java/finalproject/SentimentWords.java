package finalproject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SentimentWords {

    final static Set<String> a1 = new HashSet<>(Arrays.asList("awful", "horrible", "terrible", "disgusting", "hate", "unbearable",
            "repulsive", "atrocious", "dreadful", "appalling", "abysmal", "nauseating",
            "revolting", "loathsome", "deplorable", "abominable", "vile", "detestable",
            "unpleasant", "inferior", "miserable", "horrendous", "distasteful", "grotesque",
            "horrific", "foul", "putrid", "vicious", "heinous", "diabolical", "worst", "tasteless", "awful"));

    final static Set<String> a2 = new HashSet<>(Arrays.asList("bad", "poor", "disliked", "unsatisfactory", "subpar", "mediocre", "unpleasant",
            "disappointing", "inferior", "lacking", "unimpressive", "deficient", "lousy",
            "substandard", "unacceptable", "faulty", "flawed", "inadequate", "defective",
            "unappealing", "lamentable", "unfortunate", "second-rate", "shoddy", "mediocre",
            "substandard", "unfulfilling", "regrettable", "lackluster", "passable"));

    final static Set<String> a3 = new HashSet<>(Arrays.asList("average", "okay", "fine", "satisfactory", "indifferent", "moderate", "fair",
            "unremarkable", "tolerable", "middling", "passable", "standard", "acceptable",
            "usual", "ordinary", "plain", "commonplace", "middling", "routine", "regular",
            "so-so", "workable", "decent", "moderate", "reasonable", "mediocre", "average",
            "all right", "standard", "adequate", "can", "be"));

    final static Set<String> a4 = new HashSet<>(Arrays.asList("good", "enjoyable", "pleasant", "satisfying", "nice", "liked", "delightful",
            "pleasing", "admirable", "commendable", "worthy", "gratifying", "pleasurable",
            "appealing", "lovely", "congenial", "agreeable", "charming", "delightful",
            "rewarding", "pleasurable", "favorable", "admirable", "superior", "nice",
            "praiseworthy", "positive", "gratifying", "encouraging", "pleasant"));

    final static Set<String> a5 = new HashSet<>(Arrays.asList("excellent", "fantastic", "amazing", "wonderful", "outstanding", "superb",
            "love", "exceptional", "marvelous", "brilliant", "terrific", "remarkable",
            "phenomenal", "extraordinary", "magnificent", "perfect", "splendid",
            "glorious", "stellar", "exquisite", "superb", "unmatched", "unbeatable",
            "impressive", "stunning", "sensational", "divine", "awesome", "superior",
            "top-notch", "wow"));

    public static Map<Double, Set<String>> sentiments(String feedback) {
        Map<Double, Set<String>> mapping = new HashMap<>();

        // Split feedback into words
        String[] words = feedback.toLowerCase().split("\\s+");

        // Count occurrences in each sentiment category
        int a1Count = countWordsInSet(words, a1);
        int a2Count = countWordsInSet(words, a2);
        int a3Count = countWordsInSet(words, a3);
        int a4Count = countWordsInSet(words, a4);
        int a5Count = countWordsInSet(words, a5);

        // Determine sentiment score based on counts
        int score;
        if (a1Count > a2Count && a1Count > a3Count && a1Count > a4Count && a1Count > a5Count) {
            score = 1;
        } else if (a2Count > a1Count && a2Count > a3Count && a2Count > a4Count && a2Count > a5Count) {
            score = 2;
        } else if (a3Count > a1Count && a3Count > a2Count && a3Count > a4Count && a3Count > a5Count) {
            score = 3;
        } else if (a4Count > a1Count && a4Count > a2Count && a4Count > a3Count && a4Count > a5Count) {
            score = 4;
        } else if (a5Count > a1Count && a5Count > a2Count && a5Count > a3Count && a5Count > a4Count) {
            score = 5;
        } else {
            score = 3;
        }

        double finalScore = convertScoreToPercent(score);
        Set<String> sentimentWords = extractSentimentWords(words, score);
        mapping.put(finalScore, sentimentWords);

        return mapping;
    }

    private static int countWordsInSet(String[] words, Set<String> wordSet) {
        return (int) Arrays.stream(words).filter(wordSet::contains).count();
    }

    private static Set<String> extractSentimentWords(String[] words, int score) {
        Set<String> sentimentWords = new HashSet<>();

        switch (score) {
            case 1 ->
                sentimentWords.addAll(Arrays.stream(words)
                        .filter(a1::contains)
                        .collect(Collectors.toSet()));
            case 2 ->
                sentimentWords.addAll(Arrays.stream(words)
                        .filter(a2::contains)
                        .collect(Collectors.toSet()));
            case 3 ->
                sentimentWords.addAll(Arrays.stream(words)
                        .filter(a3::contains)
                        .collect(Collectors.toSet()));
            case 4 ->
                sentimentWords.addAll(Arrays.stream(words)
                        .filter(a4::contains)
                        .collect(Collectors.toSet()));
            case 5 ->
                sentimentWords.addAll(Arrays.stream(words)
                        .filter(a5::contains)
                        .collect(Collectors.toSet()));
            default -> {
            }
        }
        if (sentimentWords.isEmpty()) {
            Set<String> defaultValue = new HashSet<>();
            defaultValue.add("Neutral");
            return defaultValue;
        }

        return sentimentWords;
    }

    public static double convertScoreToPercent(int score) {
        return switch (score) {
            case 1 ->
                0.0;
            case 2 ->
                25.0;
            case 3 ->
                50.0;
            case 4 ->
                75.0;
            case 5 ->
                100.0;
            default ->
                50.0;
        };
    }
}
