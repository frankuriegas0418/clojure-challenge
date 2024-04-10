(ns invoice-generator
  (:require [clojure.data.json :as json]
            [clojure.pprint :as pprint]
            [clojure.spec.alpha :as s]
            [clj-time.format :as fmt]
            [clj-time.coerce :as coerce]
            [invoice-spec :as invoice-spec]))

(defn parse-date-to-instant [date-string]
  (-> (fmt/formatter "dd/MM/yyyy")
      (fmt/parse date-string)
      (coerce/to-date)))

(defn json-to-invoice [file-name]
  (let [json-data (json/read-str (slurp file-name) :key-fn keyword)
        issue-date (parse-date-to-instant (get-in json-data [:invoice :issue_date]))
        customer (-> (get-in json-data [:invoice :customer])
                     (update-keys (fn [k] (case k
                                            :company_name :customer/name
                                            :email :customer/email
                                            k))))
        items (get-in json-data [:invoice :items])

        format-taxes (fn [item] (mapv (fn [tax]
                                        {:tax/category (keyword (clojure.string/lower-case (:tax_category tax)))
                                         :tax/rate (double (:tax_rate tax))})
                                      (:taxes item)))

        invoice (-> json-data
                    (assoc :invoice/issue-date issue-date)
                    (assoc  :invoice/customer customer)
                    (assoc :invoice/items (mapv  (fn [item]
                                                   {:invoice-item/price (:price item)
                                                    :invoice-item/quantity (:quantity item)
                                                    :invoice-item/sku (:sku item)
                                                    :invoice-item/taxes (format-taxes item)})
                                                 items)))]
    (if (s/valid? ::invoice-spec/invoice invoice)
      invoice
      ((pprint/pprint invoice)
       (throw (Exception. (s/explain-str ::invoice-spec/invoice invoice)))))))

(comment
  (json-to-invoice "invoice.json"))
