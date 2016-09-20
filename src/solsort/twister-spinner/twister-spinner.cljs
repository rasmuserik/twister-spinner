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
(defn pick-random! []
  (db! [:choice] [(rand-nth limbs) (rand-nth colors)]))
(pick-random!)
(defonce init
  (js/setInterval pick-random! 5000))
(defn main []
  [:div
   {:style {:text-align :center}}
   [limb :left-hand]
   [limb :right-hand]
   [:br]
   [limb :left-foot]
   [limb :right-foot]
   ]
  )
(render [:div [main]])
