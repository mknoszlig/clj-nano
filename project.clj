(defproject clj-nano "0.1.0-SNAPSHOT"
  :description "clojure bindings for the nanomsg messaging library"
  :url "http://github.com/mknoszlig/clj-nano"
  :license {:name "MIT/X11 License, same as nanomsg"
            :url ""}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [nanomsg/jnano "0.1"]]
  :jvm-opts ["-Djava.library.path=/usr/lib:/usr/local/lib:/Users/mknoszlig/work/jnano/build/c/sharedlibrary"]
  :aot :all)
