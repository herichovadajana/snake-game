(ns snake-game.utils)

(def key-code->move
  "Mapping from the integer key code to the direction vector corresponding to that key"
  {38 [0 -1]
   40 [0 1]
   39 [1 0]
   37 [-1 0]})

(defn rand-free-position
  "This function takes the snake and the board-size as arguments,                                                                                             
  and returns a random position not colliding with the snake body"
  [snake [x y]]
  (let [snake-positions-set (into #{} (:body snake))
        board-positions (for [x-pos (range x)
                              y-pos (range y)]
                          [x-pos y-pos])]
    (when-let [free-positions (seq (remove snake-positions-set board-positions))]
      (rand-nth free-positions))))

(defn collisions
  "Returns true if the snake collision with the board edges or itself (the snake body) is detected"
  [snake board]
  (let [{:keys [body direction]} snake
        [x y] board
        border-x #{x -1}
        border-y #{y -1}
        future-x (+ (first direction) (ffirst body))
        future-y (+ (second direction) (second (first body)))]
    (or (contains? border-x future-x)
        (contains? border-y future-y)
        (contains? (into #{} (rest body)) [future-x future-y]))))

(defn change-snake-direction
  "Changes the snake head direction, only when it's perpendicular to the old head direction"
  [[new-x new-y] [x y]]
  (if (or (= x new-x)
          (= y new-y))
    [x y]
    [new-x new-y]))

(defn move-snake
  "Move the whole snake based on positions and directions of each snake body segments"
  [{:keys [direction body] :as snake}]
  (let [head-new-position (mapv + direction (first body))]
    (update-in snake [:body] #(into [] (drop-last (cons head-new-position body))))))

(defn snake-tail [coordinate-1 coordinate-2]
  "Computes x or y tail coordinate according to the last 2 values of that coordinate"
  (if (= coordinate-1 coordinate-2)
    coordinate-1
    (if (> coordinate-1 coordinate-2)
      (dec coordinate-2)
      (inc coordinate-2))))

(defn grow-snake
  "Append a new tail body segment to the snake"
  [{:keys [body direction] :as snake}]
  (let [[[first-x first-y] [sec-x sec-y]] (take-last 2 body)
        x (snake-tail first-x sec-x)
        y (snake-tail first-y sec-y)]
    (update-in snake [:body] #(conj % [x y]))))

(defn process-move
  "Evaluates the new snake position in a context of the whole game"
  [{:keys [snake point board] :as db}]
  (if (= point (first (:body snake)))
    (-> db
        (update-in [:snake] grow-snake)
        (update-in [:points] inc)
        (assoc :point (rand-free-position snake board)))
    db))
