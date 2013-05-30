package datax.examples.gh;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class GrammrHammrRuleBolt extends BaseBasicBolt {
	HashMap<Pattern,String> rulesMap = new HashMap<Pattern,String>();
	public static final Logger LOG = LoggerFactory.getLogger(GrammrHammrRuleBolt.class);
	
	// This rule filters out non-english characters, 
	// because we're only testing english rules
	Pattern englishOnly = Pattern.compile("[^\\x00-\\x94]");
	
	public GrammrHammrRuleBolt(){
		// http://openatd.trac.wordpress.org/browser/atd-server/data/rules/grammar?order=name
		// http://www.regexplanet.com/advanced/java/index.html
		
		// Word should be "you're"
		rulesMap.put(
				Pattern.compile("your (the|a|an|in|at|right|not|so|as|gonna|welcome) "), 
				"Incorrect usage of \"your\""
		);
		
		rulesMap.put(
				Pattern.compile(" to you're"), 
				"Incorrect usage of \"you're\""
		);
		rulesMap.put(
				Pattern.compile("(has|is) you're"), 
				"Incorrect usage of \"you're\""
		);
		
		// Word should be "there"
		rulesMap.put(
				Pattern.compile("their (is|are) "), 
				"Incorrect usage of \"their\""
		); 
		
		// Word should be "they're"
		rulesMap.put(
				Pattern.compile("their ([a-zA-Z]+ing |gonna)"), 
				"Incorrect usage of \"their\""
		);
		
		// Word should be "a lot"
		rulesMap.put(
				Pattern.compile(" alot "), 
				"Incorrect spelling of \"a lot\""
		);
		
		// Spelling mistakes
		rulesMap.put(
				Pattern.compile("hippocrates"), 
				"Incorrect spelling of \"hypocrites\""
		);
		rulesMap.put(
				Pattern.compile("bicuriously"), 
				"Incorrect spelling of \"vicariously\""
		);
		rulesMap.put(
				Pattern.compile("genious"), 
				"Incorrect spelling of \"genius\""
		);
		
		// Caps lock is cruise control for cool
		rulesMap.put(
				Pattern.compile("^[A-Z !]*$"), 
				"Caps lock left on"
		); 
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		Status myStatus = (Status) input.getValue(0);
		String tweetBody = myStatus.getText();
		
		Matcher mEnglish = englishOnly.matcher(tweetBody);
		
		if (mEnglish.find()) {
			//System.out.println("[Non-English]"+tweetBody);
			return;
		}
		else if ( 	
				tweetBody.length() < 6 || 
				tweetBody.toLowerCase().contains(" que ") || 
				tweetBody.toLowerCase().contains(" de ") ||
				tweetBody.toLowerCase().contains(" jaja")
				) {
			//System.out.println("[Short/que/de]"+tweetBody);
			return;
		}
		else if (myStatus.isRetweet()) {
			//System.out.println("[Retweet!]"+tweetBody);
			return;
		}
		else {
			//System.out.println("[PASS]"+tweetBody);
		}
		
		Set set = rulesMap.entrySet();
		Iterator i = set.iterator();
		
		while (i.hasNext()) {
			Map.Entry<Pattern,String> myEntry = (Entry<Pattern, String>) i.next();
			
			Pattern rule = myEntry.getKey();
			Matcher m = rule.matcher(tweetBody);
			
			if ( m.find() ) {
				//bad grammar
				//System.out.println("[Rule Match!]["+rule+"]["+tweetBody+"]"+myEntry.getValue());
				LOG.info("Rule Match: [" + myEntry.getValue() + "][" + tweetBody + "]");
				collector.emit(new Values(myEntry.getValue()));
				break;
			}
			else {
				//System.out.println("[No Match]["+rule+"]["+tweetBody+"]");
			}
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("matchMsg"));
		
	}
	
}