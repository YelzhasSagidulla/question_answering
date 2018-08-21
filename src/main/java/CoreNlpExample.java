import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import java.util.Map;
import java.util.HashMap;

public class CoreNlpExample {

    public static void main(String[] args) {

        String question = "Who is the President of India?", answer; //Where does Pranab Mukherjee live?
        Double score, max_score = 0.0;

        System.out.println("question: "+question);

        question = question.replace("?", "");

        //key value pairs for answer
        Map<Double, String> map = new HashMap<Double, String>();

        //question types
        String question_types[];
        question_types = new String[10];

        //stop words
        String[] stop_words = new String[]{"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};

        //take interrogative
        String interrogative = question.split(" ")[0].toLowerCase();

        //define question type(s)
        if( interrogative.equals("who") || interrogative.equals("what") ){
            question_types[0] = "PERSON";
            question_types[1] = "PLACE";
            question_types[2] = "ORGANIZATION";
        } else if( interrogative.equals("when") ) {
            question_types[0] = "TIME";
            question_types[1] = "DATE";
        } else if( interrogative.equals("where") ) {
            question_types[0] = "LOCATION";
            question_types[1] = "PLACE";
        } else if( interrogative.equals("why") ) {
            question_types[0] = "REASON";
        } else if( interrogative.equals("how") && question.split(" ")[1].toLowerCase().equals("many") ) {
            question_types[0] = "NUMBER";
        }

        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
        String text = "Shri Pranab Mukherjee is President of India. He is the 13th President of India. Pranab Mukherjee is 76 years\n" +
                "old. Shri Mukherjee acquired a Master’s degree in History and Political Science as well as in Law from\n" +
                "University of Kolkata. He has authored several books on Indian Economy and Nation building. Many awards\n" +
                "and honors conferred him. He was rated one of the best five Finance ministers of the world in 1984 according to\n" +
                "the survey conducted by “Euro Money”, Journal published from New York. In 2012, he visited USA. There he\n" +
                "had meeting with President of USA. Mr Barack Obama is 44th President of USA. He is 51 years old. Full\n" +
                "name of Barack Obama is Barack Hussein Obama. Natasha Obama and Malia Ann Obama are the two children\n" +
                "of Barack Obama. The inauguration of Barack Obama as the 44th president took place on January 20, 2009. He\n" +
                "was re-elected as President in November 2012. He defeated Republican nominee Mitt Romney. He swore in for\n" +
                "a second term on January 20, 2013. ";

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        boolean sentence_contained = false;

        for (CoreMap sentence : sentences) {
            answer = "";
            score = 0.0;
            sentence_contained = false;
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

                //check if Named Entity is in array of question types
                if(Arrays.asList(question_types).contains(ne)){
                    answer += " " + word;
                    if(!sentence_contained){
                        sentence_contained = true;
                        //count shared words
                        for (String sentence_word:sentence.toString().replace(".","").split(" ")) {
                            for (String question_word:question.split(" ")) {
                                //if its not stop word and exist in both
                                if(!Arrays.asList(stop_words).contains(sentence_word.toLowerCase()) && sentence_word.toLowerCase().equals(question_word.toLowerCase()))
                                {
                                    //System.out.println("Equal sentence word: "+sentence_word);
                                    score++;
                                }
                            }
                        }
                    }
                }
                //System.out.println(String.format("Print: word: [%s] pos: [%s] ne: [%s]", word, pos, ne));
            }
            if(!answer.equals("") && sentence_contained)
            {
                if(score>max_score){
                    max_score = score;
                }

                map.put(score, answer);

                System.out.println("Option: "+answer+", Score: "+score);
            }
        }

        //System.out.println(max_score);
        System.out.println("Answer: "+map.get(max_score));
    }
}
