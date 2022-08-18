(ns hello-world.core
  (:require [clojure.java.io :refer [copy file]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.util.response :refer [response]]))


;;(defn handler [request]
;;  {:status 200
;;   :headers {"Content-Type" "text/html"}
;;   :body "{\"test\": \"ddff\"}"})
;
;; 정적 html 반환
;;(defn handler [request]
;;  (response (slurp "resources/public/index.html")))
;
;;(defn handler
;;  "query-param / form-urlencoded 파라미터 처리"
;;  [{:keys [query-params form-params params] :as request}]
;;  (println request)
;;  (response (str {:query-params query-params
;;                  :form-params form-params
;;                  :params params})))
;
;{"test" {
; :filename "&#4361;&#4467;&#4367;&#4467;&#4357;&#4469;&#4523;&#4361;&#4451;&#4538; 2021-09-28 &#4363;&#4457;&#4364;&#4453;&#4523; 10.26.46.png",
; :content-type "image/png",
; :tempfile #object[java.io.File 0x678c62ea "/var/folders/kr/mm0nkz557j76d4jqs31_5yjm0000gn/T/ring-multipart-9128951106277269141.tmp"],
; :size 134562}}

(defn multipart
  "multipart 에 파일을 받았는지 체크"
  [params]
  (get-in params ["test" :tempfile]))

(defn temp-file->java-file
  "temp 파일을 java bean properties 로 반환한다. "
  [temp-file]
  (-> temp-file
    bean
    :path))

(defn handler
      [{params :params}]
      (println (str params) "😃")
      (let [temp-file (multipart params)]
        (if (not(nil? temp-file))
          (do (copy
                (file (temp-file->java-file temp-file))
                (file (str "/tmp/" (get-in params ["test" :filename])))))))
      (response ""))

;;(def reloadable-app
;;  (-> handler
;;      wrap-reload))
;
;; public 을 정적데이터 root 폴더로 설정한 미들웨어
;;(def reloadable-app
;;    (-> handler
;;        (wrap-resource "public")))
;
;;(def reloadable-app
;;  "query-param / form-urlencoded 파라미터 처리"
;;  (-> handler
;;      wrap-params
;;      wrap-session))
;
(def reloadable-app
  #_(-> handler
        (wrap-resource "public"))
  (-> handler
      wrap-params
      wrap-multipart-params
      (wrap-resource "public")))

(defn -main []
  (run-jetty #'reloadable-app {:port 3000
                               :join? false}))

;(defn wrap-content-type
;  [handler content-type]
;  (fn [request]
;    (let [response (handler request)]
;      (assoc-in response [:headers "Content-Type"] content-type))))
;
;(def app
;  (-> handler
;      (wrap-content-type "application/json")))