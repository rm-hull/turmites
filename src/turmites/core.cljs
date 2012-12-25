(ns turmites.core
  (:use [monet.canvas :only [get-context get-pixel rect fill-style]]
        [jayq.core :only [$ document-ready data attr hide bind]]))

(def spinner ($ :div#spinner))

(def offsets
  { :north [0 -1]
    :east  [1  0]
    :south [0  1]
    :west  [-1 0] })

(defn turn-right [direction]
  (case direction
    :north :east 
    :east  :south 
    :south :west
    :west  :north))

(defn turn-left [direction]
  (case direction
    :north :west 
    :east  :north 
    :south :east
    :west  :south))

(defn get-color [ctx [x y] scale]
  (get-pixel ctx (* x scale) (* y scale)))

(defn set-color! [ctx [x y] scale color]
  (-> ctx
    (fill-style color)
    (rect { :x (* x scale) :y (* y scale) :w scale :h scale})))

(def black? 
  (memoize
    (fn [color]
      (->> 
        [:red :green :blue]
        (map color)
        (reduce +)
        zero?))))

(defn toggle-previous! [state]
  (let [prev (:previous state)
        color (if (black? (:color prev)) "white" "black")]
    (set-color! (:ctx state) (:position prev) (:cell-size state) color)))

(defn draw! [state color]
  (let [ curr (:current state)]
    (set-color! (:ctx state) (:position curr) (:cell-size state) color)))

(defn bounds [n limit]
  (cond 
    (< n 0)      (dec limit)
    (>= n limit) 0
    :else n))

(defn wrap [[x y] [w h]]
  [(bounds x w) (bounds y h)])

(defn next-direction [state]
  (if (black? (get-in state [:current :color]))
    (turn-left  (:direction state))
    (turn-right (:direction state)))) 

(defn next-state [state]
  (let [dir   (next-direction state)
        pos   (wrap 
                (map + (get-in state [:current :position]) (dir offsets))
                (:bounds state))
        color (get-color (:ctx state) pos (:cell-size state))]
   (assoc state
     :generation (inc (:generation state))
     :direction dir
     :previous  (:current state)
     :current   {:position pos :color color})))

(defn available-area []
  (let [div (first ($ :div#wrapper))]
    [ (.-offsetWidth div) (.-offsetHeight div) ]))


(defn animate [turmite]
  (letfn [(loop []
            (. js/window (requestAnimFrame loop))
            (reset! turmite (next-state @ant))
            ;(.log js/console (:generation @ant))
            (toggle-previous! @ant)
            ;(draw! @ant "red")
            )]
    (loop)))

(defn create-ant [ctx pos bounds cell-size]
  (atom
    {
      :generation 0
      :ctx ctx
      :cell-size cell-size
      :bounds bounds
      :direction (get [:north :east :south :west] (rand-int 4))
      :current { :position pos :color (get-color ctx pos cell-size)}
      :previous nil
     }))

(defn coords [event scale] 
  [ (quot (.-clientX event) scale)
    (quot (.-clientY event) scale) ])

(document-ready
  (fn []
    (let [$canvas ($ :canvas#world) 
          ctx (get-context (.get $canvas 0) "2d") 
          cell-size 3
          [width height] (map #(quot % cell-size) (available-area))]
      
      (-> $canvas
        (attr :width  (+ 2 (* cell-size width)))
        (attr :height (+ 2 (* cell-size height))))

      (animate (create-ant ctx [100 30] [width height] cell-size))

      (bind $canvas :click
        (fn [event]
          (.preventDefault event)
          (animate (create-ant ctx (coords event cell-size) [width height] cell-size))))
      )))
