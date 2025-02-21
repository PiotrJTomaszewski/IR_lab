import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.IOException;

public class OpenNLP {

    public static String LANG_DETECT_MODEL = "models/langdetect-183.bin";
    public static String TOKENIZER_MODEL = "models/en-token.bin";
    public static String TOKENIZER_GERMAN_MODEL = "models/de-token.bin";
    public static String SENTENCE_MODEL = "models/en-sent.bin";
    public static String POS_MODEL = "models/en-pos-maxent.bin";
    public static String CHUNKER_MODEL = "models/en-chunker.bin";
    public static String LEMMATIZER_DICT = "models/en-lemmatizer.dict";
    public static String NAME_MODEL = "models/en-ner-person.bin";
    public static String ENTITY_XYZ_MODEL = "models/en-ner-xxx.bin";

	public static void main(String[] args) throws IOException
    {
		OpenNLP openNLP = new OpenNLP();
		openNLP.run();
	}

	public void run() throws IOException
    {

		//languageDetection(); // longer the text bigger the confidence (unless its written in different languages)
		//tokenization(); // C: Outcomes are slightly different because of different number format
		// For German model results were almost the same, only in 2nd text we got 1 more token with german model (41 vs 40)
        //sentenceDetection(); // D/E: Short sentences like "Hi." are being "glued" with next sentence.
		// Output is wrong for sentences with unnecessary question marks and dots, too.
		// F: Obviously unnecessary punctuation is causing more mistakes in algorithm.
		// Algorithm doesn't fall for every "bad" punctuation mark, it properly ignores some of them.
		//posTagging(); // C: there is one mistake in tags, 'like' is considered as preposition in both sentences, when in first one it is a verb.
		//lemmatization(); // lemmatizer seems to work better. Stemmer randomly cuts out last letter(s) of the word, f.e. reduces "provide" to "provid". Probably lemmatizer requires POS-tags to deal better with homonyms like "like" and for overall better performance.
		//stemming(); // When token doesn't exist in dict, lemmatizer returns "O"
		//chunking(); // B-prefix means that token is beginning of the chunk, I-prefix means that token is inside the chunk and not the first part of the chunk.
		// There are 5 chunks. I think obtained results are correct, because they are identical to chunking example on 2nd page of pdf file with information about the task
		nameFinding(); // Results are not fully correct. "Desk Set" isn't a person, and surnames like "Holmstrom" didn't make it into results.
		// xxx model probably seeks for dates
	}

    private void detectAndPrintLanguage(LanguageDetectorME me, String text)
    {
        System.out.println(text);
        Language lang = me.predictLanguage(text);
        System.out.printf("Language: %s Probability: %.10f\n", lang.getLang(), lang.getConfidence());
        System.out.println(); // An empty line for better readability
    }

    private void detectAndPrintLanguages(LanguageDetectorME me, String text)
    {
        System.out.println(text);
        Language[] languages = me.predictLanguages(text);
        for (Language l : languages) {
            System.out.printf("Language: %s Probability: %.10f\n", l.getLang(), l.getConfidence());
        }
        System.out.println(); // An empty line for better readability
    }

    private void languageDetection() throws IOException
    {
        System.out.println("***Language detection***");
        File modelFile = new File(LANG_DETECT_MODEL);
        LanguageDetectorModel model = new LanguageDetectorModel(modelFile);
        LanguageDetectorME me = new LanguageDetectorME(model);

        String text;
        text = "cats";
        detectAndPrintLanguage(me, text);

        text = "cats like milk";
        detectAndPrintLanguage(me, text);

        text = "Many cats like milk because in some ways it reminds them of their mother's milk.";
        detectAndPrintLanguage(me, text);

        text = "The two things are not really related. Many cats like milk because in some ways it reminds them of their mother's milk.";
        detectAndPrintLanguage(me, text);

        text = "The two things are not really related. Many cats like milk because in some ways it reminds them of their mother's milk. "
                + "It is rich in fat and protein. They like the taste. They like the consistency . "
                + "The issue as far as it being bad for them is the fact that cats often have difficulty digesting milk and so it may give them "
                + "digestive upset like diarrhea, bloating and gas. After all, cow's milk is meant for baby calves, not cats. "
                + "It is a fortunate quirk of nature that human digestive systems can also digest cow's milk. But humans and cats are not cows.";
        detectAndPrintLanguages(me, text);

        text = "Many cats like milk because in some ways it reminds them of their mother's milk. Le lait n'est pas forc�ment mauvais pour les chats";
        detectAndPrintLanguages(me, text);

        text = "Many cats like milk because in some ways it reminds them of their mother's milk. Le lait n'est pas forc�ment mauvais pour les chats. "
                + "Der Normalfall ist allerdings der, dass Salonl�wen Milch weder brauchen noch gut verdauen k�nnen.";
        detectAndPrintLanguages(me, text);
    }

    private void tokenizeAndPrint(TokenizerME me, String text)
    {
        String[] tokens = me.tokenize(text);
        double[] probabilities = me.getTokenProbabilities();
        for (int i = 0; i < tokens.length; i++) {
            System.out.printf("TOKEN: %s PROBABILITY: %f\n", tokens[i], probabilities[i]);
        }
        System.out.println(tokens.length);
        System.out.println("==========================================");
    }

    private void tokenizeTexts(File modelFile) throws IOException
    {
        TokenizerModel model = new TokenizerModel(modelFile);
        TokenizerME me = new TokenizerME(model);
        String text;
        text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
                + "but there may have been instances of domestication as early as the Neolithic from around 9500 years ago (7500 BC).";
        tokenizeAndPrint(me, text);

        text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
                + "but there may have been instances of domestication as early as the Neolithic from around 9,500 years ago (7,500 BC).";
        tokenizeAndPrint(me, text);

        text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
                + "but there may have been instances of domestication as early as the Neolithic from around 9 500 years ago ( 7 500 BC).";
        tokenizeAndPrint(me, text);
    }

    private void tokenization() throws IOException
    {
        System.out.println("***Tokenization***");
        tokenizeTexts(new File(TOKENIZER_MODEL));
        System.out.println("*Using German model*");
        tokenizeTexts(new File(TOKENIZER_GERMAN_MODEL));
    }

    private void detectSentenceAndPrint(SentenceDetectorME me, String text)
    {
        String[] sentences = me.sentDetect(text);
        double[] probabilities = me.getSentenceProbabilities();
        for (int i = 0; i < sentences.length; i++) {
            System.out.printf("SENTENCE: %s PROBABILITY: %f\n", sentences[i], probabilities[i]);
        }
        System.out.println("==========================================");
    }

    private void sentenceDetection() throws IOException
    {
        System.out.println("***Sentence detection***");
        String text;
        File modelFile = new File(SENTENCE_MODEL);
        SentenceModel model = new SentenceModel(modelFile);
        SentenceDetectorME me = new SentenceDetectorME(model);

        text = "Hi. How are you? Welcome to OpenNLP. "
                + "We provide multiple built-in methods for Natural Language Processing.";
        detectSentenceAndPrint(me, text);

        text = "Hi. How are you?! Welcome to OpenNLP? "
                + "We provide multiple built-in methods for Natural Language Processing.";
        detectSentenceAndPrint(me, text);

        text = "Hi. How are you? Welcome to OpenNLP.?? "
                + "We provide multiple . built-in methods for Natural Language Processing.";
        detectSentenceAndPrint(me, text);

        text = "The interrobang, also known as the interabang (often represented by ?! or !?), "
                + "is a nonstandard punctuation mark used in various written languages. "
                + "It is intended to combine the functions of the question mark (?), or interrogative point, "
                + "and the exclamation mark (!), or exclamation point, known in the jargon of printers and programmers as a \"bang\". ";
        detectSentenceAndPrint(me, text);

        // added redundant punctuation
        text = "The interrobang, also known as the interabang (often represented by ?! or !?), "
                + "is a nonstandard punctuation mark used in various written?? languages. "
                + "It is intended to combine the... functions of the question. mark (?), or interrogative point, "
                + "and the exclamation mark (!), or exclamation point, known in the jargon!!! of printers and programmers as a \"bang\". ";
        detectSentenceAndPrint(me, text);

    }

    private void posTagAndPrint(POSTaggerME me, String[] sentence)
    {
        String[] tags = me.tag(sentence);
        for (String tag: tags) {
            System.out.println(tag);
        }
        System.out.println();
    }

    private void posTagging() throws IOException
    {
        System.out.println("***Part-of-speech tagging***");
        File modelFile = new File(POS_MODEL);
        POSModel model = new POSModel(modelFile);
        POSTaggerME me = new POSTaggerME(model);

        String[] sentence;

        sentence = new String[]{"Cats", "like", "milk"};
        posTagAndPrint(me, sentence);

        sentence = new String[]{"Cat", "is", "white", "like", "milk"};
        posTagAndPrint(me, sentence);

		/*sentence = new String[] { "Hi", "How", "are", "you", "Welcome", "to", "OpenNLP", "We", "provide", "multiple",
				"built-in", "methods", "for", "Natural", "Language", "Processing" };
		sentence = new String[] { "She", "put", "the", "big", "knives", "on", "the", "table" };*/
    }

    private void lemmatization() throws IOException
    {
        System.out.println("***Lemmatization***");
        File model = new File(LEMMATIZER_DICT);
        DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(model);

        String[] text;
        String[] baseForms;
        text = new String[]{"Hi", "How", "are", "you", "Welcome", "to", "OpenNLP", "We", "provide", "multiple",
                "built-in", "methods", "for", "Natural", "Language", "Processing"};
        String[] tags;
        tags = new String[]{"NNP", "WRB", "VBP", "PRP", "VB", "TO", "VB", "PRP", "VB", "JJ", "JJ", "NNS", "IN", "JJ",
                "NN", "VBG"};
        baseForms = lemmatizer.lemmatize(text, tags);
        for (String s : baseForms) {
            System.out.println(s);
        }
        String[] randomWord = {"GONJAWGI"};
        System.out.println(lemmatizer.lemmatize(randomWord, tags)[0]);
        System.out.println();
    }

    private void stemming()
    {
        System.out.println("***Stemming***");
        PorterStemmer stemmer = new PorterStemmer();
        String[] sentence;
        sentence = new String[]{"Hi", "How", "are", "you", "Welcome", "to", "OpenNLP", "We", "provide", "multiple",
                "built-in", "methods", "for", "Natural", "Language", "Processing"};
        for (String word : sentence) {
            System.out.println(stemmer.stem(word));
        }
        System.out.println(stemmer.stem("Gwnoiafaiwfn"));
    }

    private void chunking() throws IOException
    {
        System.out.println("***Chunking***");
        File modelFile = new File(CHUNKER_MODEL);
		ChunkerModel model = new ChunkerModel(modelFile);
		ChunkerME me = new ChunkerME(model);
		String[] sentence = new String[] { "She", "put", "the", "big", "knives", "on", "the", "table" };

		String[] tags = new String[] { "PRP", "VBD", "DT", "JJ", "NNS", "IN", "DT", "NN" };

		String[] results = me.chunk(sentence, tags);
		for (String s : results) {
			System.out.println(s);
		}
	}

	private void extractEntityAndPrint(File modelFile, String text) throws IOException
    {
        TokenNameFinderModel model = new TokenNameFinderModel(modelFile);
        NameFinderME me = new NameFinderME(model);

        File sentenceModelFile = new File(SENTENCE_MODEL);
        SentenceModel sentenceModel = new SentenceModel(sentenceModelFile);
        SentenceDetectorME sentenceME = new SentenceDetectorME(sentenceModel);
        String[] sentences = sentenceME.sentDetect(text);

        File tokenModelFile = new File(TOKENIZER_GERMAN_MODEL);
        TokenizerModel tokenModel = new TokenizerModel(tokenModelFile);
        TokenizerME tokenMe = new TokenizerME(tokenModel);
        for (String s : sentences) {
            String[] tokens = tokenMe.tokenize(s);
            Span[] spans = me.find(tokens);
            String[] results = Span.spansToStrings(spans, tokens);
            for (String result: results) {
                System.out.println(result);
            }
        }
        System.out.println();
    }

	private void nameFinding() throws IOException
    {
        System.out.println("***Named entity extraction***");
		String text = "he idea of using computers to search for relevant pieces of information was popularized in the article "
				+ "As We May Think by Vannevar Bush in 1945. It would appear that Bush was inspired by patents "
				+ "for a 'statistical machine' - filed by Emanuel Goldberg in the 1920s and '30s - that searched for documents stored on film. "
				+ "The first description of a computer searching for information was described by Holmstrom in 1948, "
				+ "detailing an early mention of the Univac computer. Automated information retrieval systems were introduced in the 1950s: "
				+ "one even featured in the 1957 romantic comedy, Desk Set. In the 1960s, the first large information retrieval research group "
				+ "was formed by Gerard Salton at Cornell. By the 1970s several different retrieval techniques had been shown to perform "
				+ "well on small text corpora such as the Cranfield collection (several thousand documents). Large-scale retrieval systems, "
				+ "such as the Lockheed Dialog system, came into use early in the 1970s.";
		extractEntityAndPrint(new File(NAME_MODEL), text);

		System.out.println("*Using XYZ model*");
        extractEntityAndPrint(new File(ENTITY_XYZ_MODEL), text);

	}

}
