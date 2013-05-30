package datax.examples.gh;

import datax.examples.gh.GrammrHammrRuleBolt;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class GrammrHammrTopology {    
	
    public static void main(String[] args) throws Exception {
		// Print Banner
		System.out.println("#################################################################");
		System.out.println("");
		System.out.println("GrammrHammr Topology");
		System.out.println("");
		System.out.println("#################################################################");
		System.out.println("");
		System.out.println("By Ian Alexander");	
		System.out.println("");
		System.out.println("Usage: storm jar whatever.jar storm.starter.GrammarNaziTopology [-l|-p] <twitterUserName> <twitterPassword>");
		
		// Check for right amount of arguments (Needs a switch and u/p to run)
        if(args == null || args.length != 3)
		{
			System.out.println("\nMandatory arguments:");
			System.out.println("  -l\t\trun in local mode");
			System.out.println("  -p\t\trun in production mode");
			return;
		}
		
		// Build topology
        TopologyBuilder builder = new TopologyBuilder();
 
        builder.setSpout("twitterSpout", new TwitterSpout(args[1], args[2]), 1);
        builder.setBolt("ghRuleBolt", new GrammrHammrRuleBolt(), 10)
        	.shuffleGrouping("twitterSpout");
        builder.setBolt("counterBolt", new CounterBolt(), 1)
        	.fieldsGrouping("ghRuleBolt", new Fields("matchMsg"));
      
        Config conf = new Config();
		
		// Check for flags
        if(args[0].equals("-p")) { 			//run production
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology(
				"grammrhammr", conf, builder.createTopology()
			);
        } else if(args[0].equals("-l")) { 	//run local     
            conf.setMaxTaskParallelism(3);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(
				"local-grammrhammr", conf, builder.createTopology()
			);
        
            Thread.sleep(300 * 1000);
            cluster.shutdown();
        }
		else {								//bad arguments
			System.out.println("Error: bad arguments");
		}
    }
}
