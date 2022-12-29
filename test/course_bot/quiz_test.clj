(ns course-bot.quiz-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str])
  (:require [codax.core :as codax])
  (:require [course-bot.general :as general]
            [course-bot.talk :as talk]
            [course-bot.quiz :as quiz]
            [course-bot.talk-test :as ttalk]
            [course-bot.misc :as misc]))

(defn start-user [*chat start-talk id name]
  (testing "register user"
    (start-talk id "/start")
    (start-talk id name)
    (start-talk id "gr1")
    (start-talk id "/start")
    (ttalk/in-history *chat [id "You are already registered. To change your information, contact the teacher and send /whoami"])))

(declare db *chat)

(talk/deftest startquiz-talk-test [db *chat]
  (let [conf (misc/get-config "conf-example")
        start-talk (ttalk/mock-talk general/start-talk db conf)
        startquiz-talk (ttalk/mock-talk quiz/startquiz-talk db conf)]
    (start-user *chat start-talk 1 "Bot Botovich")

    (startquiz-talk 1 "/startquiz")
    (ttalk/in-history *chat 1 "That action requires admin rights.")

    (startquiz-talk 0 "/startquiz missed-quiz")
    (ttalk/in-history *chat 0 "Quiz is not defined.")

    (startquiz-talk 0 "/startquiz")
    (ttalk/in-history *chat 0 (str/join "\n"
                                        '("Available tests:"
                                          "- test-quiz (Test quiz)"
                                          "- test-quiz-2 (Test quiz 2)")))

    (startquiz-talk 0 "/startquiz test-quiz")
    (ttalk/in-history *chat 0 "Are you sure to run 'Test quiz' quiz?")

    (startquiz-talk 0 "no")
    (ttalk/in-history *chat 0 "In a next time.")

    (startquiz-talk 0 "/startquiz test-quiz")
    (ttalk/in-history *chat 0 "Are you sure to run 'Test quiz' quiz?")

    (startquiz-talk 0 "yes")
    (ttalk/in-history *chat 0 "The quiz was started.")
    (is (= {:current :test-quiz} (codax/get-at! db [:quiz])))

    (startquiz-talk 0 "/startquiz test-quiz")
    (ttalk/in-history *chat 0 "Test is already running.")

    (startquiz-talk 0 "/startquiz")
    (ttalk/in-history *chat 0 "Test is already running.")))

(talk/deftest quiz-talk-test [db *chat]
  (let [conf (misc/get-config "conf-example")
        start-talk (ttalk/mock-talk general/start-talk db conf)
        startquiz-talk (ttalk/mock-talk quiz/startquiz-talk db conf)
        quiz-talk (ttalk/mock-talk quiz/quiz-talk db conf)]
    (start-user *chat start-talk 1 "Bot Botovich")

    (quiz-talk 1 "/quiz")
    (ttalk/in-history *chat 1 "Тест не запущен, дождитесь отмашки преподавателя.")

    (startquiz-talk 0 "/startquiz test-quiz")
    (startquiz-talk 0 "yes")
    (ttalk/in-history *chat 0 "The quiz was started.")

    (quiz-talk 1 "/quiz")
    (ttalk/in-history *chat 1 "Хотите начать тест 'Test quiz' (2 вопроса(-ов))?")

    (quiz-talk 1 "nooooooo")
    (ttalk/in-history *chat 1 "Что (yes/no)?")

    (quiz-talk 1 "no")
    (ttalk/in-history *chat 1 "Ваше право.")

    (quiz-talk 1 "/quiz")
    (ttalk/in-history *chat 1 "Хотите начать тест 'Test quiz' (2 вопроса(-ов))?")
    (quiz-talk 1 "yes")
    (ttalk/in-history *chat 1
                      "Отвечайте цифрой. Ваш первый вопрос:"
                      (str/join "\n"
                                '("Q1\n"
                                  "1. a1"
                                  "2. a2")))
    (quiz-talk 1 "first")
    (ttalk/in-history *chat 1 "Не понял, укажите корректный номер ответа (1, 2...).")

    (quiz-talk 1 "0")
    (ttalk/in-history *chat 1 "Не понял, укажите корректный номер ответа (1, 2...).")

    (quiz-talk 1 "3")
    (ttalk/in-history *chat 1 "Не понял, укажите корректный номер ответа (1, 2...).")

    (quiz-talk 1 "1")
    (ttalk/in-history *chat 1
                      "Запомнили ваш ответ: 1"
                      (str/join "\n"
                                '("Q2\n"
                                  "1. a3"
                                  "2. a4")))

    (quiz-talk 1 "2")
    (ttalk/in-history *chat
                      [1 "Спасибо, тест пройден. Результаты пришлю, когда тест будет закрыт."]
                      [0 "Quiz answers: 1, 2"])

    (is (= {:test-quiz {1 '("1" "2")}} (codax/get-at! db [:quiz :results])))))

(talk/deftest stopquiz-talk-test [db *chat]
  (let [conf (misc/get-config "conf-example")
        start-talk (ttalk/mock-talk general/start-talk db conf)
        startquiz-talk (ttalk/mock-talk quiz/startquiz-talk db conf)
        quiz-talk (ttalk/mock-talk quiz/quiz-talk db conf)
        stopquiz-talk (ttalk/mock-talk quiz/stopquiz-talk db conf)]
    (start-user *chat start-talk 1 "Bot Botovich")
    (startquiz-talk 0 "/startquiz test-quiz")
    (startquiz-talk 0 "yes")
    (ttalk/in-history *chat 0 "The quiz was started.")

    (quiz-talk 1 "/quiz")
    (ttalk/in-history *chat 1 "Хотите начать тест 'Test quiz' (2 вопроса(-ов))?")
    (quiz-talk 1 "yes")
    (quiz-talk 1 "1")
    (quiz-talk 1 "1")
    (ttalk/in-history *chat
                      [1 "Спасибо, тест пройден. Результаты пришлю, когда тест будет закрыт."]
                      [0 "Quiz answers: 1, 1"])

    (is (= {:test-quiz {1 '("1" "1")}} (codax/get-at! db [:quiz :results])))

    (stopquiz-talk 1 "/stopquiz")
    (ttalk/in-history *chat 1 "That action requires admin rights.")

    (stopquiz-talk 0 "/stopquiz")
    (ttalk/in-history *chat 0 "Are you sure to stop 'Test quiz' quiz?")

    (stopquiz-talk 0 "noooooooooooo")
    (ttalk/in-history *chat 0 "What?")

    (stopquiz-talk 0 "no")
    (ttalk/in-history *chat 0 "In a next time. The quiz is still in progress.")

    (stopquiz-talk 0 "/stopquiz")
    (ttalk/in-history *chat 0 "Are you sure to stop 'Test quiz' quiz?")

    (stopquiz-talk 0 "yes")
    (ttalk/in-history *chat
                      [0 "The quiz 'Test quiz' was stopped"]
                      [0
                       "Q1"
                       ""
                       "- [1] a1"
                       "- [0] CORRECT a2"]
                      [0
                       "Q2"
                       ""
                       "- [1] CORRECT a3"
                       "- [0] a4"]
                      [1 "Ваш результат: 1/2"])))
