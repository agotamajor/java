import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class SpamSzures {
	public static String removeNum(String string) {
		string = string.replaceAll("[^a-z]"," ");
		string = string.replaceAll("[ ]+", " ");
        return string;
	}
	
	public static String removeWord(String string, String[] words) { 
        for (String word : words) {
        	if (string.contains(word)) { 
                String tempWord = " " + word + " "; 
                string = string.replaceAll(tempWord, " ");
            } 
        }
        return string; 
    }
	
	public static void main(String[] args) throws FileNotFoundException {
        File myObj = new File("../train.txt");
	    Scanner myReader = new Scanner(myObj);  
	    
	    String[] words = {"Subject:", "a", "able", "about", "across", "after", "all", "almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "she", "should", "since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your"};
	    Map<String, Integer> spamHist  = new TreeMap<String, Integer>();
	    Map<String, Integer> hamHist  = new TreeMap<String, Integer>();
	    String hamData = " ";
		String spamData = " ";
	    
		int i = 0;
		
		int spamSzam = 0;
        int hamSzam = 0; 
        
		
	    while (myReader.hasNextLine()) {
	    	String file = myReader.nextLine();
	    	if (file.contains("ham")) {
        		// ham
	    		hamSzam++;
	    		File myHam = new File("../enron6/ham/"+file);
	    		Scanner myHamReader = new Scanner(myHam);
        		while (myHamReader.hasNextLine()) {
        	    	hamData += myHamReader.nextLine() + " ";
        	    }
        		myHamReader.close();
        	} else {
        		// spam
        		spamSzam++;
        		File mySpam = new File("../enron6/spam/"+file);
        		Scanner mySpamReader = new Scanner(mySpam);
        		while (mySpamReader.hasNextLine()) {
        	    	spamData += mySpamReader.nextLine() + " ";
        	    }
        		mySpamReader.close();
        	}
	    }
	    myReader.close();
	    
	    spamData.toLowerCase();
	    hamData.toLowerCase();
	    
        String removeStoppWordsSpam = removeWord(spamData, words);
        String removeStoppWordsHam = removeWord(hamData, words);
        
        String removeSpam = removeNum(removeStoppWordsSpam);
        String removeHam = removeNum(removeStoppWordsHam);
        
        Map<String, Integer> V  = new TreeMap<String, Integer>();
        int V_length = 0;
        
        //hisztogram 
        String[] tokens = removeHam.split(" ");
        for (String token : tokens) {
        	if (hamHist.containsKey(token)) {
        		Integer j = hamHist.get(token);
        		hamHist.put(token, j+1);
        	} else {
        		hamHist.put(token, 1);
        	}
        	if (V.containsKey(token)) {
        		Integer j = V.get(token);
        		V.put(token, j+1);
        	} else {
        		V.put(token, 1);
        		V_length++;
        	}
        }
        
        tokens = removeSpam.split(" ");
        for (String token : tokens) {
        	if (spamHist.containsKey(token)) {
        		Integer j = spamHist.get(token);
        		spamHist.put(token, j+1);
        	} else {
        		spamHist.put(token, 1);
        	}
        	if (V.containsKey(token)) {
        		Integer j = V.get(token);
        		V.put(token, j+1);
        	} else {
        		V.put(token, 1);
        		V_length++;
        	}
        }
        
        spamHist.remove("");
        hamHist.remove("");
        
        int spamOsszes = 0;
        int hamOsszes = 0;
        
        
        for (Map.Entry<String, Integer> kv : spamHist.entrySet()) {
        	spamOsszes += kv.getValue();
        	// spamSzam++;
        }
        
        for (Map.Entry<String, Integer> kv : hamHist.entrySet()) {
        	hamOsszes += kv.getValue();
        	// hamSzam++;
        }
        
        double P_spam = (double)spamSzam / (double)(spamSzam + hamSzam);
        double P_ham = (double)hamSzam / (double)(spamSzam + hamSzam);
        
        //parameterek becslese
        Map<String, Double> V_spam  = new TreeMap<String, Double>();
	    Map<String, Double> V_ham  = new TreeMap<String, Double>();
	    
	    for (Map.Entry<String, Integer> kv : spamHist.entrySet()) {
        	Double value = (double) (kv.getValue().doubleValue() / spamOsszes);
	    	V_spam.put(kv.getKey(), value);
        }
        
        for (Map.Entry<String, Integer> kv : hamHist.entrySet()) {
        	Double value = (double) (kv.getValue().doubleValue() / hamOsszes);
	    	V_ham.put(kv.getKey(), value);
        }
        
        //additiv simitas
        double alfa = 1.0; //0.01 0.1, 1.0 
        Map<String, Double> additiv_spam  = new TreeMap<String, Double>();
	    Map<String, Double> additiv_ham  = new TreeMap<String, Double>();
	    
	    for (Map.Entry<String, Integer> kv : spamHist.entrySet()) {
        	Double value = (double) ((kv.getValue().doubleValue() + alfa) / (spamOsszes + alfa * V_length));
	    	additiv_spam.put(kv.getKey(), value);
        }
        
        for (Map.Entry<String, Integer> kv : hamHist.entrySet()) {
        	Double value = (double) ((kv.getValue().doubleValue() + alfa) / (hamOsszes + alfa * V_length));
	    	additiv_ham.put(kv.getKey(), value);
        }
        
        //teszteles
        //tanulasi hiba
        Scanner Reader = new Scanner(myObj);
        int talalat = 0;
        
        while (Reader.hasNextLine()) {
	    	String file = Reader.nextLine();
	    	
	    	File myEmail; 
	    	if (file.contains("ham")) {
	    		myEmail = new File("../enron6/ham/"+file);
	    	} else {
	    		myEmail = new File("../enron6/spam/"+file);
	    	}
	    	
	    	Scanner myEmailReader = new Scanner(myEmail);
	    	String email = " ";
	    	while (myEmailReader.hasNextLine()) {
    	    	email += myEmailReader.nextLine() + " ";
    	    }
    		myEmailReader.close();
    		email.toLowerCase();
    		String removeStoppWordsEmail = removeWord(email, words);
    		String removeEmail = removeNum(removeStoppWordsEmail);
    		tokens = removeEmail.split(" ");
    		double P0 = 0.00000001;
    		Map<String, Integer> emailHist  = new TreeMap<String, Integer>();
    		for (String token : tokens) {
    			if (emailHist.containsKey(token)) {
            		Integer j = emailHist.get(token);
            		emailHist.put(token, j+1);
            	} else {
            		emailHist.put(token, 1);
            	}
            }
    		tokens = removeEmail.split(" ");
    		double sum = 0.0;
    		for (String token : tokens) {
    			double pSpam, pHam;
    			if (V_spam.containsKey(token)) {
    				pSpam = Math.log(V_spam.get(token));
    			} else {
    				pSpam = Math.log(P0);
    			}
    			if (V_ham.containsKey(token)) {
    				pHam = Math.log(V_ham.get(token));
    			} else {
    				pHam = Math.log(P0);
    			}
    			sum += emailHist.get(token) * (pSpam - pHam);
            }
    		double L = Math.log(P_spam) - Math.log(P_ham) + sum;
    		if (L < 0 && file.contains("ham")) {
        		// ham
        		talalat++;
        	} else {
        		// spam
        		if (L > 0 && file.contains("spam")) {
        			talalat++;
        		}
        	}
	    }
	    Reader.close();
	    
	    double tanulasiHiba = (double) talalat / (double)(spamSzam + hamSzam);
	    System.out.println("Tanulasi hiba: " + tanulasiHiba); //0.0655401784306926
	    
	    //teszt hiba
	    
	    File myObjTeszt = new File("../test.txt");
	    Scanner ReaderTeszt = new Scanner(myObjTeszt);
        talalat = 0;
        
        while (ReaderTeszt.hasNextLine()) {
	    	String file = ReaderTeszt.nextLine();
	    	
	    	File myEmail; 
	    	if (file.contains("ham")) {
	    		myEmail = new File("../enron6/ham/"+file);
	    	} else {
	    		myEmail = new File("../enron6/spam/"+file);
	    	}
	    	
	    	Scanner myEmailReader = new Scanner(myEmail);
	    	String email = " ";
	    	while (myEmailReader.hasNextLine()) {
    	    	email += myEmailReader.nextLine() + " ";
    	    }
    		myEmailReader.close();
    		email.toLowerCase();
    		String removeStoppWordsEmail = removeWord(email, words);
    		String removeEmail = removeNum(removeStoppWordsEmail);
    		tokens = removeEmail.split(" ");
    		double P0 = 0.00000001;
    		Map<String, Integer> emailHist  = new TreeMap<String, Integer>();
    		for (String token : tokens) {
    			if (emailHist.containsKey(token)) {
            		Integer j = emailHist.get(token);
            		emailHist.put(token, j+1);
            	} else {
            		emailHist.put(token, 1);
            	}
            }
    		tokens = removeEmail.split(" ");
    		double sum = 0.0;
    		for (String token : tokens) {
    			double pSpam, pHam;
    			if (V_spam.containsKey(token)) {
    				pSpam = Math.log(V_spam.get(token));
    			} else {
    				pSpam = Math.log(P0);
    			}
    			if (V_ham.containsKey(token)) {
    				pHam = Math.log(V_ham.get(token));
    			} else {
    				pHam = Math.log(P0);
    			}
    			sum += emailHist.get(token) * (pSpam - pHam);
            }
    		double L = Math.log(P_spam) - Math.log(P_ham) + sum;
    		if (L < 0 && file.contains("ham")) {
        		// ham
        		talalat++;
        	} else {
        		// spam
        		if (L > 0 && file.contains("spam")) {
        			talalat++;
        		}
        	}
	    }
	    ReaderTeszt.close();
	    
	    double tesztHiba = (double) talalat / (double)(spamSzam + hamSzam);
	    System.out.println("Teszt hiba: " + tesztHiba); //0.02818322247091832
	    
	  //tanulasi hiba additiv simitasra
        Scanner ReaderAddittiv = new Scanner(myObj);
        talalat = 0;
        
        while (ReaderAddittiv.hasNextLine()) {
	    	String file = ReaderAddittiv.nextLine();
	    	
	    	File myEmail; 
	    	if (file.contains("ham")) {
	    		myEmail = new File("../enron6/ham/"+file);
	    	} else {
	    		myEmail = new File("../enron6/spam/"+file);
	    	}
	    	
	    	Scanner myEmailReader = new Scanner(myEmail);
	    	String email = " ";
	    	while (myEmailReader.hasNextLine()) {
    	    	email += myEmailReader.nextLine() + " ";
    	    }
    		myEmailReader.close();
    		email.toLowerCase();
    		String removeStoppWordsEmail = removeWord(email, words);
    		String removeEmail = removeNum(removeStoppWordsEmail);
    		tokens = removeEmail.split(" ");
    		double P0 = 0.00000001;
    		Map<String, Integer> emailHist  = new TreeMap<String, Integer>();
    		for (String token : tokens) {
    			if (emailHist.containsKey(token)) {
            		Integer j = emailHist.get(token);
            		emailHist.put(token, j+1);
            	} else {
            		emailHist.put(token, 1);
            	}
            }
    		tokens = removeEmail.split(" ");
    		double sum = 0.0;
    		for (String token : tokens) {
    			double pSpam, pHam;
    			if (additiv_spam.containsKey(token)) {
    				pSpam = Math.log(additiv_spam.get(token));
    			} else {
    				pSpam = Math.log(P0);
    			}
    			if (additiv_ham.containsKey(token)) {
    				pHam = Math.log(additiv_ham.get(token));
    			} else {
    				pHam = Math.log(P0);
    			}
    			sum += emailHist.get(token) * (pSpam - pHam);
            }
    		double L = Math.log(P_spam) - Math.log(P_ham) + sum;
    		if (L < 0 && file.contains("ham")) {
        		// ham
        		talalat++;
        	} else {
        		// spam
        		if (L > 0 && file.contains("spam")) {
        			talalat++;
        		}
        	}
	    }
	    ReaderAddittiv.close();
	    
	    double tanulasiHibaAddittiv = (double) talalat / (double)(spamSzam + hamSzam);
	    System.out.println("Tanulasi hiba: " + tanulasiHibaAddittiv); //alfa = 0.01 0.0655401784306926 | alfa = 0.1 0.06557170328804263 | alfa = 1 0.06561899057406766
	    
	    //teszthiba addittiv simitasra
	    Scanner ReaderTesztAdditiv = new Scanner(myObjTeszt);
        talalat = 0;
        
        while (ReaderTesztAdditiv.hasNextLine()) {
	    	String file = ReaderTesztAdditiv.nextLine();
	    	
	    	File myEmail; 
	    	if (file.contains("ham")) {
	    		myEmail = new File("../enron6/ham/"+file);
	    	} else {
	    		myEmail = new File("../enron6/spam/"+file);
	    	}
	    	
	    	Scanner myEmailReader = new Scanner(myEmail);
	    	String email = " ";
	    	while (myEmailReader.hasNextLine()) {
    	    	email += myEmailReader.nextLine() + " ";
    	    }
    		myEmailReader.close();
    		email.toLowerCase();
    		String removeStoppWordsEmail = removeWord(email, words);
    		String removeEmail = removeNum(removeStoppWordsEmail);
    		tokens = removeEmail.split(" ");
    		double P0 = 0.00000001;
    		Map<String, Integer> emailHist  = new TreeMap<String, Integer>();
    		for (String token : tokens) {
    			if (emailHist.containsKey(token)) {
            		Integer j = emailHist.get(token);
            		emailHist.put(token, j+1);
            	} else {
            		emailHist.put(token, 1);
            	}
            }
    		tokens = removeEmail.split(" ");
    		double sum = 0.0;
    		for (String token : tokens) {
    			double pSpam, pHam;
    			if (additiv_spam.containsKey(token)) {
    				pSpam = Math.log(additiv_spam.get(token));
    			} else {
    				pSpam = Math.log(P0);
    			}
    			if (additiv_ham.containsKey(token)) {
    				pHam = Math.log(additiv_ham.get(token));
    			} else {
    				pHam = Math.log(P0);
    			}
    			sum += emailHist.get(token) * (pSpam - pHam);
            }
    		double L = Math.log(P_spam) - Math.log(P_ham) + sum;
    		if (L < 0 && file.contains("ham")) {
        		// ham
        		talalat++;
        	} else {
        		// spam
        		if (L > 0 && file.contains("spam")) {
        			talalat++;
        		}
        	}
	    }
	    ReaderTesztAdditiv.close();
	    
	    double tesztHibaAddittiv = (double) talalat / (double)(spamSzam + hamSzam);
	    System.out.println("Teszt hiba: " + tesztHibaAddittiv);  //alfa = 0.01  0.02818322247091832 | alfa = 0.1 0.028214747328268338 | alfa = 1 0.02829355947164339
	}
}
