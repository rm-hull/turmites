(defproject turmites "0.1.0-SNAPSHOT"
  :url "http://turmites.destructuring-bind.org"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0-RC1"]
                 [noir "1.3.0-beta10"]
                 [jayq "2.0.0"]
                 [rm-hull/ring-gzip-middleware "0.1.4-SNAPSHOT"]
                 [rm-hull/monet "0.1.3-SNAPSHOT"]]
  :cljsbuild {
    :builds [
      {:source-path "src/turmites/client"
       :compiler {:output-to "resources/public/cljs/turmites.js"
                  ;:optimizations :whitespace
                  :optimizations :advanced
                  :externs ["externs/jquery-1.8.js"]
                  :print-input-delimiter true
                  :pretty-print true}}]}
  :hooks [leiningen.cljsbuild]
  :plugins [[lein-cljsbuild "0.2.10"]]
  :profiles {:dev {:dependencies [[vimclojure/server "2.3.6"]]}}
  :main turmites.server
  :min-lein-version "2.0.0"
  :warn-on-reflection true
  :description "A web-based turmite animator")
