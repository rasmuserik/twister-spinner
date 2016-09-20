(ns solsort.twister-spinner.twister-spinner
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop alt!]]
   [reagent.ratom :as ratom :refer  [reaction]])
  (:require
   [cljs.reader]
   [solsort.toolbox.setup]
   [solsort.toolbox.appdb :refer [db db! db-async!]]
   [solsort.toolbox.ui :refer [input select]]
   [solsort.util
    :refer
    [<ajax <seq<! js-seq load-style! put!close!
     parse-json-or-nil log page-ready render dom->clj]]
   [reagent.core :as reagent :refer []]
   [clojure.string :as string :refer [replace split blank?]]
   [cljs.core.async :refer [>! <! chan put! take! timeout close! pipe]]))

(def colors [:red :green :yellow :blue])
(def limbs [:left-hand :right-hand :left-foot :right-foot])
(db! [:sounds]
  {:english
   {:right-hand ["right" "hand"]
    :left-hand ["left" "hand"]
    :right-foot ["right" "foot"]
    :left-foot ["left" "foot"]
    :red "red"
    :green "green"
    :yellow "yellow"
    :blue "blue"
    :on "on"
    }
   :danish
   {:right-hand ["hojre" "haand"]
    :left-hand ["venstre" "haand"]
    :right-foot ["hojre" "fod"]
    :left-foot ["venstre" "fod"]
    :red "rod"
    :green "gron"
    :yellow "gul"
    :blue "blaa"
    :on "paa"
    }
   :silent
   {:right-hand []
    :left-hand []
    :right-foot []
    :left-foot []
    :red "silence"
    :green "silence"
    :blue "silence"
    :yellow "silence"
    :on "silence"}})

(db! [:language] :silent)
(db! [:interval] 5000)
(defn limb [o]
  (let [size (* 0.45 (min js/innerHeight js/innerWidth))]
   [:div
    {:style
     {:display :inline-block
      :width size
      :height size
      :border-radius (* .5 size)
      :background (get (apply hash-map (db [:choice])) o :white) 
      }}
    [:img
     {:style
      {:max-height "100%"
       :max-width "100%"}
      :src (str "assets/" (name o) ".png")}]]))
(defn get-sound [x]
  (db [:sounds (db [:language]) x]))

(defn play-sounds [s]
  (when-not (empty? s)
    (let [a (js/Audio. (str "assets/" (first s) ".mp3"))]
      (aset a "onended" #(play-sounds (rest s)))
      (.play a))))

(defn pick-random! []
  (let [t (js/Date.now)]
    (when (pos? (- t (db [:prev-time] 0) (db [:interval])))
      (db! [:prev-time] t)
      (go
        (loop [n 0.01]
          (db! [:choice] [(rand-nth limbs) (rand-nth colors)])
          (<! (timeout (* n 10)))
          (when (< n 12)
            (recur (* n 1.2))))
        (db! [:sound] (concat (get-sound (first (db [:choice]))) [(get-sound :on) (get-sound (second (db [:choice])))]))
        (play-sounds (db [:sound])))))
  )


(pick-random!)
(defonce init
  (js/setInterval #(pick-random!) 100))
(defn main []
  [:div
   {:style {:text-align :center}}
   [:div.ui.container
    [:div.ui.input
    [input {:type :select
            :db [:interval]
            :style {:background :transparent
                    :margin "1ex"
                    :padding "1ex"
                    :border-radius "1ex"}
            :options {"3 sec." 3000
                      "5 sec." 5000
                      "8 sec." 8000
                      "12 sec." 12000
                      "17 sec." 17000
                      "25 sec." 25000
                      "35 sec." 35000
                      "50 sec." 50000
                      }}]
    [input {:type :select
            :style {:background :transparent
                    :margin "1ex"
                    :padding "1ex"
                    :border-radius "1ex"}
            :db [:language]
           :options {"Silent" :silent
                     "English" :english
                     "Danish" :danish
                     }}]]
]
   [:br]
   [limb :left-hand]
   [limb :right-hand]
   [:br]
   [limb :left-foot]
   [limb :right-foot]
   ]
  )
(render [:div [main]])
