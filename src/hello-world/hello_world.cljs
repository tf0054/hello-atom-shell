(ns hello-world.core)

; atom
(def app (js/require "app"))
(def browser-window (js/require "browser-window"))
(def crash-reporter (js/require "crash-reporter"))
(def ipc (js/require "ipc"))
(def clipboard (js/require "clipboard"))

; npm
(def logw (js/require "winston")) ;winston http://bit.ly/1D34Ctc
(def request (js/require "request"))

; other
(def log (.-log js/console))

; - - - -
(def counter (atom 0N))
(def main-window (atom nil))

(defn init-browser []
  (reset! main-window (browser-window. (clj->js {:width 800 :height 600})))
  (.loadUrl @main-window (str "file://" js/__dirname "/index.html"))
  (.on @main-window "closed" #(reset! main-window nil)))

(defn cbHttpReq [error response body]
  (.log logw "warn" (str
                     (.. response -statusCode) ", "
                     (subs body 0 100))))

(defn loglog [x y]
  (log "Select file.")
  (.log logw "info" "Hello distributed log files!")

  (let [strRes (str "xxx" @counter "xxx")]
    (.send (.. x -sender) "asynchronous-reply" strRes)
    (.writeText clipboard strRes))
  (request "http://www.google.com" cbHttpReq)
  (swap! counter inc))

(.on ipc "openFileDialog" loglog);

(.start crash-reporter)
(.on app "window-all-closed"
     #(when-not (= js/process.platform "darwin") (.quit app)))
(.on app "ready"
     #(init-browser))
