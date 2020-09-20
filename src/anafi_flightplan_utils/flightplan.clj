(ns anafi-flightplan-utils.flight-plan
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.spec.gen.alpha :as gen])
  (:gen-class))

;;;
;"version": 1,
;"title": "allnotclosed",
;"product": "ANAFI_4K",
;"productId": 2324,
;"uuid": "allnotclosed",
;"date": 1598991976187,
;"progressive_course_activated": true,
;"dirty": false,
;"longitude": -0.09031500667333603,
;"latitude": 51.48512735338364,
;"longitudeDelta": 0.0016827508807182312,
;"latitudeDelta": 2.2986942239811015E-4,
;"zoomLevel": 19.48026466369629,
;"rotation": 0,
;"tilt": 0,
;"mapType": 4,
;"plan": {
(s/def ::version int?)
(s/def ::title string?)
(s/def ::product string?)
(s/def ::productId int?)
(s/def ::uuid uuid?)
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
(s/def ::plan map?)
(s/def ::flightplan (s/keys :req-un [::verison ::title ::product ::productId ::uuid ::date ::progressive_course_activated
                                     ::dirty ::longitude ::latitude ::longitudeDelta ::latitudeDelta ::zoomLevel ::rotation
                                     ::tilt ::mapType ::plan]))


