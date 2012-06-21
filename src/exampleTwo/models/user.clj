(ns exampleTwo.models.user
  (:import [java.security MessageDigest NoSuchAlgorithmException]
           [java.math BigInteger])
  (:require [exampleTwo.db :as db]))

(defn get-hash
  [#^String salt, #^String password]
  (let [alg (doto (MessageDigest/getInstance "SHA-256")
              (.reset)
              (.update (.getBytes salt))
              (.update (.getBytes password)))]
    (try
      (.toString (new BigInteger 1 (.digest alg)) 16)
      (catch NoSuchAlgorithmException e
        (throw (new RuntimeException e))))))

(def valid-characters
  (map char (concat (range 48 58)
                    (range 65 91)
                    (range 97 123))))

(defn get-salt
  []
  (apply str (take 32 (repeatedly #(rand-nth valid-characters)))))

(defn get-users
  []
  (-> (db/read "user")
      (keys)
      (sort)))

(defn get-user
  [username]
  (db/read "user" username))

(defn add-user
  "Add a new user to the database. The password will be prepended by
   an arbitrary salt."
  [username password]
  (when (not-any? #{username} (get-users))
    (let
        [salt          (get-salt)
         salted_hash   (get-hash salt password)]
      (db/create {:type "user"
                  :id username
                  :name username
                  :salt salt
                  :password salted_hash}))))

(defn remove-user
  "Remove the given user."
  [username]
  (db/delete "user" username))

(defn valid?
  "Verify that the given user's password matches."
  [username pwd]
  (let [{:keys [password salt] :as user} (get-user username)]
    (when (and user
               (= password (get-hash salt pwd)))
      user)))