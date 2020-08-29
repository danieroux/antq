(ns antq.dep.clojure-test
  (:require
   [antq.dep.clojure :as sut]
   [antq.record :as r]
   [clojure.java.io :as io]
   [clojure.test :as t]))

(defn- java-dependency
  [m]
  (r/map->Dependency (merge {:type :java
                             :file "deps.edn"
                             :repositories {"antq-test" {:url "s3://antq-repo/"}}}
                            m)))
(defn- git-dependency
  [m]
  (r/map->Dependency (merge {:type :git
                             :file "deps.edn"
                             :repositories {"antq-test" {:url "s3://antq-repo/"}}}
                            m)))

(t/deftest extract-deps-test
  (let [deps (sut/extract-deps
              (slurp (io/resource "dep/deps.edn")))]
    (t/is (sequential? deps))
    (t/is (every? #(instance? antq.record.Dependency %) deps))
    (t/is (= #{(java-dependency {:name "foo/core" :version "1.0.0"})
               (java-dependency {:name "bar/bar" :version "2.0.0"})
               (java-dependency {:name "baz/baz" :version "3.0.0"})
               (git-dependency {:name "git/hello" :version "dummy-sha"
                                :extra {:url "https://github.com/example/hello.git"}})}
             (set deps)))))
