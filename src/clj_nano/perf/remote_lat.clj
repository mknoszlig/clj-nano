(ns clj-nano.perf.remote-lat
  (:refer-clojure :exclude [send])
  (:use [clj-nano core])
  (:import [java.nio ByteBuffer]))

(defn print-stats [begin-time end-time message-size roundtrip-count]
  (println (format "message size: %d [B]" message-size))
  (println (format "roundtrip count: %d" roundtrip-count))
  (println (format "avg latency: %.3f [us]" (/ (double (- end-time begin-time))
                                               (* roundtrip-count 2 1000)))))

(defn mk-message [message-size]
  (byte-buffer (byte-array message-size (byte 111))))

(defn run-benchmark [addr message-size roundtrip-count]
  (let [s    (-> (socket :pair)
                 (connect addr))
        msg  (mk-message message-size)
        buf  (ByteBuffer/allocateDirect message-size)]
    (println (format "NANO PAIR socket bound to %s" addr))
    (println (format "NANO running %d iterations.." roundtrip-count))
    (let [begin (System/nanoTime)]
      (dotimes [_ roundtrip-count]
        (send s msg message-size)
        (receive s buf message-size))
      (let [end (System/nanoTime)]
        (close s)
        (print-stats begin end message-size roundtrip-count)))))

(defn -main [addr msg-size iterations]
  (let [message-size (Integer/parseInt msg-size)
        roundtrip-count (Integer/parseInt iterations)]
    (run-benchmark addr message-size roundtrip-count)))
