(ns clj-nano.perf.local-lat
  (:gen-class)
  (:refer-clojure :exclude [send])
  (:use [clj-nano core])
  (:import [java.nio ByteBuffer]))

(defn mk-message [message-size]
  (byte-buffer (byte-array message-size (byte 111))))


(defn run-benchmark [addr message-size roundtrip-count]
  (let [s    (-> (socket :pair)
                 (bind addr))
        msg  (mk-message message-size)
        buf  (ByteBuffer/allocateDirect message-size)]
    (println (format "NANO PAIR socket bound to %s" addr))
    (println (format "NANO running %d iterations.." roundtrip-count))
    (dotimes [_ roundtrip-count]
      (receive s buf message-size)
      (send s msg message-size))
    (close s)))

(defn -main [addr msg-size iterations]
  (let [message-size (Integer/parseInt msg-size)
        roundtrip-count (Integer/parseInt iterations)]
    (run-benchmark addr message-size roundtrip-count)))
