(ns clj-nano.perf.inproc-lat
  (:gen-class)
  (:refer-clojure :exclude [send])
  (:use [clj-nano core])
  (:import [java.util.concurrent CountDownLatch]
           [java.nio ByteBuffer]))

(defn print-stats [begin-time end-time message-size roundtrip-count]
  (println (format "message size: %d [B]" message-size))
  (println (format "roundtrip count: %d" roundtrip-count))
  (println (format "avg latency: %.3f [us]" (/ (double (- end-time begin-time))
                                               (* roundtrip-count 2 1000)))))

(defn mk-message [message-size]
  (byte-buffer (byte-array message-size (byte 111))))

(defn mk-worker [message-size roundtrip-count done addr]
  (fn []
    (let [msg (mk-message message-size)
          buf (ByteBuffer/allocateDirect message-size)
          s   (-> (socket :pair)
                  (connect addr))]
      (println (format "NANO PAIR socket connected to %s" addr))
      (println (format "NANO running %d iterations..." roundtrip-count))
      (dotimes [_ roundtrip-count]
        (receive s buf message-size)
        (send s msg message-size))
      (close s)
      (.countDown done))))

(defn run-benchmark [message-size roundtrip-count]
  (let [addr "inproc://inproc_lat_test"
        s    (-> (socket :pair)
                 (bind addr))
        msg  (mk-message message-size)
        done (CountDownLatch. 1)
        buf  (ByteBuffer/allocateDirect message-size)]
    (println (format "NANO PAIR socket bound to %s" addr))
    (.start (Thread. (mk-worker message-size
                                roundtrip-count
                                done
                                addr)))
    (println (format "NANO running %d iterations.." roundtrip-count))
    (let [begin (System/nanoTime)]
      (dotimes [_ roundtrip-count]
        (send s msg message-size)
        (receive s buf message-size))
      (let [end (System/nanoTime)]
        (.await done)
        (close s)
        (print-stats begin end message-size roundtrip-count)))))

(defn -main [msg-size iterations]
  (let [message-size (Integer/parseInt msg-size)
        roundtrip-count (Integer/parseInt iterations)]
    (run-benchmark message-size roundtrip-count)))
