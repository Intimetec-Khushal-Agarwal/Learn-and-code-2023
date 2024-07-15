package service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SentimentAnalysis {

    private enum SentimentCategory {
        VERY_NEGATIVE(1, Set.of("awful", "horrible", "terrible", "disgusting", "hate", "unbearable",
                "repulsive", "atrocious", "dreadful", "appalling", "abysmal", "nauseating",
                "revolting", "loathsome", "deplorable", "abominable", "vile", "detestable",
                "unpleasant", "miserable", "horrendous", "distasteful", "grotesque",
                "horrific", "foul", "putrid", "vicious", "heinous", "diabolical", "worst", "tasteless",
                "pathetic", "tragic", "depressing", "shocking", "ghastly", "unspeakable",
                "frightful", "offensive", "displeasing")),
        NEGATIVE(2, Set.of("bad", "poor", "disliked", "unsatisfactory", "subpar", "mediocre",
                "disappointing", "inferior", "lacking", "unimpressive", "deficient", "lousy",
                "substandard", "unacceptable", "faulty", "flawed", "inadequate", "defective",
                "unappealing", "lamentable", "unfortunate", "second-rate", "shoddy", "disagreeable",
                "unsavory", "unremarkable", "suboptimal", "dissatisfactory", "unfavorable")),
        NEUTRAL(3, Set.of("average", "okay", "fine", "satisfactory", "indifferent", "moderate", "fair", "tolerable",
                "middling", "passable", "standard", "acceptable",
                "usual", "ordinary", "plain", "commonplace", "routine", "regular",
                "so-so", "workable", "decent", "reasonable", "adequate", "can", "be",
                "neutral", "unexceptional", "alright", "run-of-the-mill")),
        POSITIVE(4, Set.of("good", "enjoyable", "pleasant", "satisfying", "liked", "delightful",
                "pleasing", "admirable", "commendable", "worthy", "pleasurable",
                "appealing", "lovely", "congenial", "agreeable", "charming", "rewarding",
                "favorable", "superior", "nice", "praiseworthy", "positive",
                "gratifying", "encouraging", "content", "happy", "enjoyed", "delicious")),
        VERY_POSITIVE(5, Set.of("excellent", "fantastic", "amazing", "wonderful", "outstanding", "superb",
                "love", "exceptional", "marvelous", "brilliant", "terrific", "remarkable",
                "phenomenal", "magnificent", "perfect", "splendid",
                "glorious", "stellar", "exquisite", "unmatched", "unbeatable",
                "impressive", "stunning", "sensational", "divine", "awesome",
                "top-notch", "wow", "ecstatic", "fabulous", "flawless",
                "unbelievable", "spectacular", "first-rate", "miraculous", "incredible",
                "extraordinary", "out of this world"));

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

        String[] words = feedback.toLowerCase().split("\\s+");

        String[] modifiedWords = handleNegations(words);

        Map<SentimentCategory, Integer> sentimentCounts = new EnumMap<>(SentimentCategory.class);
        Map<SentimentCategory, Set<String>> sentimentWordsMap = new EnumMap<>(SentimentCategory.class);

        for (SentimentCategory category : SentimentCategory.values()) {
            int count = countWordsInSet(modifiedWords, category.getWords());
            sentimentCounts.put(category, count);
            sentimentWordsMap.put(category, extractSentimentWords(modifiedWords, category));
        }

        SentimentCategory dominantCategory = Collections.max(sentimentCounts.entrySet(), Map.Entry.comparingByValue()).getKey();

        // Convert score to percentage
        double finalScore = convertScoreToPercent(dominantCategory.getScore());

        // Extract sentiment words
        Set<String> sentimentWords = sentimentWordsMap.get(dominantCategory);

        sentimentMapping.put(finalScore, sentimentWords);

        return sentimentMapping;
    }

    private static int countWordsInSet(String[] words, Set<String> wordSet) {
        return (int) Arrays.stream(words).filter(wordSet::contains).count();
    }

    private static Set<String> extractSentimentWords(String[] words, SentimentCategory category) {
        return Arrays.stream(words)
                .filter(word -> category.getWords().contains(word))
                .collect(Collectors.toSet());
    }

    private static String[] handleNegations(String[] words) {
        List<String> result = new ArrayList<>();
        Set<String> negations = Set.of("not", "never", "no", "none", "don't", "doesn't", "didn't", "isn't", "wasn't", "aren't", "weren't", "won't", "can't", "couldn't", "shouldn't", "wouldn't");

        for (int i = 0; i < words.length; i++) {
            if (negations.contains(words[i]) && i + 1 < words.length) {
                result.add(words[i] + " " + words[i + 1]);
                i++;
            } else {
                result.add(words[i]);
            }
        }

        return result.toArray(String[]::new);
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
