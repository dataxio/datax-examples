# datax-examples

These are DataX's example applications for [storm][http://storm-project.net].

datax-examples uses the excellent [leiningen 2.0.0][http://leiningen.org/] for build management. The most recent installation instructions can be found on their website.

## Deploying on DataX

Want to deploy a storm application without setting up your own cluster? Follow these steps:

1. Download and install the datax-client: 
2. Register at http://datax.io/
3. Run the following commands:

    $ datax login # Login to DataX
    $ datax auth 1.2.3.4/32 # Safety first! Authorize your IP range.
    $ datax storm create StarterCluster 2 S 0.8.2 # Create a new cluster (2 = Number of Nodes, S = Worker Size, 0.8.2 = Storm Version)
    $ datax storm list # Wait for the cluster to become 'READY'
    $ datax storm deploy StarterCluster ~/datax-starter.jar datax.examples.gh.GrammrHammrTopology -p TwitterUsername TwitterPassword # Push your application to your cluster!

# Examples:
## GrammrHammr

GrammrHammr is an application that checks twitter for bad grammar. To use this application:

    $ git clone https://github.com/dataxio/datax-examples
    $ cd datax-examples
    $ lein deps
    $ lein javac
    $ lein uberjar