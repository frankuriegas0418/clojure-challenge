(ns invoice-analysis
  (:require [clojure.edn :as edn]))

(defn find-items-satisfying-conditions [invoice]
  (->> invoice
       slurp
       edn/read-string
       :invoice/items
       (filter (fn [item]
                 (let [has-iva-19? (some #(and (= (:tax/category %) :iva)
                                               (= (:tax/rate %) 19))
                                         (:taxable/taxes item))
                       has-ret-fuente-1? (some #(and (= (:retention/category %) :ret_fuente)
                                                     (= (:retention/rate %) 1))
                                               (:retentionable/retentions item))]
                   (or (and has-iva-19? (not has-ret-fuente-1?))
                       (and has-ret-fuente-1? (not has-iva-19?))))))))
(comment
  (find-items-satisfying-conditions "invoice.edn"))
