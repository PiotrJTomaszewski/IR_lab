import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
ANSWER: Review describes the film so above average number of adjectives and adverbs was fairly expected, same goes with nouns as thing which
is described has to be stated. Percentage of verbs is pretty close to reference 15% value.
 */

public class MovieReviewStatictics
{
    private static final String DOCUMENTS_PATH = "movies/";
    private int _verbCount = 0;
    private int _nounCount = 0;
    private int _adjectiveCount = 0;
    private int _adverbCount = 0;
    private int _totalTokensCount = 0;

    public static String TOKENIZER_MODEL = "models/en-token.bin";
    public static String SENTENCE_MODEL = "models/en-sent.bin";
    public static String POS_MODEL = "models/en-pos-maxent.bin";
    public static String LEMMATIZER_DICT = "models/en-lemmatizer.dict";
    public static String NAME_MODEL = "models/en-ner-person.bin";
    public static String LOCATION_MODEL = "models/en-ner-location.bin";
    public static String ORGANISATION_MODEL = "models/en-ner-organization.bin";

    private PrintStream _statisticsWriter;

    private SentenceModel _sentenceModel;
    private TokenizerModel _tokenizerModel;
    private DictionaryLemmatizer _lemmatizer;
    private PorterStemmer _stemmer;
    private POSModel _posModel;
    private TokenNameFinderModel _peopleModel;
    private TokenNameFinderModel _placesModel;
    private TokenNameFinderModel _organizationsModel;

    public static void main(String[] args)
    {
        MovieReviewStatictics statictics = new MovieReviewStatictics();
        statictics.run();
    }

    private void run()
    {
        try
        {
            initModelsStemmerLemmatizer();

            File dir = new File(DOCUMENTS_PATH);
            File[] reviews = dir.listFiles((d, name) -> name.endsWith(".txt"));

            _statisticsWriter = new PrintStream("statistics.txt", "UTF-8");

            Arrays.sort(reviews, Comparator.comparing(File::getName));
            for (File file : reviews)
            {
                System.out.println("Movie: " + file.getName().replace(".txt", ""));
                _statisticsWriter.println("Movie: " + file.getName().replace(".txt", ""));

                String text = new String(Files.readAllBytes(file.toPath()));
                processFile(text);

                _statisticsWriter.println();
            }

            overallStatistics();
            _statisticsWriter.close();

        } catch (IOException ex)
        {
            Logger.getLogger(MovieReviewStatictics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initModelsStemmerLemmatizer()
    {
        try
        {
        // TODO: load all OpenNLP models (+Porter stemmer + lemmatizer)
        // from files (use class variables)

            File sentenceModelFile = new File(SENTENCE_MODEL);
            _sentenceModel = new SentenceModel(sentenceModelFile);
            File tokenModelFile = new File(TOKENIZER_MODEL);
            _tokenizerModel = new TokenizerModel(tokenModelFile);
            File posModelFile = new File(POS_MODEL);
            _posModel = new POSModel(posModelFile);
            _stemmer = new PorterStemmer();
            File lemmatizerDictFile = new File(LEMMATIZER_DICT);
            _lemmatizer = new DictionaryLemmatizer(lemmatizerDictFile);
            File nameModelFile = new File(NAME_MODEL);
            _peopleModel = new TokenNameFinderModel(nameModelFile);
            File locationModelFile = new File(LOCATION_MODEL);
            _placesModel = new TokenNameFinderModel(locationModelFile);
            File organisationModelFile = new File(ORGANISATION_MODEL);
            _organizationsModel = new TokenNameFinderModel(organisationModelFile);

        } catch (IOException ex)
        {
            Logger.getLogger(MovieReviewStatictics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFile(String text)
    {
        // TODO: process the text to find the following statistics:
        // For each movie derive:
        //    - number of sentences
        int noSentences = 0;
        //    - number of tokens
        int noTokens = 0;
        //    - number of (unique) stemmed forms
        int noStemmed = 0;
        //    - number of (unique) words from a dictionary (lemmatization)
        int noWords = 0;
        //    -  people
        Span people[] = new Span[] { };
        //    - locations
        Span locations[] = new Span[] { };
        //    - organisations
        Span organisations[] = new Span[] { };

        // TODO + compute the following overall (for all movies) POS tagging statistics:
        //    - percentage number of adverbs (class variable, private int _verbCount = 0)
        //    - percentage number of adjectives (class variable, private int _nounCount = 0)
        //    - percentage number of verbs (class variable, private int _adjectiveCount = 0)
        //    - percentage number of nouns (class variable, private int _adverbCount = 0)
        //    + update _totalTokensCount

        // ------------------------------------------------------------------

        // TODO derive sentences (update noSentences variable)
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(_sentenceModel);
        String[] sentences = sentenceDetector.sentDetect(text);
        noSentences = sentences.length;

        // TODO derive tokens and POS tags from text
        // (update noTokens and _totalTokensCount)
        TokenizerME tokenDetector = new TokenizerME(_tokenizerModel);
        String[] tokens = tokenDetector.tokenize(text);
        noTokens = tokens.length;
        _totalTokensCount += noTokens;
        POSTaggerME posDetector = new POSTaggerME(_posModel);
        String[] tags = posDetector.tag(tokens);


        // TODO perform stemming (use derived tokens)
        // (update noStemmed)
        Set <String> stems = new HashSet<>();

        for (String token : tokens)
        {
           stems.add(_stemmer.stem(token.toLowerCase().replaceAll("[^a-z0-9]", ""))); //thereafter, ignore "" tokens
        }
        stems.remove("");
        noStemmed = stems.size();


        // TODO perform lemmatization (use derived tokens)
        // (remove "O" from results - non-dictionary forms, update noWords)
        Set<String> baseForms = new HashSet<>(Arrays.asList(_lemmatizer.lemmatize(tokens, tags)));
        baseForms.remove("O");
        noWords = baseForms.size();

        // TODO derive people, locations, organisations (use tokens),
        // (update people, locations, organisations lists).
        NameFinderME peopleFinder = new NameFinderME(_peopleModel);
        NameFinderME orgFinder = new NameFinderME(_organizationsModel);
        NameFinderME placeFinder = new NameFinderME(_placesModel);
        people = peopleFinder.find(tokens);
        organisations = orgFinder.find(tokens);
        locations = placeFinder.find(tokens);

        // TODO update overall statistics - use tags and check first letters
        // (see https://www.clips.uantwerpen.be/pages/mbsp-tags; first letter = "V" = verb?)

        // ------------------------------------------------------------------

        for (int i = 0; i < tags.length; i++) {
            if (tags[i].startsWith("R")) _adverbCount++;
            else if (tags[i].startsWith("J")) _adjectiveCount++;
            else if (tags[i].startsWith("N")) _nounCount++;
            else if (tags[i].startsWith("M") || tags[i].startsWith("V")) _verbCount++;
        }

        saveResults("Sentences", noSentences);
        saveResults("Tokens", noTokens);
        saveResults("Stemmed forms (unique)", noStemmed);
        saveResults("Words from a dictionary (unique)", noWords);

        saveNamedEntities("People", people, tokens);
        saveNamedEntities("Locations", locations, tokens);
        saveNamedEntities("Organizations", organisations, tokens);
    }


    private void saveResults(String feature, int count)
    {
        String s = feature + ": " + count;
        System.out.println("   " + s);
        _statisticsWriter.println(s);
    }

    private void saveNamedEntities(String entityType, Span spans[], String tokens[])
    {
        StringBuilder s = new StringBuilder(entityType + ": ");
        for (int sp = 0; sp < spans.length; sp++)
        {
            for (int i = spans[sp].getStart(); i < spans[sp].getEnd(); i++)
            {
                s.append(tokens[i]);
                if (i < spans[sp].getEnd() - 1) s.append(" ");
            }
            if (sp < spans.length - 1) s.append(", ");
        }

        System.out.println("   " + s);
        _statisticsWriter.println(s);
    }

    private void overallStatistics()
    {
        _statisticsWriter.println("---------OVERALL STATISTICS----------");
        DecimalFormat f = new DecimalFormat("#0.00");

        if (_totalTokensCount == 0) _totalTokensCount = 1;
        String verbs = f.format(((double) _verbCount * 100) / _totalTokensCount);
        String nouns = f.format(((double) _nounCount * 100) / _totalTokensCount);
        String adjectives = f.format(((double) _adjectiveCount * 100) / _totalTokensCount);
        String adverbs = f.format(((double) _adverbCount * 100) / _totalTokensCount);

        _statisticsWriter.println("Verbs: " + verbs + "%");
        _statisticsWriter.println("Nouns: " + nouns + "%");
        _statisticsWriter.println("Adjectives: " + adjectives + "%");
        _statisticsWriter.println("Adverbs: " + adverbs + "%");

        System.out.println("---------OVERALL STATISTICS----------");
        System.out.println("Adverbs: " + adverbs + "%");
        System.out.println("Adjectives: " + adjectives + "%");
        System.out.println("Verbs: " + verbs + "%");
        System.out.println("Nouns: " + nouns + "%");
    }

}
