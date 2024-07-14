package service;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SentimentAnalysis {

    private enum SentimentCategory {
        VERY_NEGATIVE(1, Set.of("awful", "horrible", "terrible", "disgusting", "hate", "unbearable",
                "repulsive", "atrocious", "dreadful", "appalling", "abysmal", "nauseating",
                "revolting", "loathsome", "deplorable", "abominable", "vile", "detestable",
                "unpleasant", "inferior", "miserable", "horrendous", "distasteful", "grotesque",
                "horrific", "foul", "putrid", "vicious", "heinous", "diabolical", "worst", "tasteless")),
        NEGATIVE(2, Set.of("bad", "poor", "disliked", "unsatisfactory", "subpar", "mediocre", "unpleasant",
                "disappointing", "inferior", "lacking", "unimpressive", "deficient", "lousy",
                "substandard", "unacceptable", "faulty", "flawed", "inadequate", "defective",
                "unappealing", "lamentable", "unfortunate", "second-rate", "shoddy", "mediocre",
                "substandard", "unfulfilling", "regrettable", "lackluster", "passable")),
        NEUTRAL(3, Set.of("average", "okay", "fine", "satisfactory", "indifferent", "moderate", "fair",
                "unremarkable", "tolerable", "middling", "passable", "standard", "acceptable",
                "usual", "ordinary", "plain", "commonplace", "middling", "routine", "regular",
                "so-so", "workable", "decent", "moderate", "reasonable", "mediocre", "average",
                "all right", "standard", "adequate", "can", "be")),
        POSITIVE(4, Set.of("good", "enjoyable", "pleasant", "satisfying", "nice", "liked", "delightful",
                "pleasing", "admirable", "commendable", "worthy", "gratifying", "pleasurable",
                "appealing", "lovely", "congenial", "agreeable", "charming", "delightful",
                "rewarding", "pleasurable", "favorable", "admirable", "superior", "nice",
                "praiseworthy", "positive", "gratifying", "encouraging", "pleasant")),
        VERY_POSITIVE(5, Set.of("excellent", "fantastic", "amazing", "wonderful", "outstanding", "superb",
                "love", "exceptional", "marvelous", "brilliant", "terrific", "remarkable",
                "phenomenal", "extraordinary", "magnificent", "perfect", "splendid",
                "glorious", "stellar", "exquisite", "superb", "unmatched", "unbeatable",
                "impressive", "stunning", "sensational", "divine", "awesome", "superior",
                "top-notch", "wow"));

        private final int score;
        private final Set<String> words;

        SentimentCategory(int score, Set<String> words) {
            this.score = score;
            this.words = words;
        }

        public int getScore() {
            return score;
        }

        public Set<String> getWords() {
            return words;
        }
    }

    public static Map<Double, Set<String>> analyzeSentiments(String feedback) {
        Map<Double, Set<String>> sentimentMapping = new HashMap<>();

        // Split feedback into words
        String[] words = feedback.toLowerCase().split("\\s+");

        // Count occurrences in each sentiment category
        Map<SentimentCategory, Integer> sentimentCounts = new EnumMap<>(SentimentCategory.class);
        for (SentimentCategory category : SentimentCategory.values()) {
            sentimentCounts.put(category, countWordsInSet(words, category.getWords()));
        }

        // Determine the sentiment category with the highest count
        SentimentCategory dominantCategory = Collections.max(sentimentCounts.entrySet(), Map.Entry.comparingByValue()).getKey();

        // Convert score to percentage
        double finalScore = convertScoreToPercent(dominantCategory.getScore());

        // Extract sentiment words
        Set<String> sentimentWords = extractSentimentWords(words, dominantCategory);

        sentimentMapping.put(finalScore, sentimentWords);

        return sentimentMapping;
    }

    private static int countWordsInSet(String[] words, Set<String> wordSet) {
        return (int) Arrays.stream(words).filter(wordSet::contains).count();
    }

    private static Set<String> extractSentimentWords(String[] words, SentimentCategory category) {
        Set<String> sentimentWords = Arrays.stream(words)
                .filter(category.getWords()::contains)
                .collect(Collectors.toSet());

        if (sentimentWords.isEmpty()) {
            sentimentWords.add("Neutral");
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
