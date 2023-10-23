(ns anafi-flightplan-utils.flightplan
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [geo.core :as geo]
            [clojure.spec.gen.alpha :as gen])
  (:gen-class))

(def image-resolution-jpeg-rect 12.58291244506836)
(def image-resolution-jpeg-wide 13.600000381469727)
(def image-resolution-dng 14)

; POI
(s/def :poi/latitude geo/latitude?)
(s/def :poi/longitude geo/longitude?)
(s/def :poi/altitude int?)
(s/def :poi/color int?)
(s/def ::poi-entry (s/keys :req-un [:poi/latitude :poi/longitude :poi/altitude :poi/color]))

; Actions
(s/def ::type #{"ImageStartCapture" "ImageStopCapture" "Panorama" "VideoStartCapture" "VideoStopCapture" "Tilt" "Delay"})
(s/def :image/period number?)
(s/def :image/resolution number?)
(s/def :image/nbOfPictures int?)
(s/def :tilt/angle int?)
(s/def :tilt/speed int?)
(s/def :delay/delay int?)
(s/def :video/cameraId int?)
(s/def :video/resolution number?)
(s/def :video/fps int?)
(s/def :panorama/speed int?)
(s/def :panorama/angle int?)

(defmulti actionmm :type)
(defmethod actionmm "ImageStartCapture" [_] (s/keys :req-un [::type :image/period :image/resolution :image/nbOfPictures]))
(defmethod actionmm "ImageStopCapture" [_] (s/keys :req-un [::type]))
(defmethod actionmm "Panorama" [_] (s/keys :req-un [::type :panorama/speed :panorama/angle]))
(defmethod actionmm "VideoStartCapture" [_] (s/keys :req-un [::type :video/cameraId :video/fps :video/resolution]))
(defmethod actionmm "VideoStopCapture" [_] (s/keys :req-un [::type]))
(defmethod actionmm "Tilt" [_] (s/keys :req-un [::type :tilt/angle :tilt/speed]))
(defmethod actionmm "Delay" [_] (s/keys :req-un [::type :delay/delay]))
(s/def ::actiontype (s/multi-spec actionmm ::type))

; Waypoints
(s/def :waypoint/latitude geo/latitude?)
(s/def :waypoint/longitude geo/longitude?)
(s/def :waypoint/altitude int?)
(s/def :waypoint/yaw number?)
(s/def :waypoint/speed int?)
(s/def :waypoint/continue boolean?)
(s/def :waypoint/followPOI boolean?)
(s/def :waypoint/follow int?)
(s/def :waypoint/lastYaw int?)
(s/def :waypoint/actions (s/coll-of ::actiontype))
(s/def ::waypoint-entry (s/keys :req-un [:waypoint/latitude :waypoint/longitude :waypoint/altitude :waypoint/yaw
                                        :waypoint/speed :waypoint/continue :waypoint/followPOI :waypoint/follow
                                        :waypoint/lastYaw]
                                :opt-un [:waypoint/actions]))

(s/def ::version int?)
(s/def ::name string?)
(s/def ::product string?)
(s/def ::productId int?)
(s/def ::uuid string?)
(s/def ::date int?)
(s/def ::progressive_course_activated boolean?)
(s/def ::dirty boolean?)
(s/def ::longitude double?)
(s/def ::latitude double?)
(s/def ::longitudeDelta double?)
(s/def ::latitudeDelta double?)
(s/def ::zoomLevel double?)
(s/def ::rotation int?)
(s/def ::tilt int?)
(s/def ::mapType int?)
(s/def ::takeoff (s/coll-of ::actiontype))
(s/def ::poi (s/coll-of ::poi-entry))
(s/def ::wayPoints (s/coll-of ::waypoint-entry))
(s/def ::plan (s/keys :req-un [::wayPoints] :opt-un [::takeoff ::poi]))
(s/def ::flightplan (s/keys :req-un [::version ::title ::product ::productId ::uuid ::date ::progressive_course_activated
                                     ::dirty ::longitude ::latitude ::longitudeDelta ::latitudeDelta ::zoomLevel ::rotation
                                     ::tilt ::mapType ::plan]))

