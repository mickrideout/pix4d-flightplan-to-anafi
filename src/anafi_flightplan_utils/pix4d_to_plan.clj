(ns anafi-flightplan-utils.pix4d-to-plan
  (:require [clojure.string :as string]
            [anafi-flightplan-utils.flightplan :as fp]
            [anafi-flightplan-utils.share :as sh])
  (:gen-class))

(defn generate-anafi-action [pix4d-action cli-options]
  "Generate anafi action from pix4d action"
  (cond
    (= "BeginCapture" pix4d-action) {:type "ImageStartCapture" :period (:period cli-options) :resolution fp/image-resolution-jpeg-wide :nbOfPictures 0}
    (= "EndCapture" pix4d-action) {:type "ImageStopCapture"}
    (= "Tilt" pix4d-action) {:type "Tilt", :tilt (:tilt cli-options), :speed 180}
    ))

(defn bearing-to
  "Returns the (initial) bearing from `point-1` to the `point-2` in degrees."
  [lat1 long1 lat2 long2]
  (if (not (every? true? (map some? [lat1 long1 lat2 long2])))
    (throw (IllegalArgumentException. (str "An argument was null: " lat1 " " long1 " " lat2 " " long2))))
  (mod (- 520
          (int
            (*
              (Math/atan2
                (- lat2 lat1)
                (- long2 long1))
              (/ 180 3.14159))))
    360))

(defn determine-heading [anafi-point1 anafi-point2]
  "Determine heading between two anafi waypoints"
  (if (nil? anafi-point2)
    0.0
    (bearing-to (:latitude anafi-point1) (:longitude anafi-point1) (:latitude anafi-point2) (:longitude anafi-point2))))

(defn generate-anafi-waypoint [pix4d-waypoint cli-options]
  "Generate anafi waypoint from pix4d waypoint"
  (if (nil? pix4d-waypoint)
    nil
    (let [[longitude latitude altitude] (:location pix4d-waypoint)
          [flag] (:flags pix4d-waypoint)]
      {:latitude latitude
       :longitude longitude
       :altitude (int altitude)
       :speed (:speed cli-options)
       :continue true
       :followPOI false
       :follow 1
       :lastYaw 0
       :actions [(generate-anafi-action flag cli-options)]
       })))

(defn generate-pix4d-home-waypoint [cli-options]
  "Generate home waypoint in pix4d format"
  (generate-anafi-waypoint
    {
     :location [(:homeLongitude cli-options) (:homeLatitude cli-options) (:homeAltitude cli-options)]
     }
    cli-options))

(defn generate-anafi-waypoint-tuple [tuple cli-options]
  "Given a tuple of pix4d points, return a tuple of anafi waypoints"
  (let [pixpoint1 (first tuple)
        pixpoint2 (second tuple)]
    (if (nil? pixpoint2)
      [(conj (generate-anafi-waypoint pixpoint1 cli-options) {:yaw 0.0})]
      (let [anafi-point1 (generate-anafi-waypoint pixpoint1 cli-options)
            anafi-point2 (generate-anafi-waypoint pixpoint2 cli-options)
            heading (determine-heading anafi-point1 anafi-point2)]
        [(conj anafi-point1 {:yaw heading})
         (conj anafi-point2 {:yaw heading})]))))

(defn generate-anafi-waypoints [pix4d-waypoints cli-options]
  "Generate all waypoints"
  (let [waypoints (into [] (flatten (map #(generate-anafi-waypoint-tuple % cli-options) (sh/tuples pix4d-waypoints))))
        home-waypoint (generate-pix4d-home-waypoint cli-options)
        bearing-from-home (determine-heading home-waypoint (first waypoints))
        bearing-to-home (determine-heading (last waypoints) home-waypoint)]
        (flatten [(conj home-waypoint {:yaw bearing-from-home :actions [{:type "ImageStopCapture"} {:type "Tilt" :angle (:tilt cli-options) :speed 180}]})
                  waypoints
                  (conj home-waypoint {:yaw bearing-to-home :actions [{:type "Tilt" :angle 0 :speed 180}]})])))

(defn assoc-if-not-set [m & keyvals]
  (reduce (fn [result [key value]]
            (if (not (contains? m key))
              (assoc result key value)
              result))
          m
          (partition 2 keyvals)))

(defn generate-flightplan-body [pix4d-plan cli-options]
  "Generate anafi flight plan from pix4d plan"
  (let [[home-lat home-long] (:homeCoordinate pix4d-plan)
        override-options (assoc-if-not-set cli-options :homeLatitude home-lat :homeLongitude home-long)
        ]
    {:version 1
     :title (:title cli-options)
     :product "ANAFI_4K"
     :productId 2324
     :uuid (:title cli-options)
     :date (.getTime (java.util.Date.))
     :progressive_course_activated true
     :dirty false
     :longitude home-long
     :latitude home-lat
     :longitudeDelta 0.0
     :latitudeDelta 0.0
     :zoomLevel 19.48026466369629
     :rotation 0
     :tilt 0
     :mapType 4
     :plan {:takeoff []
            :poi []
            :wayPoints (generate-anafi-waypoints (-> pix4d-plan :flightPlan :waypoints) override-options)}}))