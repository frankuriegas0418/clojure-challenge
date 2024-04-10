(ns invoice-item-test
  (:require [clojure.test :refer [deftest is testing]]
            [invoice-item :refer [subtotal]]))

(deftest subtotal-test
  (testing "Subtotal calculation"
    (is (= 100.0 (subtotal {:invoice-item/precise-quantity 10
                            :invoice-item/precise-price    10})))
    (is (= 90.0 (subtotal {:invoice-item/precise-quantity 10
                           :invoice-item/precise-price    10
                           :invoice-item/discount-rate    10})))
    (is (= 0.0 (subtotal {:invoice-item/precise-quantity 10
                          :invoice-item/precise-price    0})))
    (is (= 0.0 (subtotal {:invoice-item/precise-quantity 0
                          :invoice-item/precise-price    10})))
    (is (= 0.0 (subtotal {:invoice-item/precise-quantity 0
                          :invoice-item/precise-price 10
                          :invoice-item/discount-rate 10})))
    (is (= 0.0 (subtotal {:invoice-item/precise-quantity 10
                          :invoice-item/precise-price 10
                          :invoice-item/discount-rate 100})))
    (is (= -70.0 (subtotal {:invoice-item/precise-quantity -10
                            :invoice-item/precise-price 10
                            :invoice-item/discount-rate 30})))))


