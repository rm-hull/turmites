(ns turmites.core
  (:use [monet.canvas :only [get-context get-pixel rect fill-style]]
        [monet.core :only [animation-frame]]
        [jayq.core :only [$ document-ready data attr hide bind prevent]]
        [jayq.util :only [log]]
        [clojure.string :only [split]]))

(def offsets 
  { :north [0 -1]
    :east  [1  0]
    :south [0  1]
    :west  [-1 0] })

(def directions
  { :north { 1 :north 2 :east  4 :south 8 :west  }
    :east  { 1 :east  2 :south 4 :west  8 :north }
    :south { 1 :south 2 :west  4 :north 8 :east  }
    :west  { 1 :west  2 :north 4 :east  8 :south }})

(def predef-rules
  { :langtons-ant "120080"
    :fibonacci "181181121010"
    :binary-counter "120010"
    :filled-triangle "081121110111"
    :box-spiral "011081120011"
    :striped-spiral "021080181020"
    :stepped-pyramid "021080181110"
    :contoured-island "021011121180"
    :woven-placemat "021021110021"
    :snowflake-ish "021121181180"
    :slow-city-builder "180011080081"
    :computer-art "180121020081"
    :ballon-bursting "180121021180"
    :horizontal-highway "181080110010"
;"{{{1, 8, 1}, {0, 8, 0}}, {{1, 2, 1}, {1, 2, 0}}}", # 14: makes a 45 degree highway
;"{{{1, 8, 1}, {0, 8, 1}}, {{1, 2, 1}, {0, 8, 0}}}", # 15: makes a 45 degree highway
    :filled-spiral "181010110120"
    :glaciers "181020080080"
    :fizzy-spill "181120080080"
    :nested-cabinets "181121110011"
    :cross "111081120111"
    :saw "111010020180"
;"{{{1, 1, 1}, {0, 1, 1}}, {{1, 2, 1}, {0, 1, 0}}}", # 23: curves in blocks growth
    :textured "111020080080"
    :diamond "111021180120"
    :coiled-rope "111180121010"
;"{{{1, 2, 0}, {0, 8, 1}}, {{1, 8, 0}, {0, 1, 1}}}", # 27: (growth)
    :square-spiral "120081180021"
    :loopy "120121010011"
    :square-ant "121081110010"
;"{{{1, 2, 1}, {0, 2, 0}}, {{0, 8, 1}, {1, 8, 0}}}", # 31: growth with curves and blocks
    :distracted-spiral "121020010121"
    :cauliflower-stalk "121021110111"
    :worm-trails "121181121020"
;"{{{1, 2, 1}, {1, 1, 0}}, {{1, 1, 0}, {0, 1, 1}}}", # 35: eventually makes a two-way highway!
    :mould-bloom "121120010010"
;"{{{1, 2, 1}, {1, 2, 0}}, {{0, 2, 0}, {1, 1, 1}}}", # 37: makes a 1 in 2 gradient highway
;"{{{1, 2, 1}, {1, 2, 1}}, {{1, 8, 1}, {0, 2, 0}}}", # 38: immediately makes a 1 in 3 highway
    :square-diagonal "021121082080122180"
;"{{{1, 8, 1}, {0, 1, 0}}, {{0, 2, 2}, {1, 8, 0}}, {{1, 2, 1}, {1, 1, 0}}}", # 40: streak at approx. an 8.1 in 1 gradient
;"{{{1, 8, 1}, {0, 1, 2}}, {{0, 2, 2}, {1, 1, 1}}, {{1, 2, 1}, {1, 1, 0}}}", # 41: streak at approx. a 1.14 in 1 gradient
    :maze "181181110012081111"
    :cornices "182020180020080081"
;"{{{1, 2, 0}, {0, 2, 2}}, {{0, 8, 0}, {0, 2, 0}}, {{0, 1, 1}, {1, 8, 0}}}", # 44: makes a 1 in 7 highway
;"{{{1, 2, 1}, {0, 8, 0}}, {{1, 2, 2}, {0, 1, 0}}, {{1, 8, 0}, {0, 8, 0}}}", # 45: makes a 4 in 1 highway
   })

(defn- pair [s]
  (let [[k v] (split s #"=")]
    [(keyword k) v]))

(defn get-params [s] 
  (let [drop1 (apply str (drop 1 (seq s)))]
  (->> 
    (split drop1 #"&")
    (map pair)
    (into {})))) 

(defn parse-rules [s]
  (->>
    (seq s)
    (map int) ; not quite compatible with clojure!?!
    (partition 3)
    (partition 2)
    (map vec)
    vec))

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

(defn toggle-previous! [turmite]
  (let [prev (:previous turmite)
        color (if (= 0 (:color turmite)) "black" "white")]
    (set-color! (:ctx turmite) (:position prev) (:cell-size turmite) color)))

(defn draw! [turmite color]
  (let [ curr (:current turmite)]
    (set-color! (:ctx turmite) (:position curr) (:cell-size turmite) color)))

(defn bounds [n limit]
  (cond 
    (< n 0)      (dec limit)
    (>= n limit) 0
    :else n))

(defn wrap [[x y] [w h]]
  [(bounds x w) (bounds y h)])

(defn next-triple [turmite]
  (let [state (get-in turmite [:current :state])
        pos   (get-in turmite [:current :position])
        color (if (black? (get-color (:ctx turmite) pos (:cell-size turmite))) 0 1)]
    (get-in turmite [:rule state color])))

(defn relative-direction [current nextdir]
  (get-in directions [current nextdir]))

(defn next-state [turmite]
  (let [[color dir state] (next-triple turmite)
        new-direction (relative-direction (:direction turmite) dir)
        pos (wrap 
              (map + (get-in turmite [:current :position]) (new-direction offsets))
              (:bounds turmite))]
   (assoc turmite
     :generation (inc (:generation turmite))
     :direction new-direction
     :color     color
     :previous  (:current turmite)
     :current   {:position pos :color color :state state})))

(defn available-area []
  (let [div (first ($ :div#wrapper))]
    [ (.-offsetWidth div) (.-offsetHeight div) ]))

(defn animate [turmite]
  (letfn [(loop []
            (animation-frame loop)
            (reset! turmite (next-state @turmite))
            (toggle-previous! @turmite))]
    (log (pr-str @turmite))
    (loop)))

(defn create-turmite [ctx pos bounds cell-size rule]
  (atom
    {
      :generation 0
      :ctx ctx
      :cell-size cell-size
      :bounds bounds
      :direction (get [:north :east :south :west] (rand-int 4))
      :current { :position pos :color (get-color ctx pos cell-size) :state 0}
      :previous nil
      :rule rule
     }))

(defn coords [event scale] 
  [ (quot (.-clientX event) scale)
    (quot (.-clientY event) scale) ])

(defn get-rule [query-string]
  (let [data (:rule (get-params query-string))
        rule (get predef-rules (keyword data) data)]
    (parse-rules 
      (if (nil? data)
        (nth (vals predef-rules) (rand-int (count predef-rules)))
        rule))))

(document-ready
  (fn []
    (let [$canvas ($ :canvas#world) 
          ctx (get-context (aget $canvas 0) "2d") 
          cell-size 3
          [width height] (map #(quot % cell-size) (available-area))
          rule (get-rule (.-search (.-location js/window)))]
      
      (-> $canvas
        (attr :width  (+ 2 (* cell-size width)))
        (attr :height (+ 2 (* cell-size height))))

      (bind $canvas :click
        (fn [event]
          (prevent event)
          (animate (create-turmite ctx (coords event cell-size) [width height] cell-size rule))))
    
      (animate (create-turmite ctx [100 60] [width height] cell-size rule)))))
