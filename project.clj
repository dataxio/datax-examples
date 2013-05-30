(defproject datax-examples "0.0.1-SNAPSHOT"
  :description "Write description"
  :url "http://datax.io"
  :java-source-paths ["src/jvm"]
  :aot :all
  :plugins [[no-man-is-an-island/lein-eclipse "2.0.0"]]
  :repositories {
    "twitter4j" "http://twitter4j.org/maven2"
  }

  :dependencies [
    [org.twitter4j/twitter4j-core "2.2.6-SNAPSHOT"]
    [org.twitter4j/twitter4j-stream "2.2.6-SNAPSHOT"]
    [commons-collections/commons-collections "3.2.1"]
  ]

  :profiles 
  {:dev
    {:dependencies [
      [storm "0.8.2"]
      [org.clojure/clojure "1.4.0"]
    ]}
  }

  :min-lein-version "2.0.0"
)
