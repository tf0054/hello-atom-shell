(ns hello-world.core)

(def app (js/require "app"))
(def browser-window (js/require "browser-window"))
(def crash-reporter (js/require "crash-reporter"))
(def ipc (js/require "ipc"))
(def log (.-log js/console))

(def counter (atom 0N))

(def main-window (atom nil))

(defn init-browser []
  (reset! main-window (browser-window. (clj->js {:width 800 :height 600})))
  (.loadUrl @main-window (str "file://" js/__dirname "/index.html"))
  (.on @main-window "closed" #(reset! main-window nil)))

(defn loglog [x y]
  (log "Select file.")
  ; event.sender.send('asynchronous-reply', filePath);
  (.send (.. x -sender) "asynchronous-reply" (str "xxx" @counter "xxx"))
  (swap! counter inc))

(.on ipc "openFileDialog" loglog);

(.start crash-reporter)
(.on app "window-all-closed"
     #(when-not (= js/process.platform "darwin") (.quit app)))
(.on app "ready"
     #(init-browser))
