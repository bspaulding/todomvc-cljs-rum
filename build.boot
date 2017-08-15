(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources/public"}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.473"]
                 [adzerk/boot-cljs "1.7.228-2"]
                 [pandeiro/boot-http "0.7.6"]
                 [org.clojure/tools.nrepl "0.2.12"] ;; BUG: required by boot-http
                 [adzerk/boot-reload "0.5.1"]
                 [adzerk/boot-cljs-repl "0.3.3"]
                 [com.cemerick/piggieback "0.2.1" :scope "test"] ;; needed by bREPL
                 ;; needed by bREPL
                 [weasel "0.7.0" :scope "test"]
                 ;; app dependencies
                 [rum "0.10.8"]
                 [secretary "1.2.3"]
								 [binaryage/devtools "0.9.4" :scope "test"]
								 [powerlaces/boot-cljs-devtools "0.2.0" :scope "test"]
								 [crisptrutski/boot-cljs-test "0.3.0" :scope "test"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[powerlaces.boot-cljs-devtools :refer [cljs-devtools]]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
  test-cljs {:js-env :node})

(deftask testing []
  (merge-env! :source-paths #{"test"})
  identity)

(deftask test []
	(comp
		(testing)
		(test-cljs)))

(deftask dev
  "Launch Immediate Feedback Dev Env"
  []
  (comp
    (serve :dir "target" :httpkit true :port 3000)
    (watch)
    (reload)
    (cljs-repl)
		(cljs-devtools)
    (cljs :source-map true :optimizations :none)
    (target :dir #{"target"})))
