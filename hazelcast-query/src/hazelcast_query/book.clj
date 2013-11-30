(ns hazelcast-query.book
  (:import (java.io Serializable)
           (java.util Map)))

(gen-class :name hazelcast-query.book.Book
           :implements [java.io.Serializable]
           :state state
           :init init
           :constructors {[java.util.Map] []}
           :methods [[getIsbn13 [] String]
                     [getName [] String]
                     [getPrice [] int]
                     [getPublishDate [] String]
                     [getCategory [] String]
                     [isOutOfPrint [] boolean]])

(defn -init [underlying]
  [[] underlying])

(defn -getIsbn13 [this]
  (:isbn13 (. this state)))

(defn -getName [this]
  (:name (. this state)))

(defn -getPrice [this]
  ;; (println "price called")
  (:price (. this state)))

(defn -getPublishDate [this]
  (:publish-date (. this state)))

(defn -getCategory [this]
  (:category (. this state)))

(defn -isOutOfPrint [this]
  (:out-of-print (. this state)))

(defn -toString [this]
  (str (. this state)))