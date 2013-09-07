(ns clj-nano.perf.local-thr
  (:refer-clojure :exclude [send])
  (:use [clj-nano core])
  (:import [java.nio ByteBuffer]))

(defn print-stats [begin end message-size message-count]
  (let [throughput (int (/ (* 1000000000.0 message-count)
                           (- end begin)))
        megabits   (/ (* throughput message-size 8)
                      1000000.0)]
    (println (format "message size: %d [B]" message-size))
    (println (format "message count: %d" message-count))
    (println (format "mean throughput: %d [msgs/s]" throughput))
    (println (format "mean throughput: %.3f [Mb/s]" megabits))))

(defn mk-message [message-size]
  (byte-buffer (byte-array message-size (byte 111))))

(defn run-benchmark [addr message-size message-count]
  (let [s    (-> (socket :pair)
                 (bind addr))
        msg  (mk-message message-size)
        buf  (ByteBuffer/allocateDirect message-size)]
    (println (format "NANO PAIR socket bound to %s" addr))
    (println (format "NANO running %d iterations.." message-count))
    (receive s buf message-size)
    (let [begin (System/nanoTime)]
      (dotimes [_ message-count]
        (receive s buf message-size))
      (let [end (System/nanoTime)]
        (close s)
        (print-stats begin end message-size message-count)))))

(defn -main [addr message-size message-count]
  (run-benchmark addr
                 (Integer/parseInt message-size)
                 (Integer/parseInt message-count)))
