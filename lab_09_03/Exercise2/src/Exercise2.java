import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.xml.sax.SAXException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.PhoneExtractingContentHandler;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;

public class Exercise2
{

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private OptimaizeLangDetector langDetector;

    public static void main(String[] args)
    {
        Exercise2 exercise = new Exercise2();
        exercise.run();
    }

    private void run()
    {
        try
        {
            if (!new File("./outputDocuments").exists())
            {
                Files.createDirectory(Paths.get("./outputDocuments"));
            }

            initLangDetector();

            File directory = new File("src/documents");
            File[] files = directory.listFiles();
            for (File file : files)
            {
                processFile(file);
            }
        } catch (IOException | SAXException | TikaException | ParseException e)
        {
            e.printStackTrace();
        }

    }

    private void initLangDetector() throws IOException
    {
        // TODO initialize language detector (langDetector)
        langDetector = new OptimaizeLangDetector();
        langDetector.loadModels();
    }

    private void processFile(File file) throws IOException, SAXException, TikaException, ParseException {
        // TODO: extract content, metadata and language from given file
        AutoDetectParser tikaParser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);

        try (InputStream stream = new FileInputStream(file)) {
            tikaParser.parse(stream, handler, metadata);
        }

        String[] metadataNames = metadata.names();

        if (file.getName().equals("test.docx")) {
            for (String s : metadataNames) {
                System.out.println(s);
            }
        }

        Date creationDate;
        Date lastSavedDate;
        try {
            creationDate = new SimpleDateFormat("YYYY-MM-DD").parse(metadata.get("meta:creation-date"));
        } catch (NullPointerException e) {
            creationDate = null;
        }
        try {
            lastSavedDate = new SimpleDateFormat("YYYY-MM-DD").parse(metadata.get("Last-Modified"));
        } catch (NullPointerException e) {
            lastSavedDate = null;
        }

        LanguageResult language = langDetector.detect(handler.toString());

        saveResult(file.getName(), language.getLanguage(), metadata.get("meta:author"), creationDate,
                lastSavedDate, metadata.get("Content-Type"), handler.toString()); //TODO: fill with proper values
    }

    private void saveResult(String fileName, String language, String creatorName, Date creationDate,
                            Date lastModification, String mimeType, String content)
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        int index = fileName.lastIndexOf(".");
        String outName = fileName.substring(0, index) + ".txt";
        try
        {
            PrintWriter printWriter = new PrintWriter("./outputDocuments/" + outName);
            printWriter.write("Name: " + fileName + "\n");
            printWriter.write("Language: " + (language != null ? language : "") + "\n");
            printWriter.write("Creator: " + (creatorName != null ? creatorName : "") + "\n");
            String creationDateStr = creationDate == null ? "" : dateFormat.format(creationDate);
            printWriter.write("Creation date: " + creationDateStr + "\n");
            String lastModificationStr = lastModification == null ? "" : dateFormat.format(lastModification);
            printWriter.write("Last modification: " + lastModificationStr + "\n");
            printWriter.write("MIME type: " + (mimeType != null ? mimeType : "") + "\n");
            printWriter.write("\n");
            printWriter.write(content + "\n");
            printWriter.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

}
