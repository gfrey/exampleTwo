(ns exampleTwo.server
  (:require [exampleTwo.db :as db]
            [noir.server :as server]))

(server/load-views "src/exampleTwo/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (db/init "database")
    (server/start port {:mode mode
                        :ns 'exampleTwo})))

