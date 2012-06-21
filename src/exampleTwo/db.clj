(ns exampleTwo.db
  "A namespace containing the basic database adapter."
  (:refer-clojure :exclude (read))
  (:require [stupiddb.core :as db]))

(def db (atom nil))

(defn init
  "Initializes the database and keeps it available for usage. Keep in
   mind that for stupiddb there must only be one instance opened at a
   time!"
  [path]
  (compare-and-set! db nil (db/db-init path 30)))

(defn read 
  "Read the document with the given id."
  ([type]
     (db/db-get-in @db [type]))
  ([type id]
     (db/db-get-in @db [type id])))

(defn create
  "Create the given document (with the _id field being used as id)."
  [{:keys [type id] :as doc}]
  (db/db-assoc-in @db [type id] doc))

(defn delete
  "Delete the document with the given id."
  [type id]
  (db/db-dissoc-in @db [type] id))

