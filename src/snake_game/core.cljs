(ns snake-game.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :refer [dispatch dispatch-sync]]
   [snake-game.view :as view]
   [snake-game.handlers :as handlers]))

(defonce snake-moving
  (js/setInterval #(dispatch [:next-state]) 150))

(defn run
  "The main app function"
  []
  (dispatch-sync [:initialize])
  (rdom/render [view/game]
               (js/document.getElementById "app")))

(run)
