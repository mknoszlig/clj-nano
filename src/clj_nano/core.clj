(ns clj-nano.core
  (:refer-clojure :exclude [send])
  (:import [org.nanomsg NanoLibrary]
           [java.nio ByteBuffer]))

(set! *warn-on-reflection* true)

(defonce ^NanoLibrary inst (NanoLibrary.))

(def transports
  {:inproc (. inst NN_INPROC)
   :ipc    (. inst NN_IPC)
   :tcp    (. inst NN_TCP)})

(def domains
  {:af-sp (. inst AF_SP)
   :af-sp-raw (. inst AF_SP_RAW)})

(def socket-types
  {:pair       (. inst NN_PAIR)
   :pub        (. inst NN_PUB)
   :sub        (. inst NN_SUB)
   :rep        (. inst NN_REP)
   :req        (. inst NN_REQ)
   :source     (. inst NN_SOURCE)
   :sink       (. inst NN_SINK)
   :push       (. inst NN_PUSH)
   :pull       (. inst NN_PULL)
   :surveyor   (. inst NN_SURVEYOR)
   :respondent (. inst NN_RESPONDENT)
   :bus        (. inst NN_BUS)})

(defn- non-neg? [value] (not (neg? value)))

(comment
  (defmacro assert-non-neg [val fn-name]
    `(assert (non-neg? ~val)
             (format "error in %s: %s\n"
                     ~(name fn-name)
                     (. inst nn_strerror
                        (. inst nn_errno))))))

(def version (. inst get_version))

(defprotocol Closeable
  (close [this]))

(defrecord Socket [domain socket-type ptr]
  Closeable
  (close [{:keys [ptr]}]
    (let [ret (. inst nn_close ptr)]
      (assert (non-neg? ret)
              (let [err-no (. inst nn_errno)]
                (format "Error %d - %s\n"
                        err-no
                        (. inst nn_strerror err-no)))))))

(defn socket
  ([type] (socket type :af-sp))
  ([type domain]
     (let [ptr (. inst nn_socket (domains domain) (socket-types type))]
       (assert (non-neg? ptr)
               (format "error in nn_socket: %s\n" (. inst nn_strerror
                                                     (. inst nn_errno))))
       (->Socket domain type ptr))))

(defn byte-buffer [^bytes value]
  (let [bb (ByteBuffer/allocateDirect (count value))]
    (.put bb value)))

(defn bind [^Socket socket ^String address]
  (let [rc (. inst nn_bind (:ptr socket) address)]
    (assert (non-neg? rc)
            (format "error in nn_bind: %s\n"
                    (. inst nn_strerror
                       (. inst nn_errno)))))
  socket)

(defn connect [^Socket socket ^String address]
  (let [rc (. inst nn_connect (get socket :ptr) address)]
    (assert (non-neg? rc)
            (format "error in nn_connect: %s\n"
                    (. inst nn_strerror
                       (. inst nn_errno)))))
  socket)

(defn send [^Socket socket ^bytes msg]
  (let [bb (byte-buffer msg)
        rc (. inst nn_send (get socket :ptr) bb 0 (count msg) 0)]
    (assert (non-neg? rc)
            (format "error in nn_send: %s\n" (. inst nn_strerror
                                                (. inst nn_errno))))))

(defn receive [^Socket socket message-size]
  (let [bb (ByteBuffer/allocateDirect message-size)
        rc (. inst nn_recv (get socket :ptr) bb 0 message-size 0)
        ret (byte-array message-size)]
    (assert (non-neg? rc)
            (format "error in nn_receive: %s\n"
                    (. inst nn_strerror
                       (. inst nn_errno))))
    (assert (= message-size rc)
            (format "message of incorrect size received\n"))
    (. bb get ret)
    ret))
