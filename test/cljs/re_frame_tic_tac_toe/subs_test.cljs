(ns re-frame-tic-tac-toe.subs-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [re-frame-tic-tac-toe.subs :as subs]))

(deftest n+side->coords-test
  (testing "returns the expected coordinates"
    (let [n 9
          side 3
          expected [[0 0] [0 1] [0 2]
                    [1 0] [1 1] [1 2]
                    [2 0] [2 1] [2 2]]]
      (is (= expected (subs/n+side->coords [n side]))))))
