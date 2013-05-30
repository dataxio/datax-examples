package datax.examples.gh;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class CounterBolt extends BaseBasicBolt {
    Map<String, Integer> counts = new HashMap<String, Integer>();
    public static final Logger LOG = LoggerFactory.getLogger(CounterBolt.class);

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
    	if(isTickTuple(tuple)) {
    	    LOG.info("Current Counts: " + prettyPrintMap(counts));
    	}
    	else {
            String word = tuple.getString(0);
            Integer count = counts.get(word);
            if(count==null) count = 0;
            count++;
            counts.put(word, count);
    	}
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }
    
    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 5);
        return conf;
    }
    
    private static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
            && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }
    
    private static <K, V> String prettyPrintMap(Map<K, V> map) {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<K, V> entry = iter.next();
            sb.append(entry.getKey());
            sb.append(" = ");
            sb.append(entry.getValue());
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();

    }
}