# course-bot

Course bot for performing laboratory works for "Computer Architecture" discipline.

## CSA course bot

- `/help` -- посмотреть справку по командам.
- `/listgroups` -- посмотреть списки групп с зарегистрированными студентами.
- `/quiz` -- начать тест; подтверждаете действие (yes/no), отвечаете цифрами на вопросы; по закрытии теста придёт информация о результатах.
- `/renameme` -- изменить своё имя в реестре.
- `/start` -- зарегистрироваться, укажите своё имя как в ведомости и свою группу.
- `/whoami` -- посмотреть информацию в реестре о себе (имя, группа, ID).
- Для лабораторной работы №1:
  - `/lab1agenda` -- расписание выступлений групп для лабораторной работы №1.
  - `/lab1schedule` -- выбрать день своего выступления.
  - `/lab1setgroup` -- укажите свою группу для лабораторной работы №1. Помните про виртуальную группу, сами её сменить вы не сможете.
  - `/lab1soon` -- список выступлений на ближайшее лабораторное занятие.
  - `/lab1submissions` -- список согласованных докладов.
  - `/lab1submit` -- вы сможете загрузить описание вашего доклада
  - после проверки (не менее суток) вам придут результаты
  - если отзыв -- улучшаете описание и загружаете повторно.
  - если подтверждение, то регистрируетесь на один из дней (`/lab1schedule`). Будьте внимательны, регистрация закрывается автоматически где-то за полчаса до занятия.
  - `/lab1feedback` -- сразу после занятия составьте свой рейтинг докладов. Приём закрывается автоматически.

### Run

Expect course config in `../edu-csa-internal`.

``` sh
clj -X course-bot.csa/run
```

### Deploy

``` sh
docker build -t csa-bot .
docker run --name csa-bot --restart=always -d -v $PWD/../edu-csa-internal:/edu-csa-internal -v $PWD/../csa-db:/csa-db csa-bot
```

## License

Copyright © 2022 Aleksandr Penskoi 

BSD 3-Clause "New" or "Revised" License
