(ns snake-game.handlers
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [reg-event-db
                                   reg-sub
                                   dispatch]]
            [goog.events :as events]
            [snake-game.utils :as utils]))


(def board [35 25])

(def snake {:direction [1 0]
            :body [[3 2] [2 2] [1 2] [0 2]]})

(def initial-state {:board board
                    :snake snake
                    :point (utils/rand-free-position snake board)
                    :points 0
                    :game-running? true})

(reg-event-db                      ;; setup initial state
 :initialize                       ;; usage (dispatch [:initialize])
 (fn
   [db _]                          ;; db is the current state
   (merge db initial-state)))      ;; what it returns becomes the new @db state

(reg-event-db
 :next-state
 (fn
   [{:keys [snake board] :as db} _]
   (if (:game-running? db)
     (if (utils/collisions snake board)
       (assoc-in db [:game-running?] false)
       (-> db
           (update-in [:snake] utils/move-snake)
           (as-> after-move
               (utils/process-move after-move))))
     db)))

(reg-event-db
 :change-direction
 (fn [db [_ new-direction]]
   (update-in db [:snake :direction]
              (partial utils/change-snake-direction new-direction))))

;;Register global event listener for keydown event.
;;Processes key strokes according to `utils/key-code->move` mapping
(defonce key-handler
  (events/listen js/window "keydown"
                 (fn [e]
                   (let [key-code (.-keyCode e)]
                     (when (contains? utils/key-code->move key-code)
                       (dispatch [:change-direction (utils/key-code->move key-code)]))))))

(reg-sub
 :board
 (fn
   [db _]             ;; db is the app-db atom
   (:board db)))      ;; the computation

(reg-sub
 :snake
 (fn
   [db _]
   (:body (:snake db))))

(reg-sub
 :point
 (fn
   [db _]
   (:point db)))


(reg-sub
 :points
 (fn
   [db _]
   (:points db)))

(reg-sub
 :game-running?
 (fn
   [db _]
   (:game-running? db)))
