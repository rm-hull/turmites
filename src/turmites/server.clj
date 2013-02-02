(ns turmites.server
  (:require [noir.server :as server]
            [ring.middleware.gzip :as deflate]))

(server/load-views "src/turmites/views")

(defn -main [& m]
  (alter-var-root #'*read-eval* (constantly false))
  (let [mode (keyword (or (first m) :dev))
        port (read-string (get (System/getenv) "PORT" "8080"))]
    (server/add-middleware deflate/wrap-gzip)
    (server/start port {:mode mode
                        :ns 'turmites})))

