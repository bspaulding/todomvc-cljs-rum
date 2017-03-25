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
                 [rum "0.10.8"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]])

(deftask dev
  "Launch Immediate Feedback Dev Env"
  []
  (comp
    (serve :dir "target")
    (watch)
    (reload)
    (cljs-repl)
    (cljs)
    (target :dir #{"target"})))
