(ns clj-nano.perf.remote-thr
  (:refer-clojure :exclude [send])
  (:use [clj-nano core])
  (:import [java.nio ByteBuffer]))

(defn mk-message [message-size]
  (byte-buffer (byte-array message-size (byte 111))))

(defn run-benchmark [addr message-size message-count]
  (let [s    (-> (socket :pair)
                 (connect addr))
        msg  (mk-message message-size)
        buf  (ByteBuffer/allocateDirect message-size)]
    (println (format "NANO PAIR socket bound to %s" addr))
    (println (format "NANO running %d iterations.." message-count))
    (send s buf message-size)
    (dotimes [_ message-count]
      (send s buf message-size))
    (close s)))

(defn -main [addr message-size message-count]
  (run-benchmark addr
                 (Integer/parseInt message-size)
                 (Integer/parseInt message-count)))
