(ns clj-nano.perf.inproc-thr
  (:gen-class)
  (:refer-clojure :exclude [send])
  (:use [clj-nano core])
  (:import [java.util.concurrent CountDownLatch]
           [java.nio ByteBuffer]))

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

(defn mk-worker [message-size message-count done addr]
  (fn []
    (Thread/sleep 10)
    (let [msg (mk-message message-size)
          buf (ByteBuffer/allocateDirect message-size)
          s   (-> (socket :pair)
                  (connect addr))]
      (println (format "[w] NANO PAIR socket connected to %s" addr))
      (println (format "[w] NANO running %d iterations..." message-count))
      (send s msg message-size)
      (dotimes [_ message-count]
        (send s msg message-size))
      (close s)
      (.countDown done))))

(defn run-benchmark [message-size message-count]
  (let [addr "inproc://inproc_thr_test"
        s    (-> (socket :pair)
                 (bind addr))
        msg  (mk-message message-size)
        done (CountDownLatch. 1)
        buf  (ByteBuffer/allocateDirect message-size)]
    (println (format "NANO PAIR socket bound to %s" addr))
    (.start (Thread. (mk-worker message-size message-count done addr)))
    (println (format "NANO running %d iterations.." message-count))
    (receive s buf message-size)
    (let [begin (System/nanoTime)]
      (dotimes [_ message-count]
        (receive s buf message-size))
      (let [end (System/nanoTime)]
        (.await done)
        (close s)
        (print-stats begin end message-size message-count)))))

(defn -main [message-size message-count]
  (run-benchmark (Integer/parseInt message-size)
                 (Integer/parseInt message-count)))
