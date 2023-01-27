(ns anafi-flightplan-utils.core
  (:require [clojure.tools.cli :as cli]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [clojure.data.json :as json]
            [anafi-flightplan-utils.flightplan :as fp]
            [anafi-flightplan-utils.pix4d-to-plan :as pix])
  (:gen-class))

(def cli-options
  [
   ["-i" "--input FILE" "Input file"]
   ["-o" "--output FILE" "Output file"]
   ["-s" "--speed SPEED" "Speed m/s" :default 5 :parse-fn #(Integer/parseInt %)]
   ["-p" "--period SECS" "Image capture period secs" :default 2 :parse-fn #(Integer/parseInt %)]
   ["-t" "--title TITLE"  "Title of the flightplan" :default (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (java.util.Date.))]
   ["-x" "--homeLatitude LAT" "latitude to return to" :parse-fn #(Double/parseDouble %)]
   ["-y" "--homeLongitude LONG" "longitude to return to" :parse-fn #(Double/parseDouble %)]
   ["-a" "--homeAltitude ALT" "Altitude to return to for home" :default 50 :parse-fn #(Integer/parseInt %)]
   ["-h" "--help" "Help"]
   ])

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn usage [options-summary]
  (->> ["Anafi flightplan utils."
        ""
        "Usage: anafi-flightplan-utils [options]"
        ""
        "Options:"
        options-summary
        ]
       (string/join \newline)))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with a error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
        {:exit-message (usage summary) :ok? true}
      errors ; errors => exit with description of errors
        {:exit-message (error-msg errors)}
      :else
        {:options options})))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn pix4d-to-plan [options]
  (let [cli-options (first options)
        plan (pix/generate-flightplan-body (json/read-str (slurp (:input cli-options)) :key-fn keyword) cli-options)]
    (if (s/valid? ::fp/flightplan plan)
      (do
        (spit (:output cli-options) (with-out-str (json/pprint plan)))
        (println (str "Flightplan written to " (:output cli-options))))
      (do
        (println (str "Invalid flightplan: \n" (s/explain ::fp/flightplan plan)))
        (println (str "Invalidate flightplan was: " plan))))))

(defn -main
  "Anafi-flightplan-utils"
  [& args]
  (let [{:keys [options exit-message]} (validate-args args)]
    (if (not(nil? exit-message))
      (exit 1 exit-message)
      (pix4d-to-plan [options]))))
